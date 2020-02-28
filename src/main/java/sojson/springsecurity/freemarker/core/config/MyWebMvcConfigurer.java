package sojson.springsecurity.freemarker.core.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.ext.jsp.TaglibFactory;

@Configuration
//@EnableWebMvc
public class MyWebMvcConfigurer implements WebMvcConfigurer {

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
//		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
//	}

	@Bean
	public MappingJackson2HttpMessageConverter getMessageConverter() {
		MediaType mediaType1 = new MediaType("text", "plain", Charset.forName("UTF-8"));
		MediaType mediaType2 = new MediaType("*", "*", Charset.forName("UTF-8"));
		MediaType mediaType3 = new MediaType("text", "*", Charset.forName("UTF-8"));
		MediaType mediaType4 = new MediaType("application", "json", Charset.forName("UTF-8"));
		List<MediaType> supportedMediaTypes = Arrays.asList(mediaType1, mediaType2, mediaType3, mediaType4);
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(supportedMediaTypes);
		return converter;
	}

//	@Override
//	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//
//	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/user/index.shtml");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// 开启路径后缀匹配
		configurer.setUseRegisteredSuffixPatternMatch(true);
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
		ServletRegistrationBean<DispatcherServlet> servletServletRegistrationBean = new ServletRegistrationBean<>(
				dispatcherServlet);
		servletServletRegistrationBean.addUrlMappings("*.shtml", "/", "*.html", "*.css", "*.js", "*.png", "*.gif",
				"*.ico", "*.jpeg", "*.jpg", "*.ftl");
		return servletServletRegistrationBean;
	}

//	/**
//	 * 设置匹配.shtml后缀的请求
//	 *
//	 * @param dispatcherServlet
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Bean
//	public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
//		ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
//		bean.addUrlMappings("*.shtml");
//		return bean;
//	}

//	单独放在此处，不生效，目前不清楚原因
//	但好似有时候又会生效，目前原因不明
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Bean
//	public FilterRegistrationBean filterRegistrationBean() {
//		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//		registrationBean.setFilter(new DelegatingFilterProxy());
//		registrationBean.addUrlPatterns("*.shtml");
//		//被代理filter
//		registrationBean.addInitParameter("targetBeanName", "shiroFilter");
//		//指明作用于filter的所有生命周期
//		registrationBean.addInitParameter("targetFilterLifecycle", "true");
//		registrationBean.setName("ShiroFilter");
//		registrationBean.setOrder(1);
//		registrationBean.setEnabled(false);
//		return registrationBean;
//	}

	@Autowired
	private FreeMarkerConfigurer configurer;

	@PostConstruct
	public void freeMarkerConfigurer() {
		List<String> tlds = new ArrayList<String>();
		tlds.add("/static/tags/security.tld");
		TaglibFactory taglibFactory = configurer.getTaglibFactory();
		taglibFactory.setClasspathTlds(tlds);
		if (taglibFactory.getObjectWrapper() == null) {
			taglibFactory.setObjectWrapper(configurer.getConfiguration().getObjectWrapper());
		}
	}
}
