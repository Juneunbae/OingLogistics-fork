package com.oringmaryho.business.userservice.application.utils;

public interface DirectMessageAuthService {

	public String getUserIdByEmail(String userEmail);

	public String openDirectMessageChannel(String userId);

	public void sendDirectMessage(String slackUsername, String code);

	public String generateCode();

}
