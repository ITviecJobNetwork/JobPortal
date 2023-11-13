package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
  @NotBlank(message = "Please enter your current password")
  private String currentPassword;
  @NotBlank(message = "Please enter your new password")
  private String newPassword;
  @NotBlank(message = "Please confirm your new password")
  private String confirmPassword;
}
