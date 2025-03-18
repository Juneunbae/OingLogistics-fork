package com.oingmaryho.business.hubservice.application;

import org.springframework.stereotype.Service;

import com.oingmaryho.business.hubservice.application.dto.mapper.HubApplicationMapper;
import com.oingmaryho.business.hubservice.application.dto.request.HubCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Hub;
import com.oingmaryho.business.hubservice.domain.repository.HubRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubAdminService {

	private final HubRepository hubRepository;
	private final HubApplicationMapper mapper;

	// TODO : Auditing 추가하기
	public HubCreateResponseServiceDto createHub(HubCreateRequestServiceDto requestDto) {
		Hub hub = mapper.toHub(requestDto);

		Hub savedHub = hubRepository.save(hub);
		return mapper.toHubCreateResponseServiceDto(savedHub);
	}
}
