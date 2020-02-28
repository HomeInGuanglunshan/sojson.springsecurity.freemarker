package sojson.springsecurity.freemarker.core.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;

import com.alibaba.fastjson.JSONObject;

import sojson.springsecurity.freemarker.common.model.UUser;
import sojson.springsecurity.freemarker.common.utils.SessionUtils;
import sojson.springsecurity.freemarker.core.filter.MyRememberMeAuthenticationFilter;
import sojson.springsecurity.freemarker.core.filter.SimpleFilter;
import sojson.springsecurity.freemarker.core.statics.Constants;
import sojson.springsecurity.freemarker.user.service.UUserService;

@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) // 先注释掉，看看不加有什么后果
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	UUserService userService;

	/**
	 * 下面的daoAuthenticationProvider()已经取代该方法作用，故注释掉
	 */
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
//
//		// 密码传过来之前，已在前端进行MD5加密，所以后台就不再对其加密了
//		// refer to: https://www.meiwen.com.cn/subject/olrqxqtx.html
//		auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {
//
//			@Override
//			public boolean matches(CharSequence rawPassword, String encodedPassword) {
//				return StringUtils.equals(rawPassword.toString(), encodedPassword);
//			}
//
//			@Override
//			public String encode(CharSequence rawPassword) {
//				return rawPassword.toString();
//			}
//		});
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();

		http.formLogin().loginPage("/u/login.shtml").loginProcessingUrl("/u/submitLogin.shtml")
				.usernameParameter("email").passwordParameter("pswd")
				// 如果此处用.successForwardUrl("/")，则报“HTTP Status 405 - Request method 'POST' not supported”，目前不清楚原因
				.defaultSuccessUrl("/");
		/**
		 * 如果不配置successHandler，前端的ajax请求失败，报statusText:"parsererror"。
		 * 应该可以通过修改ajax相关内容，来取代该配置， 但尚未找到修改办法。
		 * <p>
		 * refer to: https://www.jianshu.com/p/650a497b3a40
		 */
		http.formLogin().successHandler((request, response, authentication) -> {

			response.setContentType("application/json;charset=utf-8");

			String email = authentication.getName();
			UUser user = userService.findUserByEmail(email);
			user.setLastLoginTime(new Date());
			userService.updateByPrimaryKey(user);

			Map<String, Object> map = new HashMap<>();
			map.put("status", 200);
			map.put("message", "登录成功");

			// refer to: https://www.jianshu.com/p/bcf2d2a7d1b0
			SavedRequest savedRequest = (SavedRequest) request.getSession()
					.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
			map.put("back_url", savedRequest == null ? null : savedRequest.getRedirectUrl());

			PrintWriter out = response.getWriter();
			out.write(JSONObject.toJSONString(map));
			out.flush();
			out.close();

			// 如果是通过rememberMe cookie自动登录，估计该方法体内的所有代码不会被执行，包括这句
			request.getSession().setAttribute(Constants.ONLINE_STATUS, true);
		});
		http.formLogin().failureHandler((request, response, exception) -> {

			response.setContentType("application/json;charset=utf-8");
			// 设置为401，不进入前端ajax的success，倒是进入其error
//			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setStatus(HttpServletResponse.SC_OK);

			Map<String, Object> map = new HashMap<>();
			map.put("status", 401);

			if (exception instanceof AccountExpiredException) {
				map.put("message", "账户过期");
			} else if (exception instanceof UsernameNotFoundException) {
				map.put("message", "用户名不存在");
			} else if (exception instanceof BadCredentialsException) {
				map.put("message", "密码错误");
			} else if (exception instanceof CredentialsExpiredException) {
				map.put("message", "证书过期");
			} else if (exception instanceof DisabledException) {
				map.put("message", "账户不允许登录");
			} else if (exception instanceof LockedException) {
				map.put("message", "账号被锁定");
			} else {
				map.put("message", exception.getMessage());
			}

			PrintWriter out = response.getWriter();
			out.write(JSONObject.toJSONString(map));
			out.flush();
			out.close();
		});

		http.logout().logoutUrl("/u/logout.shtml").logoutSuccessUrl("/").clearAuthentication(true)
				.invalidateHttpSession(true).deleteCookies(Constants.SESSION_ID_COOKIE);
		/**
		 * 配置原因同successHandler
		 * <p>
		 * refer to: https://www.jianshu.com/p/650a497b3a40
		 */
		http.logout().logoutSuccessHandler((request, response, authentication) -> {

			response.setContentType("application/json;charset=utf-8");

			Map<String, Object> map = new HashMap<>();
			map.put("status", 200);

			PrintWriter out = response.getWriter();
			out.write(JSONObject.toJSONString(map));
			out.flush();
			out.close();
		});

		/**
		 * 替代shiro.demo中原有的LoginFilter<br>
		 * 此处暂时不起作用，故注释掉
		 * <p>
		 * refer to: https://www.jianshu.com/p/650a497b3a40
		 */
//		http.httpBasic().authenticationEntryPoint((request, response, authException) -> {
//			response.setContentType("application/json;charset=utf-8");
//			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//
//			Map<String, Object> map = new HashMap<>();
////			map.put("login_status", 300);
////			map.put("message", "\u5F53\u524D\u7528\u6237\u6CA1\u6709\u767B\u5F55\uFF01"); // 当前用户没有登录！
//			map.put("login_status", HttpServletResponse.SC_FORBIDDEN);
//			map.put("message", "当前用户没有登录");
//
//			PrintWriter out = response.getWriter();
//			out.write(JSONObject.toJSONString(map));
//			out.flush();
//			out.close();
//		});

		http.exceptionHandling().accessDeniedHandler((request, response, exception) -> {
			if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
				response.setContentType("application/json;charset=utf-8");
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);

				Map<String, Object> map = new HashMap<>();
				map.put("status", 403);
				map.put("message", "没有权限");

				PrintWriter out = response.getWriter();
				out.write(JSONObject.toJSONString(map));
				out.flush();
				out.close();
			} else {
				new DefaultRedirectStrategy().sendRedirect(request, response, "/open/unauthorized.shtml");
			}
		});
		/**
		 * 如果此句注释掉，则上面的<br>
		 * map.put("back_url", savedRequest.getRedirectUrl());<br>
		 * 报NullPointerException，看样子是savedRequest获取不到，目前不清楚原因
		 * <p>
		 * "/?login"表示的是登录页面？
		 */
//		http.exceptionHandling().accessDeniedPage("/?login");

		http.authorizeRequests()
				// 已被web.ignoring()取代
//				.antMatchers(Constant.STATIC_PATHS).permitAll()
				.antMatchers(Constants.UNRESTRICTED_PATHS).permitAll()
				// 根节点权限控制。如果跟下面一句代码交换位置，则根目录没有访问权限，怀疑是正则表达式写错，但目前不知怎么写对。
				.antMatchers("/").authenticated()
				// 除静态资源外，只允许访问以.shtml 结尾的 url
				.regexMatchers(".*(?<!\\.shtml)$").denyAll()
				.antMatchers("/user/**", "/permission/selectPermissionById.shtml", "/member/onlineDetails/**",
						"/role/mypermission.shtml", "/role/getPermissionTree.shtml", "/role/selectRoleByUserId.shtml")
				.authenticated()
				// refer to: https://www.iteye.com/blog/huan1993-2398652
				.anyRequest().access("@permissionFilter.isAccessAllowed(authentication, request)");

		http.sessionManagement().invalidSessionStrategy((request, response) -> {
			new DefaultRedirectStrategy().sendRedirect(request, response, "/u/login.shtml");
		});
		// 承担了shiro.demo中原有的KickoutSessionFilter
		http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false)
				.expiredSessionStrategy(new SessionInformationExpiredStrategy() {
					@Override
					public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
						SessionUtils.logoutAndRedirect(event.getRequest(), event.getResponse(),
								"/u/login.shtml?kickout");
					}
				});
		// 方便统计在线用户，但貌似统计数据只能存于内存中，服务器一重启，数据就丢失了，且不能共享。所以，此举明显不适用于分布式。
		//.sessionRegistry(getSessionRegistry());

		http.rememberMe()
				// .rememberMeParameter("rememberMe")
				// 此处设置为“rememberMe”，没有效果，仍然需传递名为“remember-me”的参数，“记住我”才能生效。
				// tokenBasedRememberMeServices()才是设置生效点，目前不清楚原因。
				.rememberMeServices(tokenBasedRememberMeServices());
		/**
		 * 同是账号admin，首先在chrome账密登入，接着在Firefox
		 * rememberMe登入，居然没有一个浏览器显示被踢出，即“禁止异地登录”失效了。如果都是账密登入，还是会有被踢出的情况出现.
		 * <p>
		 * successHandler的存在，使得RememberMeAuthenticationFilter没有机会执行<br>
		 * chain.doFilter(request, response);
		 * ，也就没有机会执行后面的SessionManagementFilter，以至于“禁止异地登录”失效
		 */
//		http.rememberMe().authenticationSuccessHandler((request, response, authentication) -> {
//			request.getSession().setAttribute(Constant.ONLINE_STATUS, true);
//			new DefaultRedirectStrategy().sendRedirect(request, response,
//					request.getRequestURI().replace(request.getContextPath(), ""));
//		});

		http.addFilterAt(new MyRememberMeAuthenticationFilter(authenticationManager(), tokenBasedRememberMeServices()),
				RememberMeAuthenticationFilter.class);
		// refer to: https://stackoverflow.com/questions/33282660/spring-security-websecurity-ignoring
		// refer to: http://www.it1352.com/965938.html
		http.addFilterAfter(new SimpleFilter(), FilterSecurityInterceptor.class);
	}

	/**
	 * 请求静态资源不会产生session了
	 * <p>
	 * refer to: https://www.jianshu.com/p/ba60f39d6182
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(Constants.STATIC_PATHS);
	}

	/**
	 * refer to: https://www.cnblogs.com/scau-chm/p/6836748.html
	 */
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		ProviderManager providerManager = new ProviderManager(
				Arrays.asList(daoAuthenticationProvider(), rememberMeAuthenticationProvider()));
//		providerManager.setEraseCredentialsAfterAuthentication(false);
		return providerManager;
	}

	@Bean("tokenBasedRememberMeServices")
	public TokenBasedRememberMeServices tokenBasedRememberMeServices() {
		TokenBasedRememberMeServices services = new TokenBasedRememberMeServices(Constants.REMEMBER_ME_SECRET,
				userDetailsService);
		services.setAlwaysRemember(false);
		services.setParameter("rememberMe"); // 默认是 remember-me
		services.setCookieName(Constants.REMEMBER_ME_COOKIE);
		services.setTokenValiditySeconds(AbstractRememberMeServices.TWO_WEEKS_S);
		return services;
	}

	@Bean
	public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
		return new RememberMeAuthenticationProvider(Constants.REMEMBER_ME_SECRET);
	}

	/**
	 * refer to: https://www.cnblogs.com/scau-chm/p/6836748.html<br>
	 * refer to: https://blog.csdn.net/jaune161/article/details/18359321
	 *
	 * @return
	 */
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(new PasswordEncoder() {
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return StringUtils.equals(rawPassword.toString(), encodedPassword);
			}

			@Override
			public String encode(CharSequence rawPassword) {
				return rawPassword.toString();
			}
		});
		return daoAuthenticationProvider;
	}

	@Bean
	public CookieHttpSessionIdResolver cookieHttpSessionIdResolver() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookieName(Constants.SESSION_ID_COOKIE);
		CookieHttpSessionIdResolver resolver = new CookieHttpSessionIdResolver();
		resolver.setCookieSerializer(serializer);
		return resolver;
	}

//	@Bean
//	public SessionRegistry getSessionRegistry() {
//		return new SessionRegistryImpl();
//	}
}
