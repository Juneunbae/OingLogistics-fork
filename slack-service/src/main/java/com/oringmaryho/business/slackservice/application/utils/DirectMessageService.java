package com.oringmaryho.business.slackservice.application.utils;

public interface DirectMessageService {

	String getUserIdByEmail(String userEmail);

	String openDirectMessageChannel(String userId);

	void sendDirectMessage(String slackUsername, String code);

	String generateCode();

}
