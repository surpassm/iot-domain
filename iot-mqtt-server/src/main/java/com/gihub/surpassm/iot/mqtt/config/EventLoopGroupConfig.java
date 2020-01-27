package com.gihub.surpassm.iot.mqtt.config;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author mc
 * Create date 2019/11/10 10:05
 * Version 1.0
 * Description
 */
@Configuration
public class EventLoopGroupConfig {

	@Resource
	private MQTTConfig.MQTTServerConfig serverConfig;
	@Resource
	private MQTTConfig.IgniteProperties igniteProperties;

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

	@Bean
	public Ignite ignite() throws Exception {
		IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
		// Ignite实例名称
		igniteConfiguration.setIgniteInstanceName(serverConfig.getId());
		// Ignite日志
		Logger logger = LoggerFactory.getLogger("org.apache.ignite");
		igniteConfiguration.setGridLogger(new Slf4jLogger(logger));
		// 非持久化数据区域
		DataRegionConfiguration notPersistence = new DataRegionConfiguration().setPersistenceEnabled(false)
				.setInitialSize(igniteProperties.getNotPersistenceInitialSize() * 1024 * 1024)
				.setMaxSize(igniteProperties.getNotPersistenceMaxSize() * 1024 * 1024).setName("not-persistence-data-region");
		// 持久化数据区域
		DataRegionConfiguration persistence = new DataRegionConfiguration().setPersistenceEnabled(true)
				.setInitialSize(igniteProperties.getPersistenceInitialSize() * 1024 * 1024)
				.setMaxSize(igniteProperties.getPersistenceMaxSize() * 1024 * 1024).setName("persistence-data-region");
		DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration().setDefaultDataRegionConfiguration(notPersistence)
				.setDataRegionConfigurations(persistence)
				.setWalArchivePath(StrUtil.isNotBlank(igniteProperties.getPersistenceStorePath()) ? igniteProperties.getPersistenceStorePath() : null)
				.setWalPath(StrUtil.isNotBlank(igniteProperties.getPersistenceStorePath()) ? igniteProperties.getPersistenceStorePath() : null)
				.setStoragePath(StrUtil.isNotBlank(igniteProperties.getPersistenceStorePath()) ? igniteProperties.getPersistenceStorePath() : null);
		igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
		// 集群, 基于组播或静态IP配置
		TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
		if (serverConfig.isEnableMulticastGroup()) {
			TcpDiscoveryMulticastIpFinder tcpDiscoveryMulticastIpFinder = new TcpDiscoveryMulticastIpFinder();
			tcpDiscoveryMulticastIpFinder.setMulticastGroup(serverConfig.getMulticastGroup());
			tcpDiscoverySpi.setIpFinder(tcpDiscoveryMulticastIpFinder);
		} else {
			TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
			tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList(serverConfig.getStaticIpAddresses()));
			tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
		}
		igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
		Ignite ignite = Ignition.start(igniteConfiguration);
		ignite.cluster().active(true);
		return ignite;
	}

	@Bean
	public IgniteCache messageIdCache() throws Exception {
		CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("not-persistence-data-region")
				.setCacheMode(CacheMode.PARTITIONED).setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL).setName("messageIdCache");
		return ignite().getOrCreateCache(cacheConfiguration);
	}

	@Bean
	public IgniteCache retainMessageCache() throws Exception {
		CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
				.setCacheMode(CacheMode.PARTITIONED).setName("retainMessageCache");
		return ignite().getOrCreateCache(cacheConfiguration);
	}

	@Bean
	public IgniteCache subscribeNotWildcardCache() throws Exception {
		CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
				.setCacheMode(CacheMode.PARTITIONED).setName("subscribeNotWildcardCache");
		return ignite().getOrCreateCache(cacheConfiguration);
	}

	@Bean
	public IgniteCache subscribeWildcardCache() throws Exception {
		CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
				.setCacheMode(CacheMode.PARTITIONED).setName("subscribeWildcardCache");
		return ignite().getOrCreateCache(cacheConfiguration);
	}

	@Bean
	public IgniteCache dupPublishMessageCache() throws Exception {
		CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
				.setCacheMode(CacheMode.PARTITIONED).setName("dupPublishMessageCache");
		return ignite().getOrCreateCache(cacheConfiguration);
	}

	@Bean
	public IgniteCache dupPubRelMessageCache() throws Exception {
		CacheConfiguration cacheConfiguration = new CacheConfiguration().setDataRegionName("persistence-data-region")
				.setCacheMode(CacheMode.PARTITIONED).setName("dupPubRelMessageCache");
		return ignite().getOrCreateCache(cacheConfiguration);
	}

	@Bean
	public IgniteMessaging igniteMessaging() throws Exception {
		return ignite().message(ignite().cluster().forRemotes());
	}
}
