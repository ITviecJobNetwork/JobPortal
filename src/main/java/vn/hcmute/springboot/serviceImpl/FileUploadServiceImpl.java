package vn.hcmute.springboot.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.service.FileUploadService;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
  private final Cloudinary cloudinary;
  @Override
  public String uploadFile(MultipartFile multipartFile) throws IOException {
    return cloudinary.uploader()
        .upload(multipartFile.getBytes(),
            Map.of("public_id", UUID.randomUUID().toString()))
        .get("url")
        .toString();
  }

  @Override
  public String uploadCv(MultipartFile multipartFile) throws IOException {
    return cloudinary.uploader()
        .upload(multipartFile.getBytes(), ObjectUtils.emptyMap())
        .get("url")
        .toString();
  }
  @Override
  public void deleteFile(String publicId) throws IOException {

    try {
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (Exception e) {
      throw new IOException("Không thể xóa tệp " + publicId, e);
    }
  }


}

