package vn.hcmute.springboot.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class CompanyWithJobsResponse {
  private List<JobOpeningResponse> jobOpenings;
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
  private Integer companySize;
  private String country;
  private LocalDate foundedDate;
}
