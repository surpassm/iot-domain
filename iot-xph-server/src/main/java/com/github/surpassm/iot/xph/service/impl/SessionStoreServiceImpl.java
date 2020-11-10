

package com.github.surpassm.iot.xph.service.impl;

import com.github.surpassm.iot.xph.pojo.SessionStore;
import com.github.surpassm.iot.xph.service.SessionStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储服务
 * @author AOC
 */
@Service
public class SessionStoreServiceImpl implements SessionStoreService {

	private Map<String, SessionStore> sessionCache = new ConcurrentHashMap<>();

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

	@Override
	public Map<String, SessionStore> findAll() {
		return sessionCache;
	}

	@Override
	public int size() {
		return sessionCache.size();
	}
}
