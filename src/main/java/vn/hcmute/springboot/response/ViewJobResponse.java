package vn.hcmute.springboot.response;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewJobResponse {
  Page<GetJobResponse> jobs;
  private String message;
  private HttpStatus status;

  public ViewJobResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }

  public ViewJobResponse(Page<GetJobResponse> jobs) {
    this.jobs = jobs;
  }
}
