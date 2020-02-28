package sojson.springsecurity.freemarker.core.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.GenericFilterBean;

import com.alibaba.fastjson.JSONObject;

import sojson.springsecurity.freemarker.common.utils.SessionUtils;
import sojson.springsecurity.freemarker.core.statics.Constants;

/**
 * 和shiro.demo中的SimpleAuthFilter起相同作用
 * <p>
 * refer to:
 *
 * <pre>
 * https://blog.csdn.net/panchang199266/article/details/90578947
 * https://stackoverflow.com/questions/33282660/spring-security-websecurity-ignoring
 * http://www.it1352.com/965938.html
 * </pre>
 */
//@Component
public class SimpleFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		HttpSession session = httpServletRequest.getSession(false);
		if (session == null) {
			chain.doFilter(request, response);
			return;
		}

		// 如果为boolean，在get到的值为null时，向下转型报错
		Boolean sessionStatus = (Boolean) session.getAttribute(Constants.ONLINE_STATUS);
		if (sessionStatus == null || sessionStatus) {
			chain.doFilter(request, response);
		} else {
			if ("XMLHttpRequest".equalsIgnoreCase(httpServletRequest.getHeader("X-Requested-With"))) {
				Map<String, Object> map = new HashMap<>();
				map.put("status", HttpServletResponse.SC_UNAUTHORIZED);
				map.put("message", "您已被踢出，请重新登录");

				PrintWriter out = response.getWriter();
				out.write(JSONObject.toJSONString(map));
				out.flush();
				out.close();
			} else {
				SessionUtils.logoutAndRedirect(httpServletRequest, httpServletResponse, "/open/kickedOut.shtml");
			}
		}
	}

}
