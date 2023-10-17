package vn.hcmute.springboot.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hcmute.springboot.model.Role;
import vn.hcmute.springboot.valid.ValidEmail;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String username;
  @ValidEmail
  private String email;
  private String password;
}