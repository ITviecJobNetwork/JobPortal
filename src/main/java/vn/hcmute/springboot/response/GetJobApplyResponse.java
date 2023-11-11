package vn.hcmute.springboot.response;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetJobApplyResponse {
  Page<GetJobResponse> job;
  private String message;
  private HttpStatus status;

  public GetJobApplyResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }

  public GetJobApplyResponse(Page<GetJobResponse> job) {
    this.job = job;
  }

}
