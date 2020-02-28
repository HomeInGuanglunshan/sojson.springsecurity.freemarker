package sojson.springsecurity.freemarker.user.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import sojson.springsecurity.freemarker.common.model.UUser;

/**
 * Session + User Bo
 *
 * @author sojson.com
 *
 */
public class UserOnlineBo extends UUser implements UserDetails, Serializable {

	private static final long serialVersionUID = 1L;

	//Session Id
	private String sessionId;
	//Session Host
	private String host;
	//Session创建时间
	private Date startTime;
	//Session最后交互时间
	private Date lastAccess;
	//Session timeout
	private long timeout;
	//session 是否踢出
	private boolean sessionStatus = Boolean.TRUE;

	public UserOnlineBo(UUser user, Set<GrantedAuthority> authorities) {
		super(user);
		this.username = getEmail();
		this.password = getPswd();
		this.enabled = new Long(1).equals(getStatus());
		this.authorities = authorities;
		this.accountNonLocked = true;
		this.accountNonExpired = true;
		this.credentialsNonExpired = true;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public boolean isSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(boolean sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	private String password;
	private String username;
	private Set<GrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public Set<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * refer to: https://www.oschina.net/question/1163960_113739<br>
	 * refer to: https://my.oschina.net/u/1437280?tab=activity
	 */
	@Override
	public boolean equals(Object obj) {
		return this.getId().equals(((UserOnlineBo) obj).getId());
	}

	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
}
