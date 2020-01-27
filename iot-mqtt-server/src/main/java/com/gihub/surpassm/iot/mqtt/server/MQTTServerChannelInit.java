package com.gihub.surpassm.iot.mqtt.server;

import com.gihub.surpassm.iot.mqtt.config.MQTTConfig;
import com.gihub.surpassm.iot.mqtt.handler.BrokerHandler;
import com.gihub.surpassm.iot.mqtt.protocol.ProtocolProcess;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author mc
 * Create date 2019/11/10 10:08
 * Version 1.0
 * Description
 */
@Component
public class MQTTServerChannelInit extends ChannelInitializer<SocketChannel> {

	@Resource
	private MQTTConfig.MQTTServerConfig serverConfig;
	@Resource
	private ProtocolProcess protocolProcess;

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		//Netty提供的心跳检测
		pipeline.addLast(new IdleStateHandler(serverConfig.getReaderIdleTimeSeconds(),serverConfig.getWriterIdleTimeSeconds(),serverConfig.getKeepAlive(), TimeUnit.SECONDS))
				.addLast("decoder", new MqttDecoder())
				.addLast("encoder", MqttEncoder.INSTANCE)
				//代理服务
		 		.addLast("broker", new BrokerHandler(protocolProcess))
		;

	}
}
