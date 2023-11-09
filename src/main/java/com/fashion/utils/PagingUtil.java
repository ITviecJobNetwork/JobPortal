package com.fashion.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class PagingUtil {

    public static List<Object> getPages(int currentPage, int lastPage) {
        int _x = 2;
        Integer x = null;
        int start = currentPage - _x;
        int end = currentPage + _x + 1;

        List<Object> rangeWithDots = new ArrayList<>();
        for (int i = 1; i <= lastPage; i++) {
            if (i == 1 || i == lastPage || (i >= start && i < end)) {
                if (Objects.nonNull(x)) {
                    if (i - x == 2) {
                        rangeWithDots.add(x + 1 + "");
                    } else if (i - x != 1) {
                        rangeWithDots.add("...");
                    }
                }
                rangeWithDots.add(i);
                x = i;
            }
        }

        return rangeWithDots;
    }
}
