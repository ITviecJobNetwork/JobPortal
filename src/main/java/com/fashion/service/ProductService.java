package com.fashion.service;

import com.fashion.constant.ImageType;
import com.fashion.dao.*;
import com.fashion.dto.base.Result;
import com.fashion.dto.comment.CommentDTO;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.dto.product.*;
import com.fashion.entity.*;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
public class ProductService extends BaseService {

    private ProductDao productDao;
    private ColorProductDao colorProductDao;
    private ProductDetailDao productDetailDao;
    private CategoryDao categoryDao;
    private ImageDao imageDao;

    private CommentService commentService;
    private ImageService imageService;

    public Result<ProductDetailResponse> getDetailProduct(String code, int limitRelatedProduct) {
        return this.tryCatchWithDoSelect(session -> {
            Product product = this.productDao.findByCode(code, session)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy: " + code));
            if (!product.getActive()) {
                throw new IllegalArgumentException("Không tìm thấy: " + code);
            }
            Category category = this.categoryDao.findById(product.getCategoryId(), session).orElseThrow();
            if (!category.isActive()) {
                throw new IllegalArgumentException("Không tìm thấy: " + code);
            }
            String imageUrl = this.imageDao.findByTypeAndObjectId(ImageType.PRODUCT, String.valueOf(product.getId()), session)
                    .map(Image::getUrl)
                    .orElse(StringUtils.EMPTY);

            Map<ColorProductDTO, List<ProductDetailRequest>> sizeAndColorProduct = this.productDao.getSizeAndColorProduct(product.getId(), session);
            List<ProductResponse> byCategoryId = this.productDao.getByCategoryId(limitRelatedProduct, product.getCategoryId(), session);
            Set<String> sizes = sizeAndColorProduct.keySet()
                    .stream()
                    .map(sizeAndColorProduct::get)
                    .flatMap(Collection::stream)
                    .map(ProductDetailRequest::getSize)
                    .collect(Collectors.toSet());

            List<CommentDTO> comments = this.commentService.findByProductId(product.getId(), session);

            ProductDetailResponse build = ProductDetailResponse.builder()
                    .id(product.getId())
                    .code(product.getCode())
                    .name(product.getName())
                    .image(imageUrl)
                    .shortDescription(product.getShortDescription())
                    .categoryName(category.getName())
                    .categoryId(category.getId())
                    .description(product.getDescription())
                    .relatedProducts(byCategoryId)
                    .details(sizeAndColorProduct)
                    .sizesSet(sizes)
                    .comments(comments)
                    .build();

            return Result.<ProductDetailResponse>builder()
                    .isSuccess(true)
                    .data(build)
                    .build();
        });
    }

    public List<ProductImageDTO> getProductImage(int limit) {
        return this.doSelect(session -> this.productDao.getProductImage(limit, session));
    }

    public List<ProductResponse> getBestSeller(int limit) {
        return this.doSelect(session -> this.productDao.getBestSeller(limit, session));
    }

    public List<ProductResponse> getNewest(int limit) {
        return this.doSelect(session -> this.productDao.getNewest(limit, session));
    }

    public List<ProductResponse> getHotSales(int limit) {
        return this.doSelect(session -> this.productDao.getHotSales(limit, session));
    }

    public PageResponse<ProductResponse> paginateProductList(PageRequest<ProductSearchRequest> request) {
        return this.doSelect(session -> this.productDao.searchProduct(request, session));
    }

    public Result<String> lockOrUnlockProduct(String code) {
        return this.tryCatchWithTransaction(session -> {
            Product product = this.productDao.findByCode(code, session)
                    .orElseThrow(() -> new IllegalArgumentException(code + " không tìm thấy"));
            product.setActive( !product.getActive() );
            this.productDetailDao.findByProductId(product.getId(), session)
                    .forEach(pd -> {
                        pd.setActive( product.getActive() );
                        this.productDetailDao.save(pd, session);
                    });
            this.productDao.save(product, session);
            return Result.<String>builder()
                    .message((product.getActive() ? "Mở khóa " : "Khóa") + " sản phẩm thành công")
                    .data(code)
                    .build();
        }, code);
    }


    public Result<CreateProductRequest> createProduct(CreateProductRequest request, HttpSession httpSession) {
        // toto: get current user in http session

        return this.tryCatchWithTransaction(session -> {
            if (this.productDao.findByCode(request.getCode(), session).isPresent()) {
                throw new IllegalArgumentException(request.getCode() + " đã tồn tại");
            }
            Product product = new Product();
            product.setCode(request.getCode());
            product.setName(request.getName());
            product.setCategoryId(request.getCategoryId());
            product.setDescription(request.getDescription());
            product.setCreatedBy("suong.nv");
            product.setActive(true);
            product.setIsShowHome(request.getIsShowHome());
            product.setShortDescription(request.getShortDescription());
            Long productId = (Long) this.productDao.save(product, session);
            product.setId(productId);
            if (Objects.nonNull(request.getImage())) {
                request.getImage().setName(product.getCode() + "-" + request.getImage().getName());
            }
            this.imageService.insertNotCommit(request.getImage(), ImageType.PRODUCT, productId.toString(), session);

            Map<String, ColorProduct> colorProductMap = new HashMap<>();
            for (ColorProductDTO colorProductDTO : request.getColorProductDTOList()) {
                ColorProduct colorProduct = this.insertColorProduct(product.getId(), colorProductDTO.getColor())
                        .apply(session);
                if (Objects.nonNull(colorProductDTO.getImage())) {
                    colorProductDTO.getImage().setName(product.getCode() + "-" + colorProductDTO.getImage().getName());
                }
                this.imageService.insertNotCommit(colorProductDTO.getImage(), ImageType.PRODUCT_COLOR, colorProduct.getId().toString(), session);
                colorProductMap.put(colorProductDTO.getColor(), colorProduct);
            }

            this.insertProductDetail(request.getProductDetails(), colorProductMap, product.getId(), new HashMap<>())
                    .accept(session);

            return Result.<CreateProductRequest>builder()
                    .data(request)
                    .message("Tạo mới sản phẩm thành công")
                    .isSuccess(true)
                    .build();
        }, request);
    }

    public Result<CreateProductRequest> updateProduct(CreateProductRequest request, HttpSession httpSession) {
        // todo: get current user in http session

        return this.tryCatchWithTransaction(session -> {
            Optional<Product> productOpt = this.productDao.findByCode(request.getCode(), session);
            if (productOpt.isPresent() && productOpt.get().getId() != request.getId()) {
                throw new IllegalArgumentException(request.getCode() + " đã tồn tại");
            }

            // update product info
            Product productInDb = productOpt.get();
            productInDb.setName(request.getName());
            productInDb.setCategoryId(request.getCategoryId());
            productInDb.setDescription(request.getDescription());
            productInDb.setIsShowHome(request.getIsShowHome());
            productInDb.setShortDescription(request.getShortDescription());
            this.productDao.save(productInDb, session);

            // handle product's avatar
            if (Objects.nonNull(request.getImage()) && request.getImage().getBytes().length > 0) {
                request.getImage().setName(productInDb.getCode() + "-" + request.getImage().getName());
                this.imageService.insertOrUpdateNotCommit(request.getImage(), ImageType.PRODUCT, String.valueOf(productInDb.getId()), session);
            }

            Map<String, ColorProduct> colorProductMap = new HashMap<>();

            // handle product's colors
            List<String> colors = request.getColorProductDTOList().stream()
                    .map(ColorProductDTO::getColor)
                    .collect(Collectors.toList());
            // remove product's color
            this.colorProductDao.deleteColorByProductIdAndNotIn(productInDb.getId(), colors, session);
            Map<String, ColorProduct> colorProductDbMap = this.colorProductDao.findByProductIdAndColors(productInDb.getId(), colors, session)
                    .stream()
                    .collect(Collectors.toMap(ColorProduct::getName, t -> t, (v1, v2) -> v2));
            Map<Long, String> colorProductNameMap = new HashMap<>();
            for (ColorProductDTO colorProductDTO : request.getColorProductDTOList()) {
                ColorProduct colorProduct = colorProductDbMap.get(colorProductDTO.getColor());

                // if product's color does not exist in DB then create new
                if (Objects.isNull(colorProduct)) {
                    colorProduct = this.insertColorProduct(productInDb.getId(), colorProductDTO.getColor())
                            .apply(session);
                }

                // if image is passed then upload to firebase and insert a record to db
                if (Objects.nonNull(colorProductDTO.getImage()) && colorProductDTO.getImage().getBytes().length > 0) {
                    colorProductDTO.getImage().setName(productInDb.getCode() + "-" + colorProductDTO.getImage().getName());
                    this.imageService.insertOrUpdateNotCommit(colorProductDTO.getImage(), ImageType.PRODUCT_COLOR, colorProduct.getId().toString(), session);
                }
                colorProductMap.put(colorProductDTO.getColor(), colorProduct);
                colorProductNameMap.put(colorProduct.getId(), colorProduct.getName());
            }

            // handle product's sizes
            // remove all product's size with product-id
            Map<String, ProductDetail> detailsMap = this.productDetailDao.findByProductId(productInDb.getId(), session)
                    .stream()
                    .collect(Collectors.toMap(p -> colorProductNameMap.get(p.getColorProductId()) + "-" + p.getSize(), p -> p));

            Map<String, ProductDetailRequest> detailMapInRequest = request.getProductDetails().values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(x -> x.getColor().getColor() + "-" + x.getSize(), x -> x));

            List<Long> removeIds = detailsMap.keySet()
                    .stream()
                    .filter(key -> Objects.isNull(detailMapInRequest.get(key)))
                    .map(detailsMap::get)
                    .map(ProductDetail::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(removeIds)) {
                this.productDetailDao.deleteByIds(removeIds, session);
            }

            // re-create new product's size
            this.insertProductDetail(request.getProductDetails(), colorProductMap, productInDb.getId(), detailsMap)
                    .accept(session);

            return Result.<CreateProductRequest>builder()
                    .data(request)
                    .message("Cập nhật sản phẩm thành công")
                    .isSuccess(true)
                    .build();
        }, request);
    }

    private Function<Session, ColorProduct> insertColorProduct(long productId, String color) {
        return session -> {
            ColorProduct colorProduct = new ColorProduct();
            colorProduct.setProductId(productId);
            colorProduct.setName(color);
            Long id = (Long) this.colorProductDao.save(colorProduct, session);
            colorProduct.setId(id);
            return colorProduct;
        };
    }

    private Consumer<Session> insertProductDetail(Map<String, List<ProductDetailRequest>> request, Map<String, ColorProduct> colorProductMap, long productId, Map<String, ProductDetail> detailsMapInDB) {
        return session -> {
            request.forEach((color, details) -> {
                ColorProduct colorProduct = colorProductMap.get(color);

                details.forEach(pd -> {
                    ProductDetail pdInDb = detailsMapInDB.get(color + "-" + pd.getSize());
                    ProductDetail productDetail = Optional.ofNullable(pdInDb).orElseGet(ProductDetail::new);
                    productDetail.setColorProductId(colorProduct.getId());
                    productDetail.setProductId(productId);
                    productDetail.setPrice(pd.getPrice());
                    productDetail.setCost(pd.getCost());
                    productDetail.setPercentDiscount(pd.getDiscount());
                    productDetail.setSize(pd.getSize());
                    productDetail.setQuantity(pd.getQuantity());
                    productDetail.setActive(true);
                    productDetailDao.save(productDetail, session);
                });
            });
        };
    }
    public Result<String> deleteProduct(String code) {
        return this.tryCatchWithTransaction(session -> {
            Product product = this.productDao.findByCode(code, session)
                .orElseThrow(() -> new IllegalArgumentException(code + " không tìm thấy"));
            this.productDetailDao.findByProductId(product.getId(), session)
                .forEach(pd -> {
                    this.productDetailDao.delete(pd, session);
                });
            this.productDao.save(product, session);
            return Result.<String>builder()
                .message( "Xóa sản phẩm thành công")
                .data(code)
                .build();
        }, code);
    }
}
