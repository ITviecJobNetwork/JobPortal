package vn.hcmute.springboot.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ApplyJobRequest {
  private Integer jobId;
  private String candidateName;
  private MultipartFile linkCv;
  private String coverLetter;


}
