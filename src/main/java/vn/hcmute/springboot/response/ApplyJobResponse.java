package vn.hcmute.springboot.response;

import java.time.LocalDate;

import lombok.*;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.ApplicationStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyJobResponse {
  private Integer userId;
  private String candidateName;
  private String linkCv;
  private String jobName;
  private String companyName;
  private ApplicationStatus applicationStatus;
  private String coverLetter;
  private String jobId;
  private LocalDate submittedAt;
  private String message;
  private HttpStatus status;
  public ApplyJobResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
}
