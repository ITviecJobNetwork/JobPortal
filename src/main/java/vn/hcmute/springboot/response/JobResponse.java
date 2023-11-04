package vn.hcmute.springboot.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.Job;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class JobResponse {
  private Page<Job> job;
  private String message;
  private HttpStatus status;
  public JobResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
  public  JobResponse(Page<Job> job) {
    this.job = job;
  }

}
