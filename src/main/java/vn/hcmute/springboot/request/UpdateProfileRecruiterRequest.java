package vn.hcmute.springboot.request;


import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.valid.ValidEmail;

@Getter
@Setter
public class UpdateProfileRecruiterRequest {
  @NotBlank(message = "Nick Name is required")
  private String nickname;

  @NotBlank(message = "Phone Number is required")
  private String phoneNumber;
  private LocalDate birthDate;

  @NotBlank(message = "Email is required")
  @ValidEmail
  private String username;
  private String fbUrl;

  private String websiteUrl;
  private String benefit;
  private String overTimePolicy;
  private LocalDate workingDay;
  private String recruitmentProcedure;
  private String introduction;

}
