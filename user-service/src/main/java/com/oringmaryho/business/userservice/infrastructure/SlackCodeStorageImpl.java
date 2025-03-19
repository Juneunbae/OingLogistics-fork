package com.oringmaryho.business.userservice.infrastructure;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.oringmaryho.business.userservice.application.utils.CodeStorage;
import com.oringmaryho.business.userservice.exception.ErrorCode;
import com.oringmaryho.business.userservice.exception.UserException;

import jakarta.annotation.PreDestroy;

@Component
public class SlackCodeStorageImpl implements CodeStorage {
	private final ConcurrentHashMap<String, Pair> storage = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void storeCode(String key, String slackUsername, String code, long ttl) {
		if (ttl < 0) {
			throw new UserException(ErrorCode.STORAGE_NEGATIVE_ERROR);
		}
		storage.put(key, new Pair(slackUsername, code));
		scheduler.schedule(() -> storage.remove(key), ttl, TimeUnit.MILLISECONDS);
	}

	public String getCode(String key) {
		Pair pair = storage.get(key);
		return pair.slackCode;
	}

	public String getSlackUsername(String key) {
		Pair pair = storage.get(key);
		return pair.slackUsername;
	}

	public boolean removeCode(String key) {
		return storage.remove(key) != null; // 키가 존재하고 삭제되면 true 키를 찾을 수 없으먄 null
	}

	public boolean hasKey(String key) {
		return storage.containsKey(key);
	}

	@PreDestroy
	public void shutdown() {
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	class Pair {
		String slackUsername;
		String slackCode;

		public Pair(String slackUsername, String slackCode) {
			this.slackUsername = slackUsername;
			this.slackCode = slackCode;
		}
	}
}

