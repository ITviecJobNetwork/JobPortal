package com.fashion.service.upload;

import com.fashion.constant.ImageType;
import com.fashion.dto.base.FileItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public interface IUploader {

    default boolean validateBeforeUpload(FileItem fileItem) {
        return Optional.ofNullable(fileItem)
                .filter(x -> x.getBytes().length > 0)
                .isPresent();
    }

    default String uploadFile(FileItem item, ImageType imageType) {
        if (!this.validateBeforeUpload(item)) return StringUtils.EMPTY;
        String upload = this.upload(item, imageType);
        return handleAfterUpload(upload);
    }

    String upload(FileItem fileItem, ImageType imageType);

    String handleAfterUpload(String url);
}
