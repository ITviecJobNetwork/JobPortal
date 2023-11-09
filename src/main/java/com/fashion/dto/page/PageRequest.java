package com.fashion.dto.page;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PageRequest<T> {
    private Integer page;
    private Integer pageSize;
    private T data;
}