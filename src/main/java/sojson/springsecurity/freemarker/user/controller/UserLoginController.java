package sojson.springsecurity.freemarker.user.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONObject;
import sojson.springsecurity.freemarker.common.controller.BaseController;
import sojson.springsecurity.freemarker.common.model.UUser;
import sojson.springsecurity.freemarker.common.utils.LoggerUtils;
import sojson.springsecurity.freemarker.common.utils.VerifyCodeUtils;
import sojson.springsecurity.freemarker.user.manager.UserManager;
import sojson.springsecurity.freemarker.user.service.UUserService;

/**
 *
 * 开发公司：itboy.net<br/>
 * 版权：itboy.net<br/>
 * <p>
 *
 * 用户登录相关，不需要做登录限制
 *
 * <p>
 *
 * 区分 责任人 日期 说明<br/>
 * 创建 周柏成 2016年5月3日 <br/>
 * <p>
 * *******
 * <p>
 *
 * @author zhou-baicheng
 * @email i@itboy.net
 * @version 1.0,2016年5月3日 <br/>
 *
 */
@Controller
@RequestMapping("u")
public class UserLoginController extends BaseController {

	@Autowired
	HttpServletRequest request;

	@Resource
	UUserService userService;

	/**
	 * 登录跳转
	 *
	 * @return
	 */
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public ModelAndView login() {
		return new ModelAndView("user/login");
	}

	/**
	 * 注册跳转
	 *
	 * @return
	 */
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public ModelAndView register() {
		return new ModelAndView("user/register");
	}

	/**
	 * 注册 && 登录
	 *
	 * @param vcode
	 *            验证码
	 * @param entity
	 *            UUser实体
	 * @return
	 */
	@RequestMapping(value = "subRegister", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> subRegister(String vcode, UUser entity) {
		resultMap.put("status", 400);
		if (!VerifyCodeUtils.verifyCode(request.getSession(), vcode)) {
			resultMap.put("message", "验证码不正确！");
			return resultMap;
		}
		String email = entity.getEmail();

		UUser user = userService.findUserByEmail(email);
		if (null != user) {
			resultMap.put("message", "帐号|Email已经存在！");
			return resultMap;
		}
		Date date = new Date();
		entity.setCreateTime(date);
		entity.setLastLoginTime(date);
		//把密码md5
		entity = UserManager.md5Pswd(entity);
		//设置有效
		entity.setStatus(UUser._1);

		entity = userService.insert(entity);
		LoggerUtils.fmtDebug(getClass(), "注册插入完毕！", JSONObject.fromObject(entity).toString());
//		entity = TokenManager.login(entity, Boolean.TRUE);
		LoggerUtils.fmtDebug(getClass(), "注册后，登录完毕！", JSONObject.fromObject(entity).toString());
		resultMap.put("message", "注册成功！");
		resultMap.put("status", 200);
		return resultMap;
	}

}
