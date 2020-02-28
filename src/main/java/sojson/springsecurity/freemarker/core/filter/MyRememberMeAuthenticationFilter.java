package sojson.springsecurity.freemarker.core.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import sojson.springsecurity.freemarker.core.statics.Constants;

public class MyRememberMeAuthenticationFilter extends RememberMeAuthenticationFilter {

	public MyRememberMeAuthenticationFilter(AuthenticationManager authenticationManager,
			RememberMeServices rememberMeServices) {
		super(authenticationManager, rememberMeServices);
	}

	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) {
		request.getSession(request.getSession(false) == null).setAttribute(Constants.ONLINE_STATUS, true);
	}

}
