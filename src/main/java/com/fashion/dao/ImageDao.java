package com.fashion.dao;

import com.fashion.constant.ImageType;
import com.fashion.entity.Image;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

public class ImageDao extends BaseDao<Image, Long> {

    @Override
    protected Class<Image> entityClass() {
        return Image.class;
    }

    public Optional<Image> findByTypeAndObjectId(ImageType type, String objectId, Session session) {
        Query<Image> query = session.createQuery("FROM Image img WHERE img.type = :type AND img.objectId = :objectId", Image.class);
        query.setParameter("type", type);
        query.setParameter("objectId", objectId);
        return this.findReturnOptional(query);
    }
}
