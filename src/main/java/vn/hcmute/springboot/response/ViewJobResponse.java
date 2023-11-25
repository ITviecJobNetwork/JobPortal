package vn.hcmute.springboot.response;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor

public class ViewJobResponse {
  Page<GetJobResponse> jobs;



  public ViewJobResponse(Page<GetJobResponse> jobs) {
    this.jobs = jobs;
  }
}
