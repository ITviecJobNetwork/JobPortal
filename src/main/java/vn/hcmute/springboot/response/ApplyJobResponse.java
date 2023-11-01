package vn.hcmute.springboot.response;

import java.time.LocalDate;

import java.util.List;
import lombok.*;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.ApplicationStatus;
import vn.hcmute.springboot.model.Job;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyJobResponse {
  private String message;
  private HttpStatus status;
  List<Job> relatedJobs;
  public ApplyJobResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
  public ApplyJobResponse(String message,List<Job> relatedJobs) {
    this.message = message;
    this.relatedJobs = relatedJobs;
  }
}
