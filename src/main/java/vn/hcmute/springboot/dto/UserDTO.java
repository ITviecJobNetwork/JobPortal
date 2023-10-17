package vn.hcmute.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.valid.ValidEmail;

@Getter
@Setter
public class UserDTO {

  @NotBlank(message = "Username is mandatory")
  private String username;

  @NotEmpty
  private String password;

  @NotBlank(message = "Email is mandatory")
  @ValidEmail
  private String email;

  // standard getters and setters
}