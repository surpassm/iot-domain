package com.github.surpassm.iot.modbus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author mc
 * Create date 2019/2/13 10:35
 * Version 1.0
 * Description 多线程任务配置
 */

@Configuration
@EnableAsync
public class ThreadConfig implements AsyncConfigurer {
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		// 核心线程数
		taskExecutor.setCorePoolSize(5);
		// 最大线程数
		taskExecutor.setMaxPoolSize(50);
		// 队列最大长度
		taskExecutor.setQueueCapacity(1000);
		// 线程池维护线程所允许的空闲时间(单位秒)
		taskExecutor.setKeepAliveSeconds(120);
		// 线程池对拒绝任务(无线程可用)的处理策略 ThreadPoolExecutor.CallerRunsPolicy策略 ,调用者的线程会执行该任务,如果执行器已关闭,则丢弃.
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

		taskExecutor.initialize();
		return taskExecutor;
	}
}
