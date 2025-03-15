package com.oingmaryho.business.hubservice.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteSearchRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hub-routes")
@RequiredArgsConstructor
public class HubRouteController {

	@GetMapping
	public ResponseEntity<?> searchHubRoutes(
		@RequestBody HubRouteSearchRequestDto requestDto,
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection
	) {
		return null;
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getHubRouteById(@PathVariable Long id) {
		return null;
	}
}
