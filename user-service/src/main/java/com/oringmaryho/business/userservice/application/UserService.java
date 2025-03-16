package com.oringmaryho.business.userservice.application;

import com.oringmaryho.business.userservice.application.dto.request.UserMasterCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private UserRepository userRepository;

  public void signUpUser(UserSignUpRequestServiceDto requestServiceDto) {

  }

  public UserSignInResponseServiceDto signInUser(UserSignInRequestServiceDto requestServiceDto) {
    return null;
  }

  public UserSearchResponseServiceDto searchUser(UserSearchRequestServiceDto requestServiceDto) {
    return null;
  }

  public void slackConfirmUser(UserSlackConfirmRequestServiceDto requestServiceDto) {

  }

  public void signUpUserMaster(UserMasterSignUpRequestServiceDto requestServiceDto) {

  }

  public void createUser(UserMasterCreateRequestServiceDto requestServiceDto) {

  }

  public void searchUserMaster(UserMasterSearchRequestServiceDto requestServiceDto) {

  }
}
