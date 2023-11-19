package vn.hcmute.springboot.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class RecruiterProfileResponse {
  private Integer recruiterId;
  private String fbUrl;
  private String websiteUrl;
  private String linkedInUrl;
  private String username;
  private LocalDate birthDate;
  private String nickname;
  private String phoneNumber;
}
