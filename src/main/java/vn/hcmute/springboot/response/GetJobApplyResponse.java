package vn.hcmute.springboot.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.Job;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetJobApplyResponse {
  private String message;
  private HttpStatus status;
  Page<Job> job;
  public GetJobApplyResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
  public GetJobApplyResponse(Page<Job> job) {
    this.job = job;
  }

}
