

package com.gihub.surpassm.iot.mqtt.service.impl;

import com.gihub.surpassm.iot.mqtt.pojo.SessionStore;
import com.gihub.surpassm.iot.mqtt.service.ISessionStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储服务
 */
@Service
public class SessionStoreService implements ISessionStoreService {

	private Map<String, SessionStore> sessionCache = new ConcurrentHashMap<String, SessionStore>();

	@Override
	public void put(String clientId, SessionStore sessionStore) {
		sessionCache.put(clientId, sessionStore);
	}

	@Override
	public SessionStore get(String clientId) {
		return sessionCache.get(clientId);
	}

	@Override
	public boolean containsKey(String clientId) {
		return sessionCache.containsKey(clientId);
	}

	@Override
	public void remove(String clientId) {
		sessionCache.remove(clientId);
	}
}
