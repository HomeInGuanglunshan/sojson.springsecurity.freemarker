package sojson.springsecurity.freemarker.core.filter;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * refer to: https://www.iteye.com/blog/huan1993-2398652
 */
@Component("permissionFilter")
public class PermissionFilter {

	public boolean isAccessAllowed(Authentication authentication, HttpServletRequest request) {
		String uri = request.getRequestURI().replace(request.getContextPath(), "");

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities.contains(new SimpleGrantedAuthority(uri));
	}

}
