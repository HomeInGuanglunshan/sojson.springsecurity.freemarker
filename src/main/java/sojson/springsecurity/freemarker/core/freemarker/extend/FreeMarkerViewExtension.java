package sojson.springsecurity.freemarker.core.freemarker.extend;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import sojson.springsecurity.freemarker.common.utils.LoggerUtils;
import sojson.springsecurity.freemarker.core.statics.Constants;

public class FreeMarkerViewExtension extends FreeMarkerView {

	@Override
	protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) {

		try {
			super.exposeHelpers(model, request);
		} catch (Exception e) {
			LoggerUtils.fmtError(FreeMarkerViewExtension.class, e, "FreeMarkerViewExtend 加载父类出现异常。请检查。");
		}

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (null != principal && !Objects.equals("anonymousUser", principal)) {
			model.put("token", principal); // 登录的token
		}

		model.put("basePath", request.getContextPath()); // base目录。
		model.put(Constants.CONTEXT_PATH, request.getContextPath());

		model.put("_v", Constants.VERSION); // 版本号，重启的时间
		model.put("cdn", Constants.DOMAIN_CDN); // CDN域名
		model.put("NOW_YEAY", Constants.NOW_YEAR); // 今年

		model.put("_time", new Date().getTime());

		model.putAll(FreeMarker.initMap);
	}
}
