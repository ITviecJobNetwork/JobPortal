package vn.hcmute.springboot.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.Skill;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
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
