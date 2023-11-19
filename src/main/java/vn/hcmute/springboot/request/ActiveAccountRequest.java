package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ActiveAccountRequest {
  @NotBlank(message = "User name is mandatory")
  private String userName;
  @NotBlank(message = "Admin email is mandatory")
  private String adminEmail;
}
