

package com.gihub.surpassm.iot.mqtt.handler;

import com.gihub.surpassm.iot.mqtt.pojo.SessionStore;
import com.gihub.surpassm.iot.mqtt.protocol.ProtocolProcess;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.io.IOException;

/**
 * @author mc
 * Create date 2020/5/21 9:43
 * Version 1.0
 * Description MQTT消息处理
 */
public class BrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

	private ProtocolProcess protocolProcess;

	public BrokerHandler(ProtocolProcess protocolProcess) {
		this.protocolProcess = protocolProcess;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
		//通过判断消息的固定头部是否为空，如果空;则通过processDisconnect(ctx)将设备连接关闭
		if (msg.fixedHeader() == null){
			processDisconnect(ctx);
			return;
		}
		/**
		 * 名字		值	报文流动方向		描述
		 * Reserved	0	禁止			保留
		 * CONNECT	1	客户端到服务端	客户端请求连接服务端
		 * CONNACK	2	服务端到客户端	连接报文确认
		 * PUBLISH	3	两个方向都允许	发布消息
		 * PUBACK	4	两个方向都允许	QoS 1消息发布收到确认
		 * PUBREC	5	两个方向都允许	发布收到（保证交付第一步）
		 * PUBREL	6	两个方向都允许	发布释放（保证交付第二步）
		 * PUBCOMP	7	两个方向都允许	QoS 2消息发布完成（保证交互第三步）
		 * SUBSCRIBE	8	客户端到服务端	客户端订阅请求
		 * SUBACK	9	服务端到客户端	订阅请求报文确认
		 * UNSUBSCRIBE	10	客户端到服务端	客户端取消订阅请求
		 * UNSUBACK	11	服务端到客户端	取消订阅报文确认
		 * PINGREQ	12	客户端到服务端	心跳请求
		 * PINGRESP	13	服务端到客户端	心跳响应
		 * DISCONNECT	14	客户端到服务端	客户端断开连接
		 * Reserved	15	禁止	保留
		 * */
		//通过判断固定头部的MQTT消息类型,针对不同消息做相应的处理
		switch (msg.fixedHeader().messageType()) {
			case CONNECT:
				protocolProcess.connect().processConnect(ctx.channel(), (MqttConnectMessage) msg);
				break;
			case CONNACK:
				break;
			case PUBLISH:
				protocolProcess.publish().processPublish(ctx.channel(), (MqttPublishMessage) msg);
				break;
			case PUBACK:
				protocolProcess.pubAck().processPubAck(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBREC:
				protocolProcess.pubRec().processPubRec(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBREL:
				protocolProcess.pubRel().processPubRel(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBCOMP:
				protocolProcess.pubComp().processPubComp(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case SUBSCRIBE:
				protocolProcess.subscribe().processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
				break;
			case SUBACK:
				break;
			case UNSUBSCRIBE:
				protocolProcess.unSubscribe().processUnSubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
				break;
			case UNSUBACK:
				break;
			case PINGREQ:
				protocolProcess.pingReq().processPingReq(ctx.channel(), msg);
				break;
			case PINGRESP:
				break;
			case DISCONNECT:
				protocolProcess.disConnect().processDisConnect(ctx.channel(), msg);
				break;
			default:
				break;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof IOException) {
			// 远程主机强迫关闭了一个现有的连接的异常
			ctx.close();
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			if (idleStateEvent.state() == IdleState.ALL_IDLE) {
				Channel channel = ctx.channel();
				String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
				// 发送遗嘱消息
				if (this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
					SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
					if (sessionStore.getWillMessage() != null) {
						this.protocolProcess.publish().processPublish(ctx.channel(), sessionStore.getWillMessage());
					}
				}
				ctx.close();
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	private void processDisconnect(ChannelHandlerContext ctx) {
		ctx.close();
	}
}
