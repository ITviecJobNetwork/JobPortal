package vn.hcmute.springboot.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyJobResponse {
  List<GetJobResponse> relatedJobs;
  private String message;
  private HttpStatus status;

  public ApplyJobResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }


}
