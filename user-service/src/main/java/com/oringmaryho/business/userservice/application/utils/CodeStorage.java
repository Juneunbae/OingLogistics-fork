package com.oringmaryho.business.userservice.application.utils;

import jakarta.annotation.PreDestroy;

public interface CodeStorage {

	public void storeCode(String key, String serviceUsername, String code, long ttl);

	public String getCode(String key);

	public String getSlackUsername(String key);

	public boolean removeCode(String key);

	public boolean hasKey(String key);

	@PreDestroy
	public void shutdown();

}
