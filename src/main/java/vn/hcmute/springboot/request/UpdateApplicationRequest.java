package vn.hcmute.springboot.request;

import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.ApplicationStatus;

@Getter
@Setter
public class UpdateApplicationRequest {
  private ApplicationStatus status;
  private String reason;
}
