package sojson.springsecurity.freemarker.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import sojson.springsecurity.freemarker.core.statics.Constants;
import sojson.springsecurity.freemarker.user.bo.UserOnlineBo;

@Component
public class SessionUtils {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	String SESSION_ID_PREFIX;

	@Value("${spring.session.redis.namespace:spring:session}")
	public void setSessionIdPrefix(String namespace) {
		this.SESSION_ID_PREFIX = namespace + ":sessions:";
	}

	public Long getUserId() {
		UserOnlineBo userOnlineBo = (UserOnlineBo) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return userOnlineBo.getId();
	}

	public String getEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/**
	 * 清空redis缓存，再把session id拿掉，只留下remember me
	 * id。接着访问“在线用户”页面，也就是请求该方法，页面在线用户列表显示为空。这不符合期望，实际上应该有一个在线用户。说明该方法返回的list
	 * size为0，redis中没有session。debug证明了这点，并且发现session总是在方法返回之后才在redis中生成
	 *
	 * @return
	 */
	public List<UserOnlineBo> getAllUsers() {

		List<UserOnlineBo> userOnlineBos = new ArrayList<>();

		Set<String> keys = redisTemplate.keys(SESSION_ID_PREFIX + "*");
		for (String key : keys) {

			if (key.indexOf(":expires:") != -1) {
				continue;
			}

			Map<Object, Object> sesssionMap = redisTemplate.opsForHash().entries(key);
			SecurityContext securityContext = (SecurityContext) sesssionMap.get("sessionAttr:SPRING_SECURITY_CONTEXT");
			if (null == securityContext) {
				continue;
			}

			UserOnlineBo userOnlineBo = (UserOnlineBo) securityContext.getAuthentication().getPrincipal();

			WebAuthenticationDetails details = (WebAuthenticationDetails) securityContext.getAuthentication()
					.getDetails();
			userOnlineBo.setHost(details.getRemoteAddress());
			userOnlineBo.setLastAccess(new Date((Long) sesssionMap.get("lastAccessedTime")));
			userOnlineBo.setSessionId(key.replace(SESSION_ID_PREFIX, ""));
//			userOnlineBo.setLastLoginTime(new Date((Long) sesssionMap.get("lastAccessedTime"))); // 自带，再设多余
			userOnlineBo.setTimeout((Integer) sesssionMap.get("maxInactiveInterval"));
			userOnlineBo.setStartTime(new Date((Long) sesssionMap.get("creationTime")));
			userOnlineBo.setSessionStatus((Boolean) sesssionMap.get("sessionAttr:" + Constants.ONLINE_STATUS));

			userOnlineBos.add(userOnlineBo);
		}

		return userOnlineBos;
	}

	public UserOnlineBo getSession(String sessionId) {

		Map<Object, Object> sesssionMap = redisTemplate.opsForHash().entries(SESSION_ID_PREFIX + sessionId);
		SecurityContext securityContext = (SecurityContext) sesssionMap.get("sessionAttr:SPRING_SECURITY_CONTEXT");
		WebAuthenticationDetails details = (WebAuthenticationDetails) securityContext.getAuthentication().getDetails();

		UserOnlineBo userOnlineBo = (UserOnlineBo) securityContext.getAuthentication().getPrincipal();

		userOnlineBo.setHost(details.getRemoteAddress());
		userOnlineBo.setLastAccess(new Date((Long) sesssionMap.get("lastAccessedTime")));
		userOnlineBo.setSessionId(sessionId);
//		userOnlineBo.setLastLoginTime(new Date((Long) sesssionMap.get("lastAccessedTime"))); // 自带，再设多余
		userOnlineBo.setTimeout((Integer) sesssionMap.get("maxInactiveInterval"));
		userOnlineBo.setStartTime(new Date((Long) sesssionMap.get("creationTime")));
		userOnlineBo.setSessionStatus((boolean) sesssionMap.get("sessionAttr:" + Constants.ONLINE_STATUS));

		return userOnlineBo;
	}

	public Map<String, Object> changeSessionStatus(Boolean status, String sessionIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String[] sessionIdArray = null;
			if (sessionIds.indexOf(",") == -1) {
				sessionIdArray = new String[] { sessionIds };
			} else {
				sessionIdArray = sessionIds.split(",");
			}
			for (String id : sessionIdArray) {
				redisTemplate.opsForHash().put(SESSION_ID_PREFIX + id, "sessionAttr:" + Constants.ONLINE_STATUS, status);
			}
			map.put("status", 200);
			map.put("sessionStatus", status ? 1 : 0);
			map.put("sessionStatusText", status ? "踢出" : "激活");
			map.put("sessionStatusTextTd", status ? "有效" : "已踢出");
		} catch (Exception e) {
			logger.error(String.format("改变Session状态错误，sessionId[%s]", sessionIds), e);
			map.put("status", 500);
			map.put("message", "改变失败，有可能Session不存在，请刷新再试！");
		}
		return map;
	}

	public void forbidUserById(Long id, Long status) {
		//获取所有在线用户
		for (UserOnlineBo bo : getAllUsers()) {
			Long userId = bo.getId();
			//匹配用户ID
			if (userId.equals(id)) {
				redisTemplate.opsForHash().put(SESSION_ID_PREFIX + id, "sessionAttr:" + Constants.ONLINE_STATUS,
						status.intValue() == 1);
			}
		}
	}

	public static void logoutAndRedirect(HttpServletRequest request, HttpServletResponse response, String uri)
			throws IOException {
		// refer to: https://zhidao.baidu.com/question/94783680.html
		Cookie rememberMe = new Cookie(Constants.REMEMBER_ME_COOKIE, null);
		rememberMe.setMaxAge(0);
		rememberMe.setPath(request.getContextPath()); // 加上path才有删除效果？
		response.addCookie(rememberMe);
//		Cookie sessionId = new Cookie(Constant.SESSION_ID_COOKIE, null);
//		sessionId.setMaxAge(0);
//		sessionId.setPath(request.getContextPath()); // 加上path才有删除效果？
//		response.addCookie(sessionId);

		request.getSession().invalidate();

		SecurityContextHolder.clearContext();

		new DefaultRedirectStrategy().sendRedirect(request, response, uri);

		// 以上流程refer to: https://www.cnblogs.com/fanqisoft/p/10659173.html
		// 还refer to: http://www.merryyou.cn/2018/01/18/spring-security%e6%ba%90%e7%a0%81%e5%88%86%e6%9e%90%e5%85%ab%ef%bc%9aspring-security-%e9%80%80%e5%87%ba/
	}

}
