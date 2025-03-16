package com.oringmaryho.business.userservice.presentation;

import com.oringmaryho.business.userservice.application.UserApplicationMapper;
import com.oringmaryho.business.userservice.application.UserMasterService;
import com.oringmaryho.business.userservice.application.UserService;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.response.UserMasterSearchResponseServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterCreateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSearchRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterSearchResponseDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSearchRequestServiceDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/users")
public class UserMasterController {

  private final UserMasterService userMasterService;
  private final UserApplicationMapper userApplicationMapper;
  private final UserPresentationMapper userPresentationMapper;

  @PostMapping("/sign-up")
  public ResponseEntity<?> signUpMasterUser(@RequestBody UserMasterSignUpRequestDto userMasterSignUpRequestDto) {
    UserMasterSignUpRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterSignUpServiceDto(
        userMasterSignUpRequestDto);
    userMasterService.signUpUserMaster(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("")
  public ResponseEntity<?> createUser(@RequestBody UserMasterCreateRequestDto userMasterCreateRequestDto) {
    UserMasterCreateRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterCreateRequestServiceDto(
        userMasterCreateRequestDto);
    userMasterService.createUser(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findUserMaster(
      @PathVariable Long id) {
    UserMasterFindRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterFindRequestServiceDto(id);
    //todo: responsedto로 변환
    userMasterService.findUserMaster(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserMasterSearchResponseDto>> searchUsers(
      @ModelAttribute UserMasterSearchRequestDto userMasterSearchRequestDto //todo: null값 처리 어케하지
  ) {
    UserMasterSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterSearchRequestServiceDto(userMasterSearchRequestDto);
    //todo: List<responsedto>로 변환
    List<UserMasterSearchResponseServiceDto> responseDtos = userMasterService.searchUsers(requestServiceDto);
    //todo: pageable 적용
    return null;
  }

}
