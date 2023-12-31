package vn.hcmute.springboot.response;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Getter
@Setter
@Data
@Builder
public class CandidateExperienceResponse {
  private Integer id;
  private String companyName;
  private String jobTitle;
  private String startTime;
  private String endTime;
}
