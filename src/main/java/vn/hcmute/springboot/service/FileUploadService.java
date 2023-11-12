package vn.hcmute.springboot.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


public interface FileUploadService {
  String uploadFile(MultipartFile multipartFile) throws IOException;

  String uploadCv(MultipartFile multipartFile) throws IOException;

  void deleteFile(String publicId) throws IOException;



}
