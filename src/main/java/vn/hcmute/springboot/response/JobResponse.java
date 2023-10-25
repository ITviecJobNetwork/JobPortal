package vn.hcmute.springboot.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.Job;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class JobResponse {
  private List<Job> job;
  private String message;
  private HttpStatus status;

}
