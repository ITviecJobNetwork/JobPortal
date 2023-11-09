package com.fashion.dao;

import com.fashion.entity.ColorProduct;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ColorProductDao extends BaseDao<ColorProduct, Long> {
    @Override
    protected Class<ColorProduct> entityClass() {
        return ColorProduct.class;
    }

    public void deleteColorByProductIdAndNotIn(Long productId, List<String> colors, Session session) {
        var query = session.createQuery("DELETE FROM ColorProduct cp WHERE cp.productId = :productId AND cp.name NOT IN (:colors)");
        query.setParameter("productId", productId);
        query.setParameter("colors", colors);
        query.executeUpdate();
    }

    public List<ColorProduct> findByProductIdAndColors(Long productId, List<String> colors, Session session) {
        Query<ColorProduct> query = session.createQuery("FROM ColorProduct cp WHERE cp.productId = :productId AND cp.name IN :colors", ColorProduct.class);
        query.setParameter("productId", productId);
        query.setParameter("colors", colors);
        return query.getResultList();
    }
}
