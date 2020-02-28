package sojson.springsecurity.freemarker.core.freemarker.extend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.XmlEscape;
import sojson.springsecurity.freemarker.core.tags.APITemplateModel;

@org.springframework.context.annotation.Configuration("freemarkerConfig")
@ConfigurationProperties(prefix = "freemarker")
public class FreeMarkerConfigExtension extends FreeMarkerConfigurer {

	public FreeMarkerConfigExtension() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("xml_escape", new XmlEscape());
		variables.put("api", new APITemplateModel());
		super.setFreemarkerVariables(variables);
	}

	@Override
	public void afterPropertiesSet() throws IOException, TemplateException {
		super.afterPropertiesSet();
		Configuration cfg = this.getConfiguration();
		putInitShared(cfg);
	}

	public static void put(Configuration cfg, String k, Object v) throws TemplateModelException {
		cfg.setSharedVariable(k, v);
		cfg.setNumberFormat("#");//防止页面输出数字,变成2,000
	}

	public static void putInitShared(Configuration cfg) throws TemplateModelException {
		//shiro tag
//		put(cfg, "shiro", new ShiroTags());
	}
}
