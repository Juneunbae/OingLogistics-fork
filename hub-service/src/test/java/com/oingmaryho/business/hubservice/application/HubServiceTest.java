package com.oingmaryho.business.hubservice.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubsSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Address;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.repository.CustomHubRepository;
import com.oingmaryho.business.hubservice.exception.ErrorCode;
import com.oingmaryho.business.hubservice.exception.HubException;
import com.oingmaryho.business.hubservice.utils.PageableUtils;

@ExtendWith(MockitoExtension.class)
class HubServiceTest {

	@InjectMocks
	private HubService hubService;

	@Mock
	private HubApplicationMapper mapper;

	@Mock
	private CustomHubRepository customHubRepository;

	@DisplayName("허브 관리자는 허브 ID를 통해 허브 정보를 조회할 수 있다.")
	@Test
	void hub_manager_get_hub_by_id_test() {
		// Given
		UUID hubId = UUID.randomUUID();
		Hub hub = Hub.builder()
			.id(hubId)
			.name("허브 이름")
			.address(new Address("허브 주소", 1.0, 2.0))
			.managerId(1L)
			.build();

		HubSearchRequestServiceDto requestDto = new HubSearchRequestServiceDto(hubId);
		HubSearchResponseServiceDto responseDto = new HubSearchResponseServiceDto(
			hubId, "허브 이름","허브 주소", 1.0, 2.0,1L
		);

		when(mapper.toHubSearchResponseServiceDto(any(Hub.class))).thenReturn(responseDto);
		when(customHubRepository.findActiveHubById(hubId)).thenReturn(Optional.of(hub));

		// When
		HubSearchResponseServiceDto result = hubService.getHubById(requestDto);

		// Then
		assertThat(result).isNotNull()
			.extracting(
				HubSearchResponseServiceDto::id,
				HubSearchResponseServiceDto::name,
				HubSearchResponseServiceDto::address,
				HubSearchResponseServiceDto::latitude,
				HubSearchResponseServiceDto::longitude,
				HubSearchResponseServiceDto::managerId
			)
			.containsExactlyInAnyOrder(hubId, "허브 이름", "허브 주소", 1.0, 2.0, 1L);
		verify(customHubRepository, times(1)).findActiveHubById(any());
	}

	@DisplayName("허브 검색을 통해 허브 정보들을 조회할 수 있다.")
	@Test
	void hub_search_test() {
		// Given
		// parameter
		Pageable pageable = PageableUtils.customPageable(1, 10, null, null);
		HubsSearchRequestServiceDto requestDto = new HubsSearchRequestServiceDto(
			null, null, null, null, null, null
		);

		// entity
		UUID hub1Id = UUID.randomUUID();
		Hub hub1 = Hub.builder()
			.id(hub1Id)
			.name("허브1 이름")
			.address(new Address("허브1 주소", 1.0, 2.0))
			.managerId(1L)
			.build();
		UUID hub2Id = UUID.randomUUID();
		Hub hub2 = Hub.builder()
			.id(hub2Id)
			.name("허브2 이름")
			.address(new Address("허브2 주소", 3.0, 4.0))
			.managerId(2L)
			.build();
		Page<Hub> hubPage = new PageImpl<>(List.of(hub1, hub2), pageable, 2);

		// dto
		HubSearchResponseServiceDto responseDto1 = new HubSearchResponseServiceDto(
			hub1Id, "허브1 이름", "허브1 주소", 1.0, 2.0, 1L
		);
		HubSearchResponseServiceDto responseDto2 = new HubSearchResponseServiceDto(
			hub2Id, "허브2 이름", "허브2 주소", 3.0, 4.0, 2L
		);

		when(customHubRepository.findDynamicQuery(any(), eq(pageable))).thenReturn(hubPage);
		when(mapper.toHubSearchResponseServiceDto(hub1)).thenReturn(responseDto1);
		when(mapper.toHubSearchResponseServiceDto(hub2)).thenReturn(responseDto2);

		// When
		Page<HubSearchResponseServiceDto> result = hubService.searchHubs(requestDto, pageable);

		// Then
		assertThat(result.getContent()).hasSize(2)
			.containsExactly(responseDto1, responseDto2);
		assertThat(result.getTotalElements()).isEqualTo(2);
		verify(customHubRepository, times(1)).findDynamicQuery(any(), any());
	}

	@DisplayName("허브 조건 검색을 통해 허브 정보들을 조회할 수 있다.")
	@Test
	void hub_search_by_condition_test() {
		// Given
		// parameter
		Pageable pageable = PageableUtils.customPageable(1, 10, null, null);
		HubsSearchRequestServiceDto requestDto = new HubsSearchRequestServiceDto(
			null, "허브1 이름", null, null, null, null
		);

		// entity
		UUID hub1Id = UUID.randomUUID();
		Hub hub1 = Hub.builder()
			.id(hub1Id)
			.name("허브1 이름")
			.address(new Address("허브1 주소", 1.0, 2.0))
			.managerId(1L)
			.build();
		UUID hub2Id = UUID.randomUUID();
		Hub hub2 = Hub.builder()
			.id(hub2Id)
			.name("허브2 이름")
			.address(new Address("허브2 주소", 3.0, 4.0))
			.managerId(2L)
			.build();
		Page<Hub> hubPage = new PageImpl<>(List.of(hub1), pageable, 2);

		// dto
		HubSearchResponseServiceDto responseDto1 = new HubSearchResponseServiceDto(
			hub1Id, "허브1 이름", "허브1 주소", 1.0, 2.0, 1L
		);

		when(customHubRepository.findDynamicQuery(any(), eq(pageable))).thenReturn(hubPage);
		when(mapper.toHubSearchResponseServiceDto(hub1)).thenReturn(responseDto1);

		// When
		Page<HubSearchResponseServiceDto> result = hubService.searchHubs(requestDto, pageable);

		// Then
		assertThat(result.getContent()).hasSize(1)
			.containsExactly(responseDto1);
		assertThat(result.getTotalElements()).isEqualTo(1);
		verify(customHubRepository, times(1)).findDynamicQuery(any(), any());
	}

	@DisplayName("허브 ID에 해당하는 허브가 존재하지 않는 경우, 예외가 발생한다.")
	@Test
	void hub_not_found_exception_test() {
		// Given
		UUID unmatchedHubId = UUID.randomUUID();
		HubSearchRequestServiceDto requestDto = new HubSearchRequestServiceDto(unmatchedHubId);

		// When & Then
		assertThatThrownBy(() -> hubService.getHubById(requestDto))
			.isInstanceOf(HubException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND);
	}
}