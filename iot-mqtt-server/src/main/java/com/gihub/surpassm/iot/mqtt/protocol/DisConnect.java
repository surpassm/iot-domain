

package com.gihub.surpassm.iot.mqtt.protocol;

import com.gihub.surpassm.iot.mqtt.pojo.SessionStore;
import com.gihub.surpassm.iot.mqtt.service.IDupPubRelMessageStoreService;
import com.gihub.surpassm.iot.mqtt.service.IDupPublishMessageStoreService;
import com.gihub.surpassm.iot.mqtt.service.ISessionStoreService;
import com.gihub.surpassm.iot.mqtt.service.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DISCONNECT连接处理
 */
public class DisConnect {

	private static final Logger LOGGER = LoggerFactory.getLogger(DisConnect.class);

	private ISessionStoreService sessionStoreService;

	private ISubscribeStoreService subscribeStoreService;

	private IDupPublishMessageStoreService dupPublishMessageStoreService;

	private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

	public DisConnect(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
		this.sessionStoreService = sessionStoreService;
		this.subscribeStoreService = subscribeStoreService;
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
	}

	public void processDisConnect(Channel channel, MqttMessage msg) {
		String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		SessionStore sessionStore = sessionStoreService.get(clientId);
		if (sessionStore.isCleanSession()) {
			subscribeStoreService.removeForClient(clientId);
			dupPublishMessageStoreService.removeByClient(clientId);
			dupPubRelMessageStoreService.removeByClient(clientId);
		}
		LOGGER.debug("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
		sessionStoreService.remove(clientId);
		channel.close();
	}

}
