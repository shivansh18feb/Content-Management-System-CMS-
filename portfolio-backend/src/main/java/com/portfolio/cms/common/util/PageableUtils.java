package com.portfolio.cms.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public final class PageableUtils {

    private PageableUtils() {}

    public static Pageable toSnakeCase(Pageable pageable) {
        return toSnakeCase(pageable, null, null);
    }

    public static Pageable toSnakeCase(Pageable pageable, String defaultSortColumn, Sort.Direction defaultDirection) {
        if (pageable == null) {
            return PageRequest.of(0, 10, Sort.by(defaultDirection != null ? defaultDirection : Sort.Direction.ASC, defaultSortColumn));
        }

        if (pageable.getSort().isUnsorted()) {
            if (defaultSortColumn != null) {
                return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(defaultDirection != null ? defaultDirection : Sort.Direction.ASC, defaultSortColumn));
            }
            return pageable;
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(new Sort.Order(order.getDirection(), camelToSnake(order.getProperty())));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
    }

    public static String camelToSnake(String str) {
        if (str == null) return null;
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
