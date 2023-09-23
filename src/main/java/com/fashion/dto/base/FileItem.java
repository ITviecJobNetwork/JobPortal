package com.fashion.dto.base;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileItem {
    private String name;
    private String contentType;
    private byte[] bytes;
}
