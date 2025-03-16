package com.oingmaryho.business.orderservice.config.pageable;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class PageableConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setFallbackPageable(PageRequest.of(0, 10, ascSort()));
        resolvers.add(resolver);
    }

    public Sort ascSort() {
        return Sort.by(
            Sort.Order.asc(SortConstants.CREATED_AT),
            Sort.Order.asc(SortConstants.UPDATED_AT)
        );
    }

    public Sort DescSort() {
        return Sort.by(
            Sort.Order.desc(SortConstants.CREATED_AT),
            Sort.Order.desc(SortConstants.UPDATED_AT)
        );
    }

    public Pageable customPageable(Integer page, Integer size, String sortDirection) {
        int defaultSize = 10; // 기본 페이지 크기
        String defaultSortDirection = "asc"; // 기본 정렬 방향

        int currentPage = (page != null && page > 0) ? page - 1 : 0;
        int currentSize = (size != null) ? size : defaultSize;
        String currentSortDirection = (sortDirection != null) ? sortDirection.toLowerCase() : defaultSortDirection;

        Sort sort = "desc".equalsIgnoreCase(currentSortDirection) ? DescSort() : ascSort();
        return PageRequest.of(currentPage, currentSize, sort);
    }
}