package com.oingmaryho.business.hubservice.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubDeleteRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;

@ExtendWith(MockitoExtension.class)
class HubAdminServiceTest {

	@InjectMocks
	private HubAdminService hubAdminService;

	@Mock
	private HubRepository hubRepository;

	@Mock
	private HubApplicationMapper mapper;

	@DisplayName("허브 정보를 통해 허브를 생성할 수 있다.")
	@Test
	void hub_create_test() {
		// Given
		HubCreateRequestServiceDto requestDto = new HubCreateRequestServiceDto(
			"허브 이름", "허브 주소", 1.0, 2.0, 1L
		);

		Hub unsavedHub = Hub.builder()
			.name(requestDto.name())
			.address(new Address(requestDto.address(), requestDto.latitude(), requestDto.longitude()))
			.managerId(requestDto.managerId())
			.build();
		Hub savedHub = Hub.builder()
			.id(UUID.randomUUID())
			.name(requestDto.name())
			.address(new Address(requestDto.address(), requestDto.latitude(), requestDto.longitude()))
			.managerId(requestDto.managerId())
			.build();

		when(mapper.toHub(requestDto)).thenReturn(unsavedHub);
		when(hubRepository.save(unsavedHub)).thenReturn(savedHub);
		when(mapper.toHubCreateResponseServiceDto(savedHub)).thenReturn(new HubCreateResponseServiceDto(savedHub.getId()));

		// When
		HubCreateResponseServiceDto result = hubAdminService.createHub(requestDto);

		// Then
		// 호출 검증
		verify(mapper, times(1)).toHub(requestDto);
		verify(hubRepository, times(1)).save(unsavedHub);
		verify(mapper, times(1)).toHubCreateResponseServiceDto(savedHub);
		// 응답값 검증
		assertThat(result).isNotNull()
			.extracting(HubCreateResponseServiceDto::id)
			.isEqualTo(savedHub.getId());
	}

	@DisplayName("허브 ID와 허브 수정 정보를 통해 허브를 수정할 수 있다.")
	@Test
	void hub_update_test() {
		// Given
		UUID hubId = UUID.randomUUID();
		Hub existingHub = Hub.builder()
			.id(hubId)
			.name("허브 이름")
			.address(new Address("허브 주소", 1.0, 2.0))
			.managerId(1L)
			.build();

		HubUpdateRequestServiceDto requestDto = new HubUpdateRequestServiceDto(
			"수정된 허브 이름", "수정된 허브 주소", 3.0, 4.0, 2L
		);

		when(hubRepository.findById(hubId)).thenReturn(Optional.of(existingHub));

		// When
		HubUpdateResponseServiceDto result = hubAdminService.updateHub(hubId, requestDto);

		// Then
		assertThat(result).isNotNull()
			.extracting(HubUpdateResponseServiceDto::id)
			.isEqualTo(hubId);
		assertThat(existingHub)
			.extracting(
				Hub::getName,
				h -> h.getAddress().getAddress(),
				h -> h.getAddress().getLatitude(),
				h -> h.getAddress().getLongitude(),
				Hub::getManagerId)
			.containsExactly(
				requestDto.name(),
				requestDto.address(),
				requestDto.latitude(),
				requestDto.longitude(),
				requestDto.managerId()
			);
		verify(hubRepository).findById(hubId);
	}

	@DisplayName("허브 ID를 통해 허브를 soft delete 할 수 있다.")
	@Test
	void hub_soft_delete_test() {
		// Given
		UUID hubId = UUID.randomUUID();
		Hub hub = Hub.builder()
			.id(hubId)
			.name("허브 이름")
			.address(new Address("허브 주소", 1.0, 2.0))
			.managerId(1L)
			.build();

		HubDeleteRequestServiceDto requestDto = new HubDeleteRequestServiceDto(hubId);

		when(hubRepository.findById(hubId)).thenReturn(Optional.of(hub));

		// When
		hubAdminService.deleteHub(requestDto);

		// Then
		assertThat(hub).isNotNull()
			.extracting(Hub::getIsDeleted)
			.isEqualTo(true);
	}
}

