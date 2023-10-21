package vn.hcmute.springboot.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hcmute.springboot.model.Gender;
import vn.hcmute.springboot.model.Role;
import vn.hcmute.springboot.valid.ValidEmail;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  @NotBlank(message = "cannot-be-blank")
  private String nickname;

  @NotBlank(message = "cannot-be-blank")
  @Size(min = 6, message = "length-must-be-greater-than-or-equal-6")
  @Email
  private String username;

  @NotBlank(message = "cannot-be-blank")
  @Size(min = 6,max = 20, message = "length-must-be-greater-than-or-equal-6-or-less-than-or-equal-20")
  private String password;

  private String phoneNumber;
  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  @NotBlank
  private LocalDate birthDate;

  @NotBlank
  private Gender gender;
  @NotBlank
  private Role role;

}