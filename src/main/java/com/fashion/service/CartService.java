package com.fashion.service;

import com.fashion.dao.CartDao;
import com.fashion.dao.ColorProductDao;
import com.fashion.dao.ProductDao;
import com.fashion.dao.ProductDetailDao;
import com.fashion.dto.base.Result;
import com.fashion.dto.cart.AddToCartRequest;
import com.fashion.dto.cart.CartResponse;
import com.fashion.dto.cart.UpdateCart;
import com.fashion.dto.user.UserResponse;
import com.fashion.entity.Cart;
import com.fashion.entity.ColorProduct;
import com.fashion.entity.Product;
import com.fashion.entity.ProductDetail;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Setter
public class CartService extends BaseService {

    private CartDao cartDao;
    private ProductDetailDao productDetailDao;
    private ProductDao productDao;
    private ColorProductDao colorProductDao;

    public List<CartResponse> getMyCart(UserResponse userResponse) {
        return this.doSelect(session -> {
            return this.cartDao.getMyCart(userResponse.getEmail(), session);
        });
    }

    public Result<Long> deleteById(Long cartId) {
        return this.tryCatchWithTransaction(session -> {
            this.cartDao.deleteById(cartId, session);
            return Result.<Long>builder()
                    .isSuccess(true)
                    .message("Xóa khỏi giỏ hàng thành công")
                    .build();
        }, cartId);
    }

    public Result<Object> deleteAll(UserResponse currentUser) {
        return this.tryCatchWithTransaction(session -> {
            this.cartDao.deleteByEmail(currentUser.getEmail(), session);
            return Result.builder()
                    .isSuccess(true)
                    .message("Xóa khỏi giỏ hàng thành công")
                    .build();
        }, null);
    }

    public Result<AddToCartRequest> addToCart(AddToCartRequest request, UserResponse userResponse) {
        return this.tryCatchWithTransaction(session -> {

            ProductDetail productDetail = this.productDetailDao.findById(request.getProductDetailId(), session)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

            Cart cart = this.cartDao.findByEmailAndProductDetailId(userResponse.getEmail(), request.getProductDetailId(), session)
                    .map(c -> {
                        c.setQuantity(c.getQuantity() + request.getQuantity());
                        return c;
                    })
                    .orElseGet(() -> {
                        Cart c = new Cart();
                        c.setQuantity(request.getQuantity());
                        c.setProductDetailId(productDetail.getId());
                        c.setEmail(userResponse.getEmail());
                        return c;
                    });
            if (productDetail.getQuantity() < cart.getQuantity()) {
                throw new IllegalArgumentException("Số lượng sản phẩm không đủ");
            }
            this.cartDao.save(cart, session);

            return Result.<AddToCartRequest>builder()
                    .isSuccess(true)
                    .message("Thêm giỏ hàng thành công")
                    .build();
        }, request);
    }

    public Result<Void> updateCart(List<UpdateCart> updateCarts, UserResponse userResponse) {
        return this.tryCatchWithTransaction(session -> {
            List<Long> ids = updateCarts.stream()
                    .map(UpdateCart::getId)
                    .collect(Collectors.toList());
            Map<Long, Cart> cartMap = this.cartDao.findByIdsAndEmail(ids, userResponse.getEmail(), session)
                    .stream()
                    .collect(Collectors.toMap(Cart::getId, c -> c));
            List<Long> productDetailIds = cartMap.values().stream()
                    .map(Cart::getProductDetailId)
                    .collect(Collectors.toList());
            Map<Long, ProductDetail> productDetailMap = this.productDetailDao.findByIds(productDetailIds, session)
                    .stream()
                    .collect(Collectors.toMap(ProductDetail::getId, pd -> pd));

            this.validateBeforeUpdateCart(updateCarts, cartMap, productDetailMap)
                    .accept(session);

            updateCarts.forEach(c -> {
                Cart cart = cartMap.get(c.getId());
                if (Objects.nonNull(cart)) {
                    cart.setQuantity(c.getQuantity());
                    this.cartDao.save(cart, session);
                }
            });
            return Result.<Void>builder()
                    .isSuccess(true)
                    .message("Cập nhật giỏ hàng thành công")
                    .build();
        });
    }

    private Consumer<Session> validateBeforeUpdateCart(List<UpdateCart> updateCarts, Map<Long, Cart> cartMap, Map<Long, ProductDetail> productDetailMap) {
        return session -> {
            String messageError = updateCarts.stream()
                    .filter(c -> cartMap.get(c.getId()) != null)
                    .map(c -> {
                        Cart cart = cartMap.get(c.getId());
                        ProductDetail productDetail = productDetailMap.get(cart.getProductDetailId());
                        return c.getQuantity() > productDetail.getQuantity() ? productDetail : null;
                    })
                    .filter(Objects::nonNull)
                    .map(pd -> {
                        Product product = this.productDao.findById(pd.getProductId(), session)
                                .orElseThrow();
                        ColorProduct colorProduct = this.colorProductDao.findById(pd.getColorProductId(), session)
                                .orElseThrow();
                        return String.format("%s-%s-%s không đủ số lượng", product.getName(), colorProduct.getName(), pd.getSize());
                    })
                    .collect(Collectors.joining("</br>"));
            if (StringUtils.isNotBlank(messageError)) {
                throw new IllegalArgumentException(messageError);
            }
        };
    }
}
