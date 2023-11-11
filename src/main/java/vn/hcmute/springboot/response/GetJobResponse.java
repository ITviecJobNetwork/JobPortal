package vn.hcmute.springboot.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetJobResponse {
  private Integer jobId;
  private String title;
  private String companyName;
  private String address;
  private List<String> skills;
  private String description;
  private LocalDate createdDate;
  private LocalDate expiredDate;
  private String requirements;
  private String jobType;
  private String location;
  private Boolean isSaved;
  private Boolean isApplied;

}
