

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
 * MQTT消息处理
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
		//通过判断固定头部的MQTT消息类型,针对不同消息做相应的处理
		switch (msg.fixedHeader().messageType()) {
			case CONNECT://1客户端请求连接到服务器
				protocolProcess.connect().processConnect(ctx.channel(), (MqttConnectMessage) msg);
				break;
			case CONNACK://2连接确认
				break;
			case PUBLISH://3发布讯息
				protocolProcess.publish().processPublish(ctx.channel(), (MqttPublishMessage) msg);
				break;
			case PUBACK://4发布确认
				protocolProcess.pubAck().processPubAck(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBREC://5发布已收到（保证交付部分1）
				protocolProcess.pubRec().processPubRec(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBREL://6发布发布（确保交付的第2部分）
				protocolProcess.pubRel().processPubRel(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBCOMP://7发布完成（保证交付的第3部分）
				protocolProcess.pubComp().processPubComp(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case SUBSCRIBE://8客户订阅请求
				protocolProcess.subscribe().processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
				break;
			case SUBACK://9订阅确认
				break;
			case UNSUBSCRIBE://10客户退订请求
				protocolProcess.unSubscribe().processUnSubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
				break;
			case UNSUBACK://11退订确认
				break;
			case PINGREQ://12PING请求
				protocolProcess.pingReq().processPingReq(ctx.channel(), msg);
				break;
			case PINGRESP://13PING回应
				break;
			case DISCONNECT://14客户端正在断开连接
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
