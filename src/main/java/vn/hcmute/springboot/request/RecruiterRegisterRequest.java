package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.Location;
import vn.hcmute.springboot.valid.ValidEmail;

@Getter
@Setter
public class RecruiterRegisterRequest {
  @NotBlank(message = "FullName is required")
  private String fullName;

  @NotBlank(message = "Email is required")
  @ValidEmail
  private String username;

  @NotBlank(message = "Work Title is required")
  private String workTitle;

  @NotBlank(message = "PhoneNumber is required")
  private String phoneNumber;

  @NotBlank(message = "Company is required")
  private String companyName;

  @NotBlank(message = "Company Location is required")
  private String companyLocation;
  private String websiteUrl;
  private String hearAboutUs;


}
