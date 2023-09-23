package com.fashion.dto.product;

import com.fashion.dto.base.FileItem;
import com.fashion.utils.ObjectUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class CreateProductRequest {
    private Long id;

    @NotBlank(message = "Mã sản phẩm không được trống")
    private String code;

    @NotBlank(message = "Tên sản phẩm không được trống")
    private String name;

    @NotNull(message = "Loại sản phẩm không được trống")
    private Long categoryId;
    private FileItem image;

    @NotBlank(message = "Mô tả ngắn không được trống")
    @Length(max = 200, message = "Mô tả ngán tối đa 200 ký tự")
    private String shortDescription;

    private String description;
    private String colors;
    private String sizes;
    private Boolean isShowHome;
    private List<ColorProductDTO> colorProductDTOList;
    private Map<String, List<ProductDetailRequest>> productDetails;

    public void setColorSize(HttpServletRequest request) {
        if (StringUtils.isBlank(this.colors) || StringUtils.isBlank(this.sizes)) {
            throw new IllegalArgumentException("Sizes and Colors is mandatory");
        }
        this.colorProductDTOList = new ArrayList<>();
        this.productDetails = new LinkedHashMap<>();

        String[] colorArray = this.colors.split(",\\s*");
        String[] sizeArray = this.sizes.split(",\\s*");

        for (String color : colorArray) {
            FileItem fileItem = ObjectUtil.mappingRequestPart(request, color, false);
            ColorProductDTO colorProductDTO = ColorProductDTO.builder()
                    .image(fileItem)
                    .color(color)
                    .build();
            this.colorProductDTOList.add(colorProductDTO);

            List<ProductDetailRequest> detailRequests = new ArrayList<>();
            for (String size : sizeArray) {
                String quantity = request.getParameter(String.format("quantity-%s-%s", color, size));
                String cost = request.getParameter(String.format("cost-%s-%s", color, size));
                String price = request.getParameter(String.format("price-%s-%s", color, size));
                String discount = request.getParameter(String.format("discount-%s-%s", color, size));
                ProductDetailRequest productDetailRequest = ProductDetailRequest.builder()
                        .color(colorProductDTO)
                        .size(size)
                        .discount(NumberUtils.toInt(discount, 0))
                        .cost(new BigDecimal(cost))
                        .price(new BigDecimal(price))
                        .quantity(NumberUtils.toInt(quantity, 0))
                        .build();
                detailRequests.add(productDetailRequest);
            }
            this.productDetails.put(color, detailRequests);
        }

    }
}
