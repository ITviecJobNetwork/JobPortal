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
  @NotBlank(message = "Please upload your CV")
  private String linkCv;
  private String coverLetter;


}
