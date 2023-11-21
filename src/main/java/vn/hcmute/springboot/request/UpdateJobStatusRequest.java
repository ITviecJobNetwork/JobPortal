package vn.hcmute.springboot.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.JobStatus;

@Data
@Getter
@Setter
public class UpdateJobStatusRequest {
  private JobStatus status;
}
