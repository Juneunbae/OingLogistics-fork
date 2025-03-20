package com.oringmaryho.business.userservice.application.utils;

import java.util.Map;

import com.oringmaryho.business.userservice.domain.User;

public interface RedisUtil {
	void updateUserInfo(User user);

	Map<String, String> updateUserJwtToken(Long id);


}
