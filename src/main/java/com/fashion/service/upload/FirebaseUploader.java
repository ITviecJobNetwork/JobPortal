package com.fashion.service.upload;

import com.fashion.annotation.Order;
import com.fashion.constant.ImageType;
import com.fashion.dto.base.FileItem;
import com.fashion.service.BaseService;
import com.google.cloud.storage.Bucket;
import lombok.Setter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

@Setter
@Order(Integer.MAX_VALUE)
public class FirebaseUploader extends BaseService implements IUploader {

    private Bucket bucket;

    @Override
    public String upload(FileItem fileItem, ImageType imageType) {
        String fileName = imageType.getType() + fileItem.getName();
        this.bucket.create(fileName, fileItem.getBytes(), fileItem.getContentType());
        return fileName;
    }

    @Override
    public String handleAfterUpload(String url) {
        String encode = URLEncoder.encode(url, StandardCharsets.UTF_8);
        return MessageFormat.format("https://firebasestorage.googleapis.com/v0/b/{0}/o/{1}?alt=media", this.bucket.getName(), encode);
    }
}
