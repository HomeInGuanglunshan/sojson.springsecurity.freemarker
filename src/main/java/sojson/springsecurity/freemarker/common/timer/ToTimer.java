package sojson.springsecurity.freemarker.common.timer;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;

import sojson.springsecurity.freemarker.permission.service.RoleService;

/**
 * 定时任务恢复数据
 * <p>
 * 此定时器，在modify.shiro.demo中没有启动，但在这里却启动了，目前暂不清楚原因。无关痛痒，先停掉。
 */
//@Component
public class ToTimer {

	@Resource
	RoleService roleService;

	@Scheduled(cron = "0/20 * * * * ? ")
	public void run() {
		/**
		 * 调用存储过程，重新创建表，插入初始化数据。
		 */
		roleService.initData();
		System.out.println(new Date().getTime());
	}

}
