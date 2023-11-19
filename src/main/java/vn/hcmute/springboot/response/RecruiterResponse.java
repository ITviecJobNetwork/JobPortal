package vn.hcmute.springboot.response;

import lombok.*;
import vn.hcmute.springboot.model.RecruiterStatus;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class RecruiterResponse {
  private Integer recruiterId;
  private String companyName;
  private String companyAddress;
  private Integer companySize;
  private String phoneNumber;
  private String overTimePolicy;
  private String recruitmentProcedure;
  private String benefit;
  private String introduction;
  private String fbUrl;
  private String websiteUrl;
  private String linkedInUrl;
  private RecruiterStatus status;
  private String username;
  private LocalDate birthDate;
  private String nickname;
}
