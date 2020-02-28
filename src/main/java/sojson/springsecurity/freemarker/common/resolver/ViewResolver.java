package sojson.springsecurity.freemarker.common.resolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import sojson.springsecurity.freemarker.core.freemarker.extend.FreeMarkerViewExtension;

/**
 * https://blog.csdn.net/qq_29611427/article/details/88867549
 */
@Configuration
public class ViewResolver {

	@Bean
	public InternalResourceViewResolver commonViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(InternalResourceView.class); // 设置检查器
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setOrder(1);
		viewResolver.setContentType("text/html;charset=UTF-8");
		return viewResolver;
	}

	@Bean
	public FreeMarkerViewResolver freemarkerViewResolver() {
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
		viewResolver.setViewClass(FreeMarkerViewExtension.class); // 设置检查器
		viewResolver.setCache(true);
		viewResolver.setSuffix(".ftl");
		viewResolver.setOrder(0);
		viewResolver.setContentType("text/html;charset=UTF-8");
		return viewResolver;
	}

	@Bean
	public InternalResourceViewResolver htmlViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/views/");
		viewResolver.setViewClass(JstlView.class); // 设置检查器
		viewResolver.setSuffix(".jsp");
		viewResolver.setOrder(2);
		viewResolver.setContentType("text/html;charset=UTF-8");
		return viewResolver;
	}
}
