package vn.hcmute.springboot.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
  @NotBlank
  @Email
  private String username;

  @NotBlank
  private String password;

}