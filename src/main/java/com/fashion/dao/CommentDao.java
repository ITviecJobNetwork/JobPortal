package com.fashion.dao;

import com.fashion.entity.Comment;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class CommentDao extends BaseDao<Comment, Long> {

    @Override
    protected Class<Comment> entityClass() {
        return Comment.class;
    }

    public List<Comment> findByProductId(Long productId, Session session) {
        Query<Comment> query = session.createQuery("FROM Comment c WHERE c.productId = :productId AND c.hidden = false ORDER BY c.createdDate DESC", Comment.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
}
