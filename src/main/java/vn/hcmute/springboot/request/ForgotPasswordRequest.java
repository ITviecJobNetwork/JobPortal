package vn.hcmute.springboot.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
  @NotEmpty(message = "cannot-be-empty")
  @Size(max = 50, message = "length-must-be-less-than-50")
  @Email(message = "invalid-email-format")
  private String email;

}
