package sojson.springsecurity.freemarker.user.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sojson.springsecurity.freemarker.common.model.UUser;
import sojson.springsecurity.freemarker.permission.service.PermissionService;
import sojson.springsecurity.freemarker.permission.service.RoleService;
import sojson.springsecurity.freemarker.user.bo.UserOnlineBo;
import sojson.springsecurity.freemarker.user.service.UUserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UUserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UUser user = userService.findUserByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户名不存在");
		}

		Set<String> roles = roleService.findRoleByUserId(user.getId());
		Set<String> permissions = permissionService.findPermissionByUserId(user.getId());

		Set<GrantedAuthority> authorities = new HashSet<>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
		for (String permission : permissions) {
			authorities.add(new SimpleGrantedAuthority(permission));
		}

		return new UserOnlineBo(user, authorities);
	}

}
