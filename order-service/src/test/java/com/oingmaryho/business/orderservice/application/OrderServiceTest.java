package com.oingmaryho.business.orderservice.application;

import com.oingmaryho.business.orderservice.application.dto.OrderServiceDto;
import com.oingmaryho.business.orderservice.application.dto.OrdersServiceDto;
import com.oingmaryho.business.orderservice.config.pageable.PageableConfig;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.domain.Status;
import com.oingmaryho.business.orderservice.infrastructure.OrderRepository;
import com.oingmaryho.business.orderservice.presentation.OrderPresentationMapper;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDetailDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private Cache cache;

    @Mock
    private Pageable pageable;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private PageableConfig pageableConfig;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderServiceDto orderServiceDto;

    @Mock
    private OrdersServiceDto ordersServiceDto;

    @Mock
    private OrderApplicationMapper orderApplicationMapper;

    @Mock
    private OrderPresentationMapper orderPresentationMapper;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("마스터 - 전체 조회 (캐시 미스)")
        //캐시 미스 상황을 테스트 하는것이 맞음.
    void testGetOrdersCacheMiss() {
        // given
        when(cacheManager.getCache("orders")).thenReturn(cache); // CacheManager 설정 추가

        when(ordersServiceDto.productName()).thenReturn("");
        when(ordersServiceDto.recipientName()).thenReturn("");
        when(ordersServiceDto.requesterName()).thenReturn("");
        when(ordersServiceDto.isDeleted()).thenReturn(false);
        when(pageableConfig.customPageable(1, null, null)).thenReturn(pageable);
        String cacheKey = makeOrdersCacheKey(ordersServiceDto);

        when(cache.get(cacheKey, Page.class)).thenReturn(null); // 캐시 미스 설정
        when(ordersServiceDto.customPageable()).thenReturn(pageable);

        List<Order> orderList = Collections.emptyList(); // 빈 리스트 또는 가상의 데이터 리스트
        Page<Order> orderPage = new PageImpl<>(orderList);
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        // when: getOrders 호출
        Page<OrderDto> result = orderService.getOrders(ordersServiceDto);

        // then: 캐시된 데이터가 반환되었는지 확인
        assertNotNull(result);
        verify(cache, times(1)).get(cacheKey, Page.class);
        verify(orderRepository, times(1)).findAll(any(Pageable.class)); // DB 조회는 1번 확인되어야 함
    }

    @Test
    @DisplayName("마스터 - 전체 조회 (캐시 히트)")
    void testGetOrdersCacheHit() {
        // given
        when(ordersServiceDto.productName()).thenReturn("");
        when(ordersServiceDto.recipientName()).thenReturn("");
        when(ordersServiceDto.requesterName()).thenReturn("");
        when(ordersServiceDto.isDeleted()).thenReturn(false);
        when(pageableConfig.customPageable(1, null, null)).thenReturn(pageable);
        String cacheKey = makeOrdersCacheKey(ordersServiceDto);
        List<OrderDto> orderDtoList = List.of(createOrderDto());
        Page<OrderDto> cachedOrderDto = new PageImpl<>(orderDtoList); // 가상의 캐시 데이터
        cache.put(cacheKey, cachedOrderDto);

        when(cacheManager.getCache("orders")).thenReturn(cache);
        when(ordersServiceDto.customPageable()).thenReturn(pageable);
        when(cache.get(cacheKey, Page.class)).thenReturn(cachedOrderDto);

        // when: getOrders 호출
        Page<OrderDto> result = orderService.getOrders(ordersServiceDto);

        // then: 캐시된 데이터가 반환되었는지 확인
        assertNotNull(result);
        verify(cache, times(1)).get(cacheKey, Page.class); // cache.get 이 한 번 호출되었는지 확인
        verify(orderRepository, times(0)).findAll(any(Pageable.class)); // DB 조회는 0번 확인되어야 함
    }

    @Test
    @DisplayName("마스터 - 상세 조회 (캐시 미스)")
    void testGetOrderCacheMiss() {
        // given
        UUID orderId = UUID.fromString("0ab88134-13ce-4f24-963e-4f8ad716a69e");
        OrderServiceDto orderServiceDto = createOrderServiceDto(orderId);
        Order order = createOrder();
        OrderDto expectedOrderDto = createOrderDto();

        when(cacheManager.getCache("order")).thenReturn(cache); // CacheManager 설정 추가

        when(cache.get(orderId, OrderDto.class)).thenReturn(null);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderPresentationMapper.toOrderDto(any(Order.class), anyList())).thenReturn(expectedOrderDto);

        // when
        OrderDto result = orderService.getOrder(orderServiceDto);

        // then
        assertNotNull(result);
        assertEquals(expectedOrderDto, result);
        verify(orderRepository, times(1)).findById(orderId); // DB 조회는 1번 확인되어야 함
        verify(cache, times(0)).put(orderId, expectedOrderDto); // 캐시 저장 확인
    }

    @Test
    @DisplayName("마스터 - 상세 조회 (캐시 히트)")
    void testGetOrderCacheHit() {
        // given
        when(cacheManager.getCache("order")).thenReturn(cache);

        UUID orderId = UUID.fromString("0ab88134-13ce-4f24-963e-4f8ad716a69e");
        OrderServiceDto orderServiceDto = createOrderServiceDto(orderId);
        OrderDto cachedOrderDto = createOrderDto();

        when(cache.get(orderId, OrderDto.class)).thenReturn(cachedOrderDto);
        when(orderPresentationMapper.toOrderDto(any(Order.class), anyList())).thenReturn(cachedOrderDto);

        // when
        OrderDto result = orderService.getOrder(orderServiceDto);

        // then
        assertNotNull(result);
        assertEquals(cachedOrderDto, result);
        verify(cache, times(1)).get(orderId, OrderDto.class);
        verify(orderRepository, times(0)).findById(orderId);
    }

    OrderDto createOrderDto() {
        OrderDetailDto orderDetailDto = new OrderDetailDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "테스트 요청자 이름",
            UUID.randomUUID(),
            UUID.randomUUID(),
            "테스트 상품1 이름",
            2,
            1000
        );

        List<OrderDetailDto> orderDetails = List.of(orderDetailDto);

        return new OrderDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "테스트1",
            Status.COMPLETE,
            1000,
            "요청 테스트 1",
            false,
            null,
            null,
            1L,
            LocalDateTime.now(),
            null,
            null,
            orderDetails
        );
    }

    private OrderServiceDto createOrderServiceDto(UUID orderId) {
        return new OrderServiceDto(
            orderId
        );
    }

    String makeOrdersCacheKey(OrdersServiceDto ordersServiceDto) {
        String productName = ordersServiceDto.productName();
        productName = (productName == null) ? "" : productName;

        String recipientName = ordersServiceDto.recipientName();
        recipientName = (recipientName == null) ? "" : recipientName;

        String requesterName = ordersServiceDto.requesterName();
        requesterName = (requesterName == null) ? "" : requesterName;

        Pageable customPageable = pageableConfig.customPageable(1, null, null);
        int customPageableHashCode = (customPageable != null) ? customPageable.hashCode() : 0;

        return "Orders_" +
            productName.hashCode() +
            recipientName.hashCode() +
            requesterName.hashCode() +
            ordersServiceDto.isDeleted().hashCode() +
            customPageableHashCode;
    }

    private Order createOrder() {
        OrderDetail orderDetail = OrderDetail.builder()
            .id(UUID.randomUUID())
            .requesterId(UUID.randomUUID())
            .requesterName("테스트 요청자 이름")
            .shippingId(UUID.randomUUID())
            .productId(UUID.randomUUID())
            .productName("테스트 상품1 이름")
            .quantity(2)
            .price(1000)
            .build();

        return Order.builder()
            .id(UUID.randomUUID())
            .recipientId(UUID.randomUUID())
            .recipientName("테스트 수령인")
            .status(Status.COMPLETE)
            .totalPrice(1000)
            .requests("문 앞에 놔주세요.")
            .isDeleted(false)
            .orderDetails(List.of(orderDetail))
            .build();
    }
}