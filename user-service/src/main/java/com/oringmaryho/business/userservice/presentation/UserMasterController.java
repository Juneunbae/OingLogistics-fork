package com.oringmaryho.business.userservice.presentation;

import com.oringmaryho.business.userservice.application.UserMasterService;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterCreateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterFindRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterGrantRoleRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSearchRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterSignUpRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterUpdateRequestServiceDto;
import com.oringmaryho.business.userservice.application.dto.request.UserMasterUpdateRoleRequestServiceDto;
import com.oringmaryho.business.userservice.config.pageable.PageableConfig;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterCreateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterDeleteRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterDeleteRoleRequestServiceDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterGrantRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSearchRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterSignUpRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterUpdateRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.request.UserMasterUpdateRoleRequestDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterGrantRoleResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterSearchResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterUpdateResponseDto;
import com.oringmaryho.business.userservice.presentation.dto.response.UserMasterUpdateRoleResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/users")
public class UserMasterController {

  private final UserMasterService userMasterService;
  private final UserPresentationMapper userPresentationMapper;
  private final PageableConfig pageableConfig;

  @PostMapping("/sign-up")
  public ResponseEntity<?> signUpMasterUser(
      @RequestBody UserMasterSignUpRequestDto userMasterSignUpRequestDto) {
    UserMasterSignUpRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterSignUpServiceDto(
        userMasterSignUpRequestDto);
    userMasterService.signUpUserMaster(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("")
  public ResponseEntity<?> createUser(
      @RequestBody UserMasterCreateRequestDto userMasterCreateRequestDto) {
    UserMasterCreateRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterCreateRequestServiceDto(
        userMasterCreateRequestDto);
    userMasterService.createUser(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findUserMaster(@PathVariable Long id) {
    UserMasterFindRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterFindRequestServiceDto(
        id);
    //todo: responsedto로 변환
    userMasterService.findUserMaster(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserMasterSearchResponseDto>> searchUsers(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "sortDirection", required = false) String sortDirection,
      @RequestBody UserMasterSearchRequestDto userMasterSearchRequestDto) {
    Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
    UserMasterSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterSearchRequestServiceDto(
        userMasterSearchRequestDto, customPageable);
    List<UserMasterSearchResponseDto> responseDtos = userMasterService.searchUsers(
        requestServiceDto);
    return null;
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateUserMaster(@PathVariable Long id,
      @RequestBody UserMasterUpdateRequestDto userMasterUpdateRequestDto) {
    UserMasterUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterUpdateRequestServiceDto(
        id, userMasterUpdateRequestDto);
    UserMasterUpdateResponseDto responseDto = userMasterService.updateUser(requestServiceDto);
    //todo: responsedto 반환하기
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{id}/grant")
  public ResponseEntity<?> grantRoleUserMaster(@PathVariable Long id,
      @RequestBody UserMasterGrantRoleRequestDto userMasterGrantRoleRequestDto) {
    UserMasterGrantRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterGrantRoleRequestServiceDto(
        id, userMasterGrantRoleRequestDto);
    UserMasterGrantRoleResponseDto responseDto = userMasterService.grantRoleUser(requestServiceDto);
    //todo: responsedto 반환하기
    return ResponseEntity.ok().build();
  }

  @PutMapping("/roles/{id}")
  public ResponseEntity<?> updateRoleUserMaster(@PathVariable Long id,
      @RequestBody UserMasterUpdateRoleRequestDto userMasterUpdateRoleRequestDto) {
    UserMasterUpdateRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterUpdateRoleRequestServiceDto(
        id, userMasterUpdateRoleRequestDto);
    UserMasterUpdateRoleResponseDto responseDto = userMasterService.updateRoleUser(
        requestServiceDto);
    //todo: responsedto 반환하기
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/roles/{id}")
  public ResponseEntity<?> deleteRoleUserMaster(@PathVariable Long id) {
    UserMasterDeleteRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterDeleteRoleRequestServiceDto(
        id);
    userMasterService.deleteRoleUser(requestServiceDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUserMaster(@PathVariable Long id) {
    UserMasterDeleteRequestServiceDto requestServiceDto = userPresentationMapper.toUserMasterDeleteRequestServiceDto(
        id);
    userMasterService.deleteUser(requestServiceDto);
    return ResponseEntity.ok().build();
  }
}
