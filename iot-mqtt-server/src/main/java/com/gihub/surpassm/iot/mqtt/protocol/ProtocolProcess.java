

package com.gihub.surpassm.iot.mqtt.protocol;

import com.gihub.surpassm.iot.mqtt.internal.InternalCommunication;
import com.gihub.surpassm.iot.mqtt.service.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 协议处理
 */
@Component
public class ProtocolProcess {

	@Resource
	private ISessionStoreService sessionStoreService;

	@Resource
	private ISubscribeStoreService subscribeStoreService;

	@Resource
	private IAuthService authService;

	@Resource
	private IMessageIdService messageIdService;

	@Resource
	private IRetainMessageStoreService messageStoreService;

	@Resource
	private IDupPublishMessageStoreService dupPublishMessageStoreService;

	@Resource
	private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

	@Resource
	private InternalCommunication internalCommunication;

	private Connect connect;

	private Subscribe subscribe;

	private UnSubscribe unSubscribe;

	private Publish publish;

	private DisConnect disConnect;

	private PingReq pingReq;

	private PubRel pubRel;

	private PubAck pubAck;

	private PubRec pubRec;

	private PubComp pubComp;

	public Connect connect() {
		if (connect == null) {
			connect = new Connect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authService);
		}
		return connect;
	}

	public Subscribe subscribe() {
		if (subscribe == null) {
			subscribe = new Subscribe(subscribeStoreService, messageIdService, messageStoreService);
		}
		return subscribe;
	}

	public UnSubscribe unSubscribe() {
		if (unSubscribe == null) {
			unSubscribe = new UnSubscribe(subscribeStoreService);
		}
		return unSubscribe;
	}

	public Publish publish() {
		if (publish == null) {
			publish = new Publish(sessionStoreService, subscribeStoreService, messageIdService, messageStoreService, dupPublishMessageStoreService, internalCommunication);
		}
		return publish;
	}

	public DisConnect disConnect() {
		if (disConnect == null) {
			disConnect = new DisConnect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService);
		}
		return disConnect;
	}

	public PingReq pingReq() {
		if (pingReq == null) {
			pingReq = new PingReq();
		}
		return pingReq;
	}

	public PubRel pubRel() {
		if (pubRel == null) {
			pubRel = new PubRel();
		}
		return pubRel;
	}

	public PubAck pubAck() {
		if (pubAck == null) {
			pubAck = new PubAck(messageIdService, dupPublishMessageStoreService);
		}
		return pubAck;
	}

	public PubRec pubRec() {
		if (pubRec == null) {
			pubRec = new PubRec(dupPublishMessageStoreService, dupPubRelMessageStoreService);
		}
		return pubRec;
	}

	public PubComp pubComp() {
		if (pubComp == null) {
			pubComp = new PubComp(messageIdService, dupPubRelMessageStoreService);
		}
		return pubComp;
	}

	public ISessionStoreService getSessionStoreService() {
		return sessionStoreService;
	}

}
