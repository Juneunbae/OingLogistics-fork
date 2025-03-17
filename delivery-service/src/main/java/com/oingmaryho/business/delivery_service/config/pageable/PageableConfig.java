package com.oingmaryho.business.delivery_service.config.pageable;

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

    private static final int DEFAULT_SIZE = 10;
    private static final String DEFAULT_SORT_DIRECTION = "desc";

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setFallbackPageable(PageRequest.of(0, 10, ascSort()));
        resolvers.add(resolver);
    }

    public Pageable customPageable(Integer page, Integer size, String sortDirection) {
        int currentPage = (page != null && page > 0) ? page - 1 : 0;
        int currentSize = (size != null) ? size : DEFAULT_SIZE;
        String currentSortDirection = (sortDirection != null) ? sortDirection.toLowerCase() : DEFAULT_SORT_DIRECTION;

        Sort sort = DEFAULT_SORT_DIRECTION.equalsIgnoreCase(currentSortDirection) ? DescSort() : ascSort();
        return PageRequest.of(currentPage, currentSize, sort);
    }

    private Sort ascSort() {
        return Sort.by(
                Sort.Order.asc(SortConstants.CREATED_AT),
                Sort.Order.asc(SortConstants.UPDATED_AT)
        );
    }

    private Sort DescSort() {
        return Sort.by(
                Sort.Order.desc(SortConstants.CREATED_AT),
                Sort.Order.desc(SortConstants.UPDATED_AT)
        );
    }
}