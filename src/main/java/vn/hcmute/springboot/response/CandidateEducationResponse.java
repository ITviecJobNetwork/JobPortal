package vn.hcmute.springboot.response;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;


@Getter
@Setter
@Data
@Builder
public class CandidateEducationResponse {
  private Integer id;
  private String major;
  private String school;
  private String startTime;
  private String endTime;
}
