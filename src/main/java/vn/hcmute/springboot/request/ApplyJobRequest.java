package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ApplyJobRequest {
  private Integer jobId;
  @NotBlank(message = "Please enter your name")
  private String candidateName;
  private String linkCv;
  private String coverLetter;


}
