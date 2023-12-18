package vn.hcmute.springboot.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetJobResponse {
  private Integer jobId;
  private String title;
  private Integer companyId;
  private String companyName;
  private String companyLogo;
  private String companyType;
  private Integer minCompanySize;
  private Integer maxCompanySize;
  private String country;
  private String workingFrom;
  private String workingTo;
  private String overtimePolicy;
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
  private Double minSalary;
  private Double maxSalary;
  private String level;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate appliedAt;
  private List<CompanyKeySkillResponse> companyKeySkill;

}
