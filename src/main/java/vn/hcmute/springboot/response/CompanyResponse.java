package vn.hcmute.springboot.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
  private Integer companyId;
  private String companyName;
  private String companyLogo;
  private String companyType;
  private String address;
  private String description;
  private String website;
  private String phoneNumber;
  private String industry;
  private LocalDate createdDate;
  private Integer countJobOpenings;
  private Integer maxCompanySize;
  private Integer minCompanySize;
  private String overtimePolicy;
  private String country;
  private LocalDate foundedDate;
  private String workingFrom;
  private String workingTo;
  private List<CompanyKeySkillResponse> companyKeySkill;
}
