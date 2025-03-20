package com.oingmaryho.business.delivery_service.application;

import com.oingmaryho.business.delivery_service.application.dto.mapper.DeliveryApplicationMapper;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.application.service.DeliveryService;
import com.oingmaryho.business.delivery_service.domain.entity.Delivery;
import com.oingmaryho.business.delivery_service.domain.entity.DeliveryManager;
import com.oingmaryho.business.delivery_service.domain.entity.DeliveryRoute;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;
import com.oingmaryho.business.delivery_service.domain.type.UserRoleType;
import com.oingmaryho.business.delivery_service.infrastructure.repository.DeliveryRepository;
import com.oingmaryho.business.delivery_service.utils.PageableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    private static final Logger log = LoggerFactory.getLogger(DeliveryServiceTest.class);
    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryApplicationMapper mapper;


    private UUID companyDeliveryManagerId1;
    private UUID companyDeliveryManagerId2;

    private UUID hubDeliveryManagerId1;
    private UUID hubDeliveryManagerId2;

    private UUID orderId1;
    private UUID orderId2;
    private UUID orderId3;

    private UUID routeId1;
    private UUID routeId2;
    private UUID routeId3;

    private UUID deliveryId1;
    private UUID deliveryId2;
    private UUID deliveryId3;

    private UUID hubId1;
    private UUID hubId2;
    private UUID hubId3;
    private UUID hubId4;

    private String address1;
    private String address2;

    private String receiver1;
    private String receiver2;

    private String receiverSlackId1;
    private String receiverSlackId2;

    private DeliveryStatus deliveryStatus1;
    private DeliveryStatus deliveryStatus2;
    private DeliveryStatus deliveryStatus3;
    private DeliveryStatus deliveryStatus4;
    private DeliveryStatus deliveryStatus5;

    private String companyDeliveryManagerSlackId1;
    private String companyDeliveryManagerSlackId2;

    private String hubDeliveryManagerSlackId1;
    private String hubDeliveryManagerSlackId2;

    private UUID companyId1;
    private UUID companyId2;

    private Long userId1;
    private Long userId2;
    private Long userId3;
    private Long userId4;
    private Long userId5;

    private UserRoleType role1;
    private UserRoleType role2;
    private UserRoleType role3;
    private UserRoleType role4;
    private UserRoleType role5;

    private DeliveryManagerType managerType1;
    private DeliveryManagerType managerType2;

    private DeliveryRouteStatus routeStatus1;
    private DeliveryRouteStatus routeStatus2;
    private DeliveryRouteStatus routeStatus3;


    @BeforeEach
    void setUp() {
        companyDeliveryManagerId1 = UUID.randomUUID();
        companyDeliveryManagerId2 = UUID.randomUUID();

        hubDeliveryManagerId1 = UUID.randomUUID();
        hubDeliveryManagerId2 = UUID.randomUUID();

        orderId1 = UUID.randomUUID();
        orderId2 = UUID.randomUUID();
        orderId3 = UUID.randomUUID();

        routeId1 = UUID.randomUUID();
        routeId2 = UUID.randomUUID();
        routeId3 = UUID.randomUUID();

        deliveryId1 = UUID.randomUUID();
        deliveryId2 = UUID.randomUUID();
        deliveryId3 = UUID.randomUUID();

        hubId1 = UUID.randomUUID();
        hubId2 = UUID.randomUUID();
        hubId3 = UUID.randomUUID();
        hubId4 = UUID.randomUUID();

        address1 = "배송지1";
        address2 = "배송지2";

        receiver1 = "수령인 이름1";
        receiver2 = "수령인 이름2";
        receiverSlackId1 = "수령인 슬랙 아이디1";
        receiverSlackId2 = "수령인 슬랙 아이디2";

        deliveryStatus1 = DeliveryStatus.HUB_WAITING;
        deliveryStatus2 = DeliveryStatus.HUB_MOVING;
        deliveryStatus3 = DeliveryStatus.HUB_ARRIVED;
        deliveryStatus4 = DeliveryStatus.COMPANY_MOVING;
        deliveryStatus5 = DeliveryStatus.COMPLETE;

        companyDeliveryManagerSlackId1 = "업체 배송 담당자 슬랙 아이디1";
        companyDeliveryManagerSlackId2 = "업체 배송 담당자 슬랙 아이디2";

        hubDeliveryManagerSlackId1 = "허브 배송 담당자 슬랙 아이디1";
        hubDeliveryManagerSlackId2 = "허브 배송 담당자 슬랙 아이디2";

        companyId1 = UUID.randomUUID();
        companyId2 = UUID.randomUUID();

        userId1 = 1L;
        userId2 = 2L;
        userId3 = 3L;
        userId4 = 4L;

        role1 = UserRoleType.HUB_MANAGER;
        role2 = UserRoleType.HUB_DELIVERY_MANAGER;
        role3 = UserRoleType.COMPANY_DELIVERY_MANAGER;
        role4 = UserRoleType.COMPANY_MANAGER;
        role5 = UserRoleType.MASTER;

        managerType1 = DeliveryManagerType.HUB_DELIVERY_MANAGER;
        managerType2 = DeliveryManagerType.COMPANY_DELIVERY_MANAGER;

        routeStatus1 = DeliveryRouteStatus.HUB_WAITING;
        routeStatus2 = DeliveryRouteStatus.HUB_MOVING;
        routeStatus3 = DeliveryRouteStatus.HUB_ARRIVED;

    }

    @Test
    @DisplayName("배송 상세 조회: 업체 배송 담당자는 본인의 배송을 조회할 수 있다.")
    public void 배송조회_업체배송담당자() {
        //given
        DeliveryManager manager = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(1)
                .build();

        Delivery delivery = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager)
                .build();

        DeliveryDetailRequestServiceDto requestDto = new DeliveryDetailRequestServiceDto(
                deliveryId1
        );
        DeliveryResponseServiceDto responseDto = new DeliveryResponseServiceDto(
                deliveryId1,
                deliveryStatus1,
                hubId1,
                hubId2,
                address1,
                receiver1,
                receiverSlackId1,
                companyDeliveryManagerId1,
                Boolean.FALSE
        );

        when(mapper.toDeliveryResponseServiceDto(any(Delivery.class)))
                .thenReturn(responseDto);

        when(deliveryRepository.findByIdAndIsDeletedFalse(deliveryId1))
                .thenReturn(Optional.of(delivery));

        //when
        DeliveryResponseServiceDto result = deliveryService.GetDeliveryDetail(
                userId1,
                role3,
                requestDto
        );

        //then
        assertThat(result).isNotNull()
                .extracting(
                        DeliveryResponseServiceDto::id,
                        DeliveryResponseServiceDto::status,
                        DeliveryResponseServiceDto::departureHubId,
                        DeliveryResponseServiceDto::destinationHubId,
                        DeliveryResponseServiceDto::address,
                        DeliveryResponseServiceDto::receiver,
                        DeliveryResponseServiceDto::receiverSlackId,
                        DeliveryResponseServiceDto::managerId,
                        DeliveryResponseServiceDto::isDeleted
                )
                .containsExactlyInAnyOrder(
                        deliveryId1,
                        deliveryStatus1,
                        hubId1,
                        hubId2,
                        address1,
                        receiver1,
                        receiverSlackId1,
                        companyDeliveryManagerId1,
                        Boolean.FALSE
                );
        verify(deliveryRepository, times(1))
                .findByIdAndIsDeletedFalse(deliveryId1);

    }

    @Test
    @DisplayName("배송 상세 조회: 허브 배송 담당자는 본인의 배송을 조회할 수 있다.")
    public void 배송조회_허브배송담당자() {
        //given
        DeliveryManager manager = DeliveryManager.builder()
                .id(hubDeliveryManagerId1)
                .slackId(hubDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(null)
                .managerId(userId1)
                .type(managerType1)
                .sequence(1)
                .build();

        Delivery delivery = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager)
                .build();

        DeliveryDetailRequestServiceDto requestDto = new DeliveryDetailRequestServiceDto(
                deliveryId1
        );
        DeliveryResponseServiceDto responseDto = new DeliveryResponseServiceDto(
                deliveryId1,
                deliveryStatus1,
                hubId1,
                hubId2,
                address1,
                receiver1,
                receiverSlackId1,
                hubDeliveryManagerId1,
                Boolean.FALSE
        );

        when(mapper.toDeliveryResponseServiceDto(any(Delivery.class)))
                .thenReturn(responseDto);

        when(deliveryRepository.findByIdAndIsDeletedFalse(deliveryId1))
                .thenReturn(Optional.of(delivery));

        //when
        DeliveryResponseServiceDto result = deliveryService.GetDeliveryDetail(
                userId1,
                role2,
                requestDto
        );

        //then
        assertThat(result).isNotNull()
                .extracting(
                        DeliveryResponseServiceDto::id,
                        DeliveryResponseServiceDto::status,
                        DeliveryResponseServiceDto::departureHubId,
                        DeliveryResponseServiceDto::destinationHubId,
                        DeliveryResponseServiceDto::address,
                        DeliveryResponseServiceDto::receiver,
                        DeliveryResponseServiceDto::receiverSlackId,
                        DeliveryResponseServiceDto::managerId,
                        DeliveryResponseServiceDto::isDeleted
                )
                .containsExactlyInAnyOrder(
                        deliveryId1,
                        deliveryStatus1,
                        hubId1,
                        hubId2,
                        address1,
                        receiver1,
                        receiverSlackId1,
                        hubDeliveryManagerId1,
                        Boolean.FALSE
                );
        verify(deliveryRepository, times(1))
                .findByIdAndIsDeletedFalse(deliveryId1);

    }

    @Test
    @DisplayName("배송 검색: 허브 관리자는 본인 허브의 배송을 검색할 수 있다.")
    public void 배송검색_허브관리자() {
        //given
        Pageable pageable = PageableUtils.customPageable(1, 10, null, null);
        DeliverySearchRequestServiceDto requestDto = new DeliverySearchRequestServiceDto(
                hubId1,
                null,
                null,
                Boolean.FALSE,
                pageable
        );

        DeliveryManager manager1 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(null)
                .build();

        DeliveryManager manager2 = DeliveryManager.builder()
                .id(hubDeliveryManagerId1)
                .slackId(hubDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(null)
                .managerId(userId2)
                .type(managerType1)
                .sequence(1)
                .build();

        Delivery delivery1 = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        Delivery delivery2 = Delivery.builder()
                .id(deliveryId2)
                .status(deliveryStatus2)
                .orderId(orderId2)
                .departureHubId(hubId1)
                .destinationHubId(hubId3)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager2)
                .build();

        Page<Delivery> page = new PageImpl<>(List.of(delivery1, delivery2), pageable, 2);

        DeliveryResponseServiceDto responseDto1 = new DeliveryResponseServiceDto(
                deliveryId1,
                deliveryStatus1,
                hubId1,
                hubId2,
                address1,
                receiver1,
                receiverSlackId1,
                companyDeliveryManagerId1,
                Boolean.FALSE
        );

        DeliveryResponseServiceDto responseDto2 = new DeliveryResponseServiceDto(
                deliveryId2,
                deliveryStatus2,
                hubId1,
                hubId3,
                address1,
                receiver1,
                receiverSlackId1,
                hubDeliveryManagerId1,
                Boolean.FALSE
        );

        when(deliveryRepository.searchDelivery(any(), eq(pageable))).thenReturn(page);
        when(mapper.toDeliveryResponseServiceDto(delivery1)).thenReturn(responseDto1);
        when(mapper.toDeliveryResponseServiceDto(delivery2)).thenReturn(responseDto2);

        //when
        Page<DeliveryResponseServiceDto> result = deliveryService.GetDeliveriesBySearch(
                userId1,
                role1,
                requestDto
        );

        //then
        assertThat(result.getContent()).hasSize(2)
                        .containsExactly(responseDto1, responseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(deliveryRepository, times(1))
                .searchDelivery(any(), any());
    }

    @Test
    @DisplayName("배송 검색: 허브 배송 담당자는 본인 배송을 검색할 수 있다.")
    public void 배송검색_허브배송담당자() {
        //given
        Pageable pageable = PageableUtils.customPageable(1, 10, null, null);
        DeliverySearchRequestServiceDto requestDto = new DeliverySearchRequestServiceDto(
                null,
                null,
                null,
                Boolean.FALSE,
                pageable
        );

        // 허브 배송 담당자
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(hubDeliveryManagerId1)
                .slackId(hubDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(null)
                .managerId(userId2)
                .type(managerType1)
                .sequence(1)
                .build();

        // 업체 배송 담당자
        DeliveryManager manager2 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId2)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(1)
                .build();

        // 배송
        Delivery delivery1 = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager2)
                .build();

        // 배송 경로
        DeliveryRoute route1 = DeliveryRoute.builder()
                .id(routeId1)
                .delivery(delivery1)
                .sequence(1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .status(routeStatus1)
                .estimatedDistance(12.1234)
                .estimatedTime(1)
                .manager(manager1)
                .build();

        Page<Delivery> page = new PageImpl<>(List.of(delivery1), pageable, 1);

        DeliveryResponseServiceDto responseDto1 = new DeliveryResponseServiceDto(
                deliveryId1,
                deliveryStatus1,
                hubId1,
                hubId2,
                address1,
                receiver1,
                receiverSlackId1,
                companyDeliveryManagerId1,
                Boolean.FALSE
        );

        when(deliveryRepository.searchDelivery(any(), eq(pageable))).thenReturn(page);
        when(mapper.toDeliveryResponseServiceDto(delivery1)).thenReturn(responseDto1);

        //when
        Page<DeliveryResponseServiceDto> result = deliveryService.GetDeliveriesBySearch(
                userId2,
                role2,
                requestDto
        );

        //then
        assertThat(result.getContent()).hasSize(1)
                .containsExactly(responseDto1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(deliveryRepository, times(1))
                .searchDelivery(any(), any());
    }

    @Test
    @DisplayName("배송 검색: 업체 배송 담당자는 본인 배송을 검색할 수 있다.")
    public void 배송검색_업체배송담당자() {
        //given
        Pageable pageable = PageableUtils.customPageable(1, 10, null, null);
        DeliverySearchRequestServiceDto requestDto = new DeliverySearchRequestServiceDto(
                null,
                null,
                null,
                Boolean.FALSE,
                pageable
        );

        // 업체 배송 담당자
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(null)
                .build();

        Delivery delivery1 = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        Delivery delivery2 = Delivery.builder()
                .id(deliveryId2)
                .status(deliveryStatus2)
                .orderId(orderId2)
                .departureHubId(hubId1)
                .destinationHubId(hubId3)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        Page<Delivery> page = new PageImpl<>(List.of(delivery1, delivery2), pageable, 2);

        DeliveryResponseServiceDto responseDto1 = new DeliveryResponseServiceDto(
                deliveryId1,
                deliveryStatus1,
                hubId1,
                hubId2,
                address1,
                receiver1,
                receiverSlackId1,
                companyDeliveryManagerId1,
                Boolean.FALSE
        );

        DeliveryResponseServiceDto responseDto2 = new DeliveryResponseServiceDto(
                deliveryId2,
                deliveryStatus2,
                hubId1,
                hubId3,
                address1,
                receiver1,
                receiverSlackId1,
                companyDeliveryManagerId1,
                Boolean.FALSE
        );

        when(deliveryRepository.searchDelivery(any(), eq(pageable))).thenReturn(page);
        when(mapper.toDeliveryResponseServiceDto(delivery1)).thenReturn(responseDto1);
        when(mapper.toDeliveryResponseServiceDto(delivery2)).thenReturn(responseDto2);

        //when
        Page<DeliveryResponseServiceDto> result = deliveryService.GetDeliveriesBySearch(
                userId3,
                role3,
                requestDto
        );

        //then
        assertThat(result.getContent()).hasSize(2)
                .containsExactly(responseDto1, responseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(deliveryRepository, times(1))
                .searchDelivery(any(), any());
    }

    @Test
    @DisplayName("배송 검색: 업체 담당자는 본인 업체에 속한 업체 배송을 검색할 수 있다.")
    public void 배송검색_업체담당자() {
        //given
        Pageable pageable = PageableUtils.customPageable(1, 10, null, null);
        DeliverySearchRequestServiceDto requestDto = new DeliverySearchRequestServiceDto(
                null,
                companyId1,
                null,
                Boolean.FALSE,
                pageable
        );

        // 업체 배송 담당자
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId2)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(1)
                .build();

        // 업체 배송 담당자
        DeliveryManager manager2 = DeliveryManager.builder()
                .id(companyDeliveryManagerId2)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId3)
                .companyId(companyId1)
                .managerId(userId2)
                .type(managerType2)
                .sequence(2)
                .build();

        Delivery delivery1 = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        Delivery delivery2 = Delivery.builder()
                .id(deliveryId2)
                .status(deliveryStatus1)
                .orderId(orderId2)
                .departureHubId(hubId1)
                .destinationHubId(hubId3)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager2)
                .build();

        Page<Delivery> page = new PageImpl<>(List.of(delivery1, delivery2), pageable, 2);

        DeliveryResponseServiceDto responseDto1 = new DeliveryResponseServiceDto(
                deliveryId1,
                deliveryStatus1,
                hubId1,
                hubId2,
                address1,
                receiver1,
                receiverSlackId1,
                companyDeliveryManagerId1,
                Boolean.FALSE
        );

        DeliveryResponseServiceDto responseDto2 = new DeliveryResponseServiceDto(
                deliveryId2,
                deliveryStatus1,
                hubId1,
                hubId3,
                address1,
                receiver1,
                receiverSlackId1,
                companyDeliveryManagerId2,
                Boolean.FALSE
        );

        when(deliveryRepository.searchDelivery(any(), eq(pageable))).thenReturn(page);
        when(mapper.toDeliveryResponseServiceDto(delivery1)).thenReturn(responseDto1);
        when(mapper.toDeliveryResponseServiceDto(delivery2)).thenReturn(responseDto2);

        //when
        Page<DeliveryResponseServiceDto> result = deliveryService.GetDeliveriesBySearch(
                userId4,
                role4,
                requestDto
        );

        //then
        assertThat(result.getContent()).hasSize(2)
                .containsExactly(responseDto1, responseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(deliveryRepository, times(1))
                .searchDelivery(any(), any());
    }

    @Test
    @DisplayName("배송 경로 상세 조회: 허브 배송 담당자는 본인의 배송 경로를 조회할 수 있다.")
    public void 배송경로상세조회_허브배송담당자() {
        //given
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(hubDeliveryManagerId1)
                .slackId(hubDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(null)
                .managerId(userId1)
                .type(managerType1)
                .sequence(1)
                .build();

        DeliveryManager manager2 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId2)
                .companyId(companyId1)
                .managerId(userId2)
                .type(managerType2)
                .sequence(1)
                .build();

        Delivery delivery = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager2)
                .build();

        // 배송 경로
        DeliveryRoute route = DeliveryRoute.builder()
                .id(routeId1)
                .delivery(delivery)
                .sequence(1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .status(routeStatus1)
                .estimatedDistance(12.1234)
                .estimatedTime(1)
                .manager(manager1)
                .build();

        DeliveryRouteDetailRequestServiceDto requestDto = new DeliveryRouteDetailRequestServiceDto(
                routeId1
        );

        DeliveryRouteResponseServiceDto responseDto = new DeliveryRouteResponseServiceDto(
                routeId1,
                deliveryId1,
                1,
                hubId1,
                hubId2,
                routeStatus1,
                12.1234,
                1,
                null,
                null,
                hubDeliveryManagerId1,
                Boolean.FALSE
        );

        when(mapper.toRouteResponseServiceDto(any(DeliveryRoute.class)))
                .thenReturn(responseDto);

        when(deliveryRepository.findRouteByIdAndIsDeleted(routeId1))
                .thenReturn(Optional.of(route));

        //when
        DeliveryRouteResponseServiceDto result = deliveryService.GetDeliveryRouteDetail(
                userId2,
                role2,
                requestDto
        );

        //then
        assertThat(result).isNotNull()
                .extracting(
                        DeliveryRouteResponseServiceDto::id,
                        DeliveryRouteResponseServiceDto::deliveryId,
                        DeliveryRouteResponseServiceDto::sequence,
                        DeliveryRouteResponseServiceDto::departureHubId,
                        DeliveryRouteResponseServiceDto::destinationHubId,
                        DeliveryRouteResponseServiceDto::status,
                        DeliveryRouteResponseServiceDto::estimatedDistance,
                        DeliveryRouteResponseServiceDto::estimatedTime,
                        DeliveryRouteResponseServiceDto::managerId,
                        DeliveryRouteResponseServiceDto::isDeleted
                )
                .containsExactlyInAnyOrder(
                        routeId1,
                        deliveryId1,
                        1,
                        hubId1,
                        hubId2,
                        routeStatus1,
                        12.1234,
                        1,
                        hubDeliveryManagerId1,
                        Boolean.FALSE
                );
        verify(deliveryRepository, times(1))
                .findRouteByIdAndIsDeleted(routeId1);
    }

    @Test
    @DisplayName("배송 경로 검색: 업체 배송 담당자는 배송 경로를 검색할 수 있다.")
    public void 배송경로검색_업체배송담당자() {
        //given
        Pageable pageable = PageableUtils.customPageable(1, 10, null, null);
        DeliveryRouteSearchRequestServiceDto requestDto = new DeliveryRouteSearchRequestServiceDto(
                deliveryId1,
                null,
                null,
                null,
                Boolean.FALSE,
                pageable
        );

        // 업체 배송 담당자
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(1)
                .build();

        // 허브 배송 담당자
        DeliveryManager manager2 = DeliveryManager.builder()
                .id(hubDeliveryManagerId1)
                .slackId(hubDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(null)
                .managerId(userId2)
                .type(managerType1)
                .sequence(1)
                .build();

        // 허브 배송 담당자
        DeliveryManager manager3 = DeliveryManager.builder()
                .id(hubDeliveryManagerId2)
                .slackId(hubDeliveryManagerSlackId2)
                .hubId(hubId2)
                .companyId(null)
                .managerId(userId3)
                .type(managerType1)
                .sequence(2)
                .build();

        Delivery delivery = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId3)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        // 배송 경로
        DeliveryRoute route1 = DeliveryRoute.builder()
                .id(routeId1)
                .delivery(delivery)
                .sequence(1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .status(routeStatus1)
                .estimatedDistance(12.1234)
                .estimatedTime(1)
                .manager(manager2)
                .build();

        // 배송 경로
        DeliveryRoute route2 = DeliveryRoute.builder()
                .id(routeId2)
                .delivery(delivery)
                .sequence(2)
                .departureHubId(hubId2)
                .destinationHubId(hubId3)
                .status(routeStatus1)
                .estimatedDistance(22.1234)
                .estimatedTime(2)
                .manager(manager3)
                .build();

        Page<DeliveryRoute> page = new PageImpl<>(List.of(route1, route2), pageable, 2);

        DeliveryRouteResponseServiceDto responseDto1 = new DeliveryRouteResponseServiceDto(
                routeId1,
                deliveryId1,
                1,
                hubId1,
                hubId2,
                routeStatus1,
                12.1234,
                1,
                null,
                null,
                hubDeliveryManagerId1,
                Boolean.FALSE
        );

        DeliveryRouteResponseServiceDto responseDto2 = new DeliveryRouteResponseServiceDto(
                routeId2,
                deliveryId1,
                1,
                hubId2,
                hubId3,
                routeStatus1,
                22.1234,
                2,
                null,
                null,
                hubDeliveryManagerId2,
                Boolean.FALSE
        );


        when(deliveryRepository.searchRoute(any(), eq(pageable))).thenReturn(page);
        when(mapper.toRouteResponseServiceDto(route1)).thenReturn(responseDto1);
        when(mapper.toRouteResponseServiceDto(route2)).thenReturn(responseDto2);

        //when
        Page<DeliveryRouteResponseServiceDto> result = deliveryService.GetDeliveryRoutesBySearch(
                userId1,
                role1,
                requestDto
        );

        //then
        assertThat(result.getContent()).hasSize(2)
                .containsExactly(responseDto1, responseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(deliveryRepository, times(1))
                .searchRoute(any(), any());
    }

    @Test
    @DisplayName("배송 수정 - 배송 정보를 수정한다.")
    public void 배송수정_마스터() {
        //given
        // 업체 배송 담당자
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(1)
                .build();

        Delivery delivery = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId3)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        DeliveryUpdateRequestServiceDto requestDto = new DeliveryUpdateRequestServiceDto(
                deliveryId1,
                receiver2,
                receiverSlackId2,
                null,
                null
        );
        DeliveryUpdateResponseServiceDto responseDto = new DeliveryUpdateResponseServiceDto(
                deliveryId1
        );

        when(mapper.toUpdateResponseServiceDto(any(UUID.class)))
                .thenReturn(responseDto);

        when(deliveryRepository.findByIdAndIsDeletedFalse(deliveryId1))
                .thenReturn(Optional.of(delivery));

        //when
        DeliveryUpdateResponseServiceDto result = deliveryService.updateDelivery(   // 마스터 시도
                userId5,
                role5,
                requestDto
        );

        //then
        assertThat(result).isNotNull()
                .extracting(DeliveryUpdateResponseServiceDto::id)
                .isEqualTo(deliveryId1);

        verify(deliveryRepository, times(1))
                .findByIdAndIsDeletedFalse(deliveryId1);

    }

    @Test
    @DisplayName("배송 상태 수정 - 배송 상태를 변경한다.")
    public void 배송상태수정_마스터() {
        //given
        // 업체 배송 담당자
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(1)
                .build();

        Delivery delivery = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId3)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        DeliveryUpdateStatusRequestServiceDto requestDto = new DeliveryUpdateStatusRequestServiceDto(
                deliveryId1,
                deliveryStatus2

        );
        DeliveryUpdateStatusResponseServiceDto responseDto = new DeliveryUpdateStatusResponseServiceDto(
                deliveryId1
        );

        when(mapper.toUpdateStatusResponseServiceDto(any(UUID.class)))
                .thenReturn(responseDto);

        when(deliveryRepository.findByIdAndIsDeletedFalse(deliveryId1))
                .thenReturn(Optional.of(delivery));

        //when
        DeliveryUpdateStatusResponseServiceDto result = deliveryService.updateStatusDelivery(   // 마스터 시도
                userId5,
                role5,
                requestDto
        );

        //then
        assertThat(result).isNotNull()
                .extracting(DeliveryUpdateStatusResponseServiceDto::id)
                .isEqualTo(deliveryId1);

        verify(deliveryRepository, times(1))
                .findByIdAndIsDeletedFalse(deliveryId1);
    }

    @Test
    @DisplayName("배송 삭제 - 배송을 삭제한다.")
    public void 배송삭제_마스터() {
        //given
        // 업체 배송 담당자
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(1)
                .build();

        Delivery delivery = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId3)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        DeliveryDeletionRequestServiceDto requestDto = new DeliveryDeletionRequestServiceDto(
                deliveryId1
        );

        when(deliveryRepository.findByIdAndIsDeletedFalse(deliveryId1))
                .thenReturn(Optional.of(delivery));

        //when
        deliveryService.deleteDelivery(   // 마스터 시도
                userId5,
                role5,
                requestDto
        );

        //then
        verify(deliveryRepository, times(1))
                .delete(any(Delivery.class));
    }

    @Test
    @DisplayName("배송 경로 상태 수정 - 배송 경로 상태가 목적지 허브 도착 상태로 수정될 때, 배송 상태도 수정된다.")
    public void 배송경로상태수정_마스터() {
        //given
        // 업체 배송 담당자
        DeliveryManager manager1 = DeliveryManager.builder()
                .id(companyDeliveryManagerId1)
                .slackId(companyDeliveryManagerSlackId1)
                .hubId(hubId2)
                .companyId(companyId1)
                .managerId(userId1)
                .type(managerType2)
                .sequence(1)
                .build();

        // 허브 배송 담당자
        DeliveryManager manager2 = DeliveryManager.builder()
                .id(hubDeliveryManagerId1)
                .slackId(hubDeliveryManagerSlackId1)
                .hubId(hubId1)
                .companyId(null)
                .managerId(userId2)
                .type(managerType1)
                .sequence(1)
                .build();

        Delivery delivery = Delivery.builder()
                .id(deliveryId1)
                .status(deliveryStatus1)
                .orderId(orderId1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .address(address1)
                .receiver(receiver1)
                .receiverSlackId(receiverSlackId1)
                .manager(manager1)
                .build();

        // 배송 경로
        DeliveryRoute route = DeliveryRoute.builder()
                .id(routeId1)
                .delivery(delivery)
                .sequence(1)
                .departureHubId(hubId1)
                .destinationHubId(hubId2)
                .status(routeStatus2)
                .estimatedDistance(12.1234)
                .estimatedTime(1)
                .manager(manager2)
                .build();

        DeliveryRouteUpdateStatusRequestServiceDto requestDto = new DeliveryRouteUpdateStatusRequestServiceDto(
                routeId1,
                routeStatus3

        );
        DeliveryRouteUpdateStatusResponseServiceDto responseDto = new DeliveryRouteUpdateStatusResponseServiceDto(
                routeId1
        );

        when(mapper.toUpdateRouteStatusResponseServiceDto(any(UUID.class)))
                .thenReturn(responseDto);

        when(deliveryRepository.findRouteByIdAndIsDeleted(routeId1))
                .thenReturn(Optional.of(route));

        when(deliveryRepository.findByIdAndIsDeletedFalse(deliveryId1))
                .thenReturn(Optional.of(delivery));

        //when
        DeliveryRouteUpdateStatusResponseServiceDto result = deliveryService.updateRouteStatusDelivery(   // 마스터 시도
                userId5,
                role5,
                requestDto
        );

        //then
        assertThat(result).isNotNull()
                .extracting(DeliveryRouteUpdateStatusResponseServiceDto::id)
                .isEqualTo(routeId1);

        verify(deliveryRepository, times(1))
                .findRouteByIdAndIsDeleted(routeId1);

        verify(deliveryRepository, times(1))
                .findByIdAndIsDeletedFalse(deliveryId1);

    }


}
