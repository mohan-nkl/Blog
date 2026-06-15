package com.mohan.blog.restapis.dtos;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {

    /** Wraps page metadata around an already-mapped content list. */
    public static <T> PagedResponse<T> of(Page<?> page, List<T> content) {
        return new PagedResponse<>(
                content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}