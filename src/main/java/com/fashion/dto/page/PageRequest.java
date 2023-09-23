package com.fashion.dto.page;

import lombok.Data;

@Data
public class PageRequest<T> {
    private Integer page;
    private Integer pageSize;
    private T data;
}