package sojson.springsecurity.freemarker.core.statics;

import java.util.Calendar;

import sojson.springsecurity.freemarker.core.config.IConfig;

/**
 *
 * 开发公司：SOJSON在线工具
 * <p>
 * 版权所有：© www.sojson.com
 * <p>
 * 博客地址：http://www.sojson.com/blog/
 * <p>
 * <p>
 *
 * 静态变量
 *
 * <p>
 *
 * 区分 责任人 日期 说明<br/>
 * 创建 周柏成 2016年6月2日 <br/>
 *
 * @author zhou-baicheng
 * @email so@sojson.com
 * @version 1.0,2016年6月2日 <br/>
 *
 */
public interface Constants {

	String CONTEXT_PATH = "contextPath";
	/*** 项目根路径 */

	/*** Freemarker 使用的变量 begin **/

	String TARGET = "target";//标签使用目标

	String OUT_TAG_NAME = "outTagName";//输出标签Name

	/*** Freemarker 使用的变量 end **/

	/** 其他常用变量 begin **/
	String NAME = "name";
	String ID = "id";
	String TOKEN = "token";
	String LOING_USER = "loing_user";
	/** Long */
	Long ZERO = new Long(0);
	Long ONE = new Long(1);
	Long TWO = new Long(2);
	Long THREE = new Long(3);
	Long EIGHT = new Long(8);

	/** String */
	String S_ZERO = "0";
	String S_ONE = "1";
	String S_TOW = "2";
	String S_THREE = "3";

	/** Integer */
	Integer I_ZERO = 0;
	Integer I_ONE = 1;
	Integer I_TOW = 2;
	Integer I_THREE = 3;
	/** 其他常用变量 end **/

	/** cache常用变量 begin **/
	String CACHE_NAME = "shiro_cache";
	String CACHE_MANAGER = "cacheManager";//cacheManager bean name
	/** cache常用变量 end **/

	/** 当前年份 **/
	int NOW_YEAR = Calendar.getInstance().get(Calendar.YEAR);

	/** 地址 **/
	String DOMAIN_WWW = IConfig.get("domain.www");//前端域名
	String DOMAIN_CDN = IConfig.get("domain.cdn");//静态资源域名
	String VERSION = String.valueOf(System.currentTimeMillis());//版本号，重启的时间

	//存储到缓存，标识用户的禁止状态，解决在线用户踢出的问题
	String EXECUTE_CHANGE_USER = "SOJSON_EXECUTE_CHANGE_USER";

	String ONLINE_STATUS = "SOJSON_ONLINE_STATUS";

	String SESSION_ID_COOKIE = "v_v-s-baidu";
	String REMEMBER_ME_COOKIE = "v_v-re-baidu";
	String REMEMBER_ME_SECRET = "sojson.springcloud";

	/**
	 * /open/kickedOut.shtml和/open/unauthorized.shtml所指向的页面，包含了top.ftl，而top.
	 * ftl更是包含了一系列spring
	 * security标签。估计就是这些标签，导致已经被ignore了的/open/**页面重定向。所以，/open/**
	 * 页面不能和其他STATIC_PATHS一起被ignore，而是要作为被permitALl的UNRESTRICTED_PATHS之一
	 */
	String[] UNRESTRICTED_PATHS = { "/u/**", "/open/**" };
	String[] STATIC_PATHS = { "/css/**", "/js/**", "/favicon.ico" };
}
