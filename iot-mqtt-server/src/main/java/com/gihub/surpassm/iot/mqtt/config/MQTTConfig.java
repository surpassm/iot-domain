package com.gihub.surpassm.iot.mqtt.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * netty配置信息
 */
public class MQTTConfig {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Component
    @ConfigurationProperties(prefix = "spring.mqtt.server")
    public static class MQTTServerConfig{
		/**
		 * Broker唯一标识
		 */
		private String id;


        /**
         * netty server port
         */
        private Integer tcpPort;

        /**
         * 主线程最大线程数
         */
        private Integer bossMaxThreadCount = 4;

        /**
         * 工作线程最大线程数
         */
        private Integer workMaxThreadCount = 50;

		/**
		 *
		 */
		private Integer businessThreadsNum = 50;


        /**
         * 数据包最大长度
         */
        private Integer maxFrameLength = 65535;

        /**
         * 单节点最大连接数
         */
        private Integer maxConnectNum = 2;

        /**
         * 读事件空闲时间 秒
         */
        private Integer readerIdleTimeSeconds = 0;

        /**
         * 写事件空闲时间 秒
         */
        private Integer writerIdleTimeSeconds = 0;
		/**
		 * 心跳时间(秒), 默认60秒, 该值可被客户端连接时相应配置覆盖
		 */
		private int keepAlive = 60;

		/**
		 * 是否开启Epoll模式, 默认关闭
		 */
		private boolean isEpoll = false;
		/**
		 * Socket参数, 存放已完成三次握手请求的队列最大长度, 默认1024长度
		 */
		private int soBackLog = 1024;
		/**
		 * 设置发送缓冲大小 SO_SNDBUF
		 */
		private int soSendBuf = 32 * 1024;
		/**
		 * 设置接收缓冲大小 SO_RCVBUF
		 */
		private int soReceiveBuf = 32 * 1024;

		/**
		 * Socket参数, 是否开启心跳保活机制, 默认开启
		 */
		private boolean soKeepAlive = true;

		/**
		 * 集群配置, 是否基于组播发现, 默认开启
		 */
		private boolean enableMulticastGroup = true;
		/**
		 * 集群配置, 基于组播发现
		 */
		private String multicastGroup;
		/**
		 * 集群配置, 当组播模式禁用时, 使用静态IP开启配置集群
		 */
		private String[] staticIpAddresses;
    }

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Component
	@ConfigurationProperties(prefix = "spring.mqtt.server.cache")
	public static class IgniteProperties{
		/**
		 * 持久化缓存内存初始化大小(MB), 默认值: 64
		 */
		private int persistenceInitialSize = 64;

		/**
		 * 持久化缓存占用内存最大值(MB), 默认值: 128
		 */
		private int persistenceMaxSize = 128;

		/**
		 * 持久化磁盘存储路径
		 */
		private String persistenceStorePath;

		/**
		 * 非持久化缓存内存初始化大小(MB), 默认值: 64
		 */
		private int NotPersistenceInitialSize = 64;

		/**
		 * 非持久化缓存占用内存最大值(MB), 默认值: 128
		 */
		private int NotPersistenceMaxSize = 128;
	}


}
