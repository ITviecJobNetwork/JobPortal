package vn.hcmute.springboot.response;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.ViewJobs;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewJobResponse {
  Page<ViewJobs> jobs;
  private String message;
  private HttpStatus status;
  public ViewJobResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }

  public ViewJobResponse(Page<ViewJobs> jobs) {
    this.jobs = jobs;
  }
}
