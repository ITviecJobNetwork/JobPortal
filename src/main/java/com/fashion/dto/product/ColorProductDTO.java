package com.fashion.dto.product;

import com.fashion.dto.base.FileItem;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(exclude = {"color", "image", "imageUrl"})
public class ColorProductDTO {
    private Long id;
    private String color;
    private FileItem image;
    private String imageUrl;
}
