package com.github.surpassm.iot.modbus.server;

import com.github.surpassm.iot.modbus.config.ModbusConfig;
import com.github.surpassm.iot.modbus.example.ModbusRequestHandlerExample;
import com.github.surpassm.iot.modbus.handler.ModbusRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.beans.PropertyChangeSupport;

/**
 * @author mc
 * Create date 2020/1/27 12:06
 * Version 1.0
 * Description
 * 整体服务启动初始类
 */
@Slf4j
@Service
public class ModbusServer {
	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;
	private final EventExecutorGroup businessGroup;

	private final ChannelGroup clientChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private CONNECTION_STATES connectionState = CONNECTION_STATES.down;

	@Resource
	private ModbusConfig.ModbusServerConfig serverConfig;

	@Resource
	private ModbusServerChannelInit modbusServerChannelInit;

	private Channel channel;

	public ModbusServer(@Qualifier("businessGroup") EventExecutorGroup businessGroup,
						@Qualifier("workerGroup") EventLoopGroup workerGroup,
						@Qualifier("bossGroup") EventLoopGroup bossGroup) {
		this.businessGroup = businessGroup;
		this.workerGroup = workerGroup;
		this.bossGroup = bossGroup;
	}

	@PostConstruct
	public void start() throws InterruptedException {
		modbusServer(new ModbusRequestHandlerExample());
	}

	/**
	 * 销毁资源
	 */
	@PreDestroy
	public void stop() {
		bossGroup.shutdownGracefully().syncUninterruptibly();
		workerGroup.shutdownGracefully().syncUninterruptibly();
		businessGroup.shutdownGracefully().syncUninterruptibly();
		channel.closeFuture().syncUninterruptibly();
		clientChannels.close().awaitUninterruptibly();
		channel = null;
		log.info("TCP服务关闭成功");
	}

	/**
	 * MQTT服务配置
	 */
	private void modbusServer(ModbusRequestHandler handler) throws InterruptedException {
		handler.setServer(this);
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup)
				.channel(serverConfig.isEpoll()? EpollServerSocketChannel.class : NioServerSocketChannel.class)
				// handler在初始化时就会执行
				.handler(new LoggingHandler(LogLevel.INFO))
				// childHandler会在客户端成功connect后才执行
				.childHandler(modbusServerChannelInit)
				//服务端可连接队列数,对应TCP/IP协议listen函数中SO_BACKLOG参数
				.option(ChannelOption.SO_BACKLOG, serverConfig.getSoBackLog())
				//设置发送缓冲大小
				.option(ChannelOption.SO_SNDBUF, serverConfig.getSoSendBuf())
				//设置接收缓冲大小
				.option(ChannelOption.SO_RCVBUF, serverConfig.getSoReceiveBuf())
				//保持连接 是否开启心跳保活机制, 默认开启
				.childOption(ChannelOption.SO_KEEPALIVE, serverConfig.isSoKeepAlive())
				;
		//内存泄漏检测 开发推荐PARANOID 线上SIMPLE
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);
		ChannelFuture future = serverBootstrap.bind(serverConfig.getTcpPort()).sync();
		setConnectionState(CONNECTION_STATES.listening);

		channel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
				setConnectionState(CONNECTION_STATES.down);
			}
		});

		if (future.isSuccess()) {
			log.info("TCP服务启动完毕,port={}", serverConfig.getTcpPort());
			channel = future.channel();
		}
	}


	public void setConnectionState(CONNECTION_STATES connectionState) {
		this.connectionState = connectionState;
	}


	public void close() {
		if (channel != null) {
			channel.close().awaitUninterruptibly();
		}
		clientChannels.close().awaitUninterruptibly();
	}

	public void addClient(Channel channel) {
		clientChannels.add(channel);
		setConnectionState(CONNECTION_STATES.clientsConnected);
	}

	public void removeClient(Channel channel) {
		clientChannels.remove(channel);
		setConnectionState(CONNECTION_STATES.clientsConnected);
	}



	public static enum CONNECTION_STATES {
		listening, down, clientsConnected
	}
}
