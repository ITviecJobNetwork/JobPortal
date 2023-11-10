package vn.hcmute.springboot.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.Skill;

@Getter
@Setter
public class JobOpeningResponse {
  private Integer jobId;
  private String title;
  private String companyName;
  private String address;
  private String companyType;
  private Set<Skill> skills;
  private String description;
  private String companyLogo;
  private LocalDate createdDate;


}
