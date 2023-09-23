package com.fashion.dao;

import com.fashion.entity.ProductDetail;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ProductDetailDao extends BaseDao<ProductDetail, Long> {

    @Override
    protected Class<ProductDetail> entityClass() {
        return ProductDetail.class;
    }

    public List<ProductDetail> findByIds(List<Long> ids, Session session) {
        Query<ProductDetail> query = session.createQuery("FROM ProductDetail pd WHERE pd.id IN :ids", ProductDetail.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
  
    public List<ProductDetail> findByColorProductId(Long colorProductId, Session session) {
        Query<ProductDetail> query = session.createQuery("FROM ProductDetail pd WHERE pd.colorProductId = :colorProductId", ProductDetail.class);
        query.setParameter("colorProductId", colorProductId);
        return query.getResultList();
    }

    public void deleteByIds(List<Long> ids, Session session) {
        Query query = session.createQuery("DELETE FROM ProductDetail pd WHERE pd.id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    public List<ProductDetail> findByProductId(long productId, Session session) {
        Query<ProductDetail> query = session.createQuery("FROM ProductDetail pd WHERE pd.productId = :productId", ProductDetail.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
}
