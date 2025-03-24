package com.oringmaryho.business.userservice.application.utils;

public interface DirectMessageAuthService {

  String makeDirectMessage(String code);

  String generateCode();

}
