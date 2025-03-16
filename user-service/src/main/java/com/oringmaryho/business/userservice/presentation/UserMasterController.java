package com.oringmaryho.business.userservice.presentation;

import com.oringmaryho.business.userservice.application.UserApplicationMapper;
import com.oringmaryho.business.userservice.application.UserService;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignInRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserSlackConfirmRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSearchResponseServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserSignInResponseServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterCreateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignInRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserSlackConfirmRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserSignInResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/users")
public class UserMasterController {

  private final UserService userService;
  private final UserApplicationMapper userApplicationMapper;
  private final UserPresentationMapper userPresentationMapper;

  @PostMapping("/sign-up")
  public ResponseEntity<?> signUpMasterUser(@RequestBody UserMasterSignUpRequestDto userMasterSignUpRequestDto) {
    UserMasterSignUpRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterSignUpServiceDto(
        userMasterSignUpRequestDto);
    userService.signUpUserMaster(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("")
  public ResponseEntity<?> createUser(@RequestBody UserMasterCreateRequestDto userMasterCreateRequestDto) {
    UserMasterCreateRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterCreateRequestServiceDto(
        userMasterCreateRequestDto);
    userService.createUser(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> searchUserMaster(
      @PathVariable Long id) {
    UserMasterSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterSearchRequestServiceDto(id);
    userService.searchUserMaster(requestServiceDto);
    return ResponseEntity.ok().build();
  }
}
