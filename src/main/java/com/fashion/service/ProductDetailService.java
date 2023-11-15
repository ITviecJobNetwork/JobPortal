package com.fashion.service;

import com.fashion.constant.ImageType;
import com.fashion.dao.ImageDao;
import com.fashion.dao.ProductDao;
import com.fashion.dao.ProductDetailDao;
import com.fashion.dto.base.Result;
import com.fashion.dto.product.ColorProductDTO;
import com.fashion.dto.product.ProductDetailRequest;
import com.fashion.dto.product.ProductResponse;
import com.fashion.entity.Image;
import com.fashion.entity.Product;
import com.fashion.entity.ProductDetail;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
public class ProductDetailService extends BaseService {
    private ProductDetailDao productDetailDao;
    private ProductDao productDao;
    private ImageDao imageDao;

    public Set<String> getAllSize() {
        return this.doSelect(session -> {
            return this.productDetailDao.findAll(session)
                    .stream()
                    .map(ProductDetail::getSize)
                    .collect(Collectors.toSet());
        });
    }

    public Result<Long> unlockOrLockDetail(Long id) {
        return this.tryCatchWithTransaction(session -> {
            ProductDetail productDetail = this.productDetailDao.findById(id, session).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy: " + id));
            productDetail.setActive( !productDetail.getActive() );
            this.productDetailDao.save(productDetail, session);

            if (productDetail.getActive()) {
                Product product = this.productDao.findById(productDetail.getProductId(), session).orElseThrow();
                if (!product.getActive()) {
                    product.setActive(true);
                    this.productDao.save(product, session);
                }
            } else {
                boolean allIsStatus = this.productDetailDao.findByProductId(productDetail.getProductId(), session)
                        .stream()
                        .filter(pd -> pd.getId() != productDetail.getId())
                        .allMatch(pd -> !pd.getActive());
                if (allIsStatus) {
                    Product product = this.productDao.findById(productDetail.getProductId(), session).orElseThrow();
                    product.setActive(false);
                    this.productDao.save(product, session);
                }
            }
            return Result.<Long>builder()
                    .isSuccess(true)
                    .message((productDetail.getActive() ? "Mở khóa" : "Khóa") + " sản phẩm thành công")
                    .build();
        }, id);
    }

    public Result<ProductResponse> getDetailProduct(String productCode) {
        return this.tryCatchWithDoSelect(session -> {
            Product product = this.productDao.findByCode(productCode, session).orElseThrow(() -> new IllegalArgumentException(productCode + " không tồn tại"));
            Image image = this.imageDao.findByTypeAndObjectId(ImageType.PRODUCT, String.valueOf(product.getId()), session).orElse(new Image());

            ProductResponse productResponse = new ProductResponse();
            productResponse.setCode(product.getCode());
            productResponse.setName(product.getName());
            productResponse.setImage(image.getUrl());
            productResponse.setId(product.getId());
            productResponse.setCreatedDate(product.getCreatedDate());
            productResponse.setDescription(product.getDescription());
            productResponse.setCategoryId(product.getCategoryId());
            productResponse.setIsShowHome(product.getIsShowHome());
            productResponse.setShortDescription(product.getShortDescription());

            Map<ColorProductDTO, List<ProductDetailRequest>> details = this.productDao.getSizeAndColorProduct(product.getId(), session);
            productResponse.setDetails(details);
            return Result.<ProductResponse>builder()
                    .isSuccess(true)
                    .data(productResponse)
                    .build();
        });
    }

}