package vn.hcmute.springboot.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.SaveJobs;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveJobResponse {

  List<Job> jobs;
  private String message;
  private HttpStatus status;

  public SaveJobResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
  public SaveJobResponse(List<Job> jobs) {
    this.jobs = jobs;
  }
}
