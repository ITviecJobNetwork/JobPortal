package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeNickNameRequest {
  @NotBlank(message = "Please enter your new nickname")
  private String newNickName;
}
