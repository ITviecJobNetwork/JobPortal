package com.fashion.service;

import com.fashion.annotation.Inject;
import com.fashion.annotation.Order;
import com.fashion.constant.ImageType;
import com.fashion.dao.ImageDao;
import com.fashion.dto.base.FileItem;
import com.fashion.entity.Image;
import com.fashion.service.upload.FirebaseUploader;
import com.fashion.service.upload.IUploader;
import lombok.Setter;
import org.hibernate.Session;

@Setter
@Order(Integer.MAX_VALUE - 1)
public class ImageService extends BaseService {
    private ImageDao imageDao;

    @Setter(onParam_ = @Inject(usedBean = FirebaseUploader.class))
    private IUploader uploader;

    public Image insertNotCommit(FileItem fileItem, ImageType imageType, String objectId, Session session) {
        String url = this.uploader.uploadFile(fileItem, imageType);
        Image image = new Image();
        image.setObjectId(objectId);
        image.setUrl(url);
        image.setType(imageType);
        return this.saveNotCommit(image, session);
    }

    public Image insertOrUpdateNotCommit(FileItem fileItem, ImageType imageType, String objectId, Session session) {
        return this.imageDao.findByTypeAndObjectId(imageType, objectId, session)
                .map(image -> {
                    String url = this.uploader.uploadFile(fileItem, imageType);
                    image.setUrl(url);
                    return this.saveNotCommit(image, session);
                })
                .orElseGet(() -> this.insertNotCommit(fileItem, imageType, objectId, session));
    }

    private Image saveNotCommit(Image image, Session session) {
        Long id = (Long) this.imageDao.save(image, session);
        image.setId(id);
        return image;
    }
}
