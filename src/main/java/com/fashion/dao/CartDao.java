package com.fashion.dao;

import com.fashion.constant.ImageType;
import com.fashion.dto.cart.CartResponse;
import com.fashion.entity.Cart;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CartDao extends BaseDao<Cart, Long> {
    @Override
    protected Class<Cart> entityClass() {
        return Cart.class;
    }

    public List<Cart> findByIdsAndEmail(List<Long> ids, String email, Session session) {
        return session.createQuery("FROM Cart c WHERE c.id IN :ids AND c.email = :email", Cart.class)
                .setParameter("email", email)
                .setParameter("ids", ids)
                .getResultList();
    }

    public Optional<Cart> findByEmailAndProductDetailId(String email, Long productDetailId, Session session) {
        Query<Cart> query = session.createQuery("FROM Cart c WHERE c.email = :email AND c.productDetailId = :productDetailId", Cart.class);
        query.setParameter("email", email);
        query.setParameter("productDetailId", productDetailId);
        return this.findReturnOptional(query);
    }

    public void deleteByEmail(String email, Session session) {
        var query = session.createQuery("DELETE FROM Cart c WHERE c.email = :email");
        query.setParameter("email", email);
        query.executeUpdate();
    }

    public List<CartResponse> getMyCart(String email, Session session) {
        return this.getMyCart(email, null, session);
    }

    public List<CartResponse> getMyCart(String email, List<Long> cartIds, Session session) {
        String sql = new StringBuilder("SELECT c, p.name, pd.price, img.url, pd.percentDiscount, pd.size, cp.name, p.code, pd.quantity FROM Cart c")
                .append("  JOIN ProductDetail pd ON pd.id = c.productDetailId")
                .append("  JOIN Product p ON p.id = pd.productId")
                .append("  JOIN ColorProduct cp ON cp.id = pd.colorProductId")
                .append("  LEFT JOIN Image img ON img.objectId = cp.id AND img.type = :imageType")
                .append(" WHERE c.email = :email")
                .append(CollectionUtils.isNotEmpty(cartIds) ? " AND c.id IN :cartIds" : "")
                .toString();
        Query<Tuple> query = session.createQuery(sql, Tuple.class);
        query.setParameter("imageType", ImageType.PRODUCT_COLOR);
        if (CollectionUtils.isNotEmpty(cartIds)) {
            query.setParameter("cartIds", cartIds);
        }
        query.setParameter("email", email);
        return query.getResultStream()
                .map(tuple -> {
                    Cart cart = tuple.get(0, Cart.class);
                    return CartResponse.builder()
                            .cartId(cart.getId())
                            .quantity(cart.getQuantity())
                            .productName(tuple.get(1, String.class))
                            .price(tuple.get(2, BigDecimal.class))
                            .productDetailImage(tuple.get(3, String.class))
                            .discount(tuple.get(4, Integer.class))
                            .size(tuple.get(5, String.class))
                            .color(tuple.get(6, String.class))
                            .productCode(tuple.get(7, String.class))
                            .maxQuantity(tuple.get(8, Integer.class))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
