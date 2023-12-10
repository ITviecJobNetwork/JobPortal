package vn.hcmute.springboot.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.ApplicationStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ApplicationFormResponse {
  private Integer id;
  private String linkCV;
  private Integer jobId;
  private String jobTitle;
  private String candidateName;
  private LocalDate submittedAt;
  private String coverLetter;
  private ApplicationStatus status;
  private Integer candidateId;
}
