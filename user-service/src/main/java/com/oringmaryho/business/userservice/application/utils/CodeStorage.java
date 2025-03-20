package com.oringmaryho.business.userservice.application.utils;

import jakarta.annotation.PreDestroy;

public interface CodeStorage {

	void storeCode(String key, String serviceUsername, String code, long ttl);

	String getCode(String key);

	String getSlackUsername(String key);

	boolean removeCode(String key);

	boolean hasKey(String key);

	@PreDestroy
	void shutdown();

}
