package sojson.springsecurity.freemarker.core.config;

import java.util.Arrays;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@Configuration
public class SessionConfig {

	@Bean
	public RedisHttpSessionConfiguration redisHttpSessionConfiguration() {
		RedisHttpSessionConfiguration configuration = new RedisHttpSessionConfiguration();
		configuration.setHttpSessionListeners(Arrays.asList(httpSessionListener()));
		return configuration;
	}

	@Bean
	public HttpSessionListener httpSessionListener() {
		return new HttpSessionListener() {

			@Override
			public void sessionDestroyed(HttpSessionEvent se) {
				System.out.println("session destroyed");
			}

			@Override
			public void sessionCreated(HttpSessionEvent se) {
				System.out.println("session created");
			}
		};
	}

}
