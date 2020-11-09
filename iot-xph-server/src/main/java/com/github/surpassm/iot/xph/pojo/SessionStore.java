/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.github.surpassm.iot.xph.pojo;

import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * 会话存储
 * @author AOC
 */
public class SessionStore implements Serializable {

	private String clientId;

	private Channel channel;

	private boolean cleanSession;



	public SessionStore(String clientId, Channel channel, boolean cleanSession) {
		this.clientId = clientId;
		this.channel = channel;
		this.cleanSession = cleanSession;
	}

	public String getClientId() {
		return clientId;
	}

	public SessionStore setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public Channel getChannel() {
		return channel;
	}

	public SessionStore setChannel(Channel channel) {
		this.channel = channel;
		return this;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public SessionStore setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
		return this;
	}

}
