package com.github.surpassm.iot.modbus.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author mc
 * Create date 2019/11/10 10:05
 * Version 1.0
 * Description
 */
@Configuration
public class EventLoopGroupConfig {

	@Resource
	private ModbusConfig.ModbusServerConfig serverConfig;

	/**
	 * 负责TCP连接建立操作 绝对不能阻塞
	 *
	 * @return
	 */
	@Bean(name = "bossGroup")
	public EventLoopGroup bossGroup() {
		return serverConfig.isEpoll() ? new EpollEventLoopGroup(serverConfig.getBossMaxThreadCount()) : new NioEventLoopGroup(serverConfig.getBossMaxThreadCount());
	}

	/**
	 * 负责Socket读写操作 绝对不能阻塞
	 *
	 * @return
	 */
	@Bean(name = "workerGroup")
	public EventLoopGroup workerGroup() {
		return serverConfig.isEpoll() ? new EpollEventLoopGroup(serverConfig.getWorkMaxThreadCount()) : new NioEventLoopGroup(serverConfig.getWorkMaxThreadCount());
	}

	/**
	 * Handler中出现IO操作(如数据库操作，网络操作)使用这个
	 *
	 * @return
	 */
	@Bean(name = "businessGroup")
	public EventExecutorGroup businessGroup() {
		return new DefaultEventExecutorGroup(serverConfig.getBusinessThreadsNum());
	}




}
