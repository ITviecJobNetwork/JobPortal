package vn.hcmute.springboot.response;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@Data
@Builder
public class CandidateExperienceResponse {
  private Integer id;
  private String companyName;
  private String jobTitle;
  private String description;
  private LocalDate startTime;
  private LocalDate endTime;
}
