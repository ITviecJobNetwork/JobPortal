package vn.hcmute.springboot.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.UserStatus;

@Getter
@Setter
@Data
public class DeActiveRequest {
  private String reason;
  private UserStatus status = UserStatus.DEACTIVATED;
}
