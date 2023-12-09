package vn.hcmute.springboot.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class CompanyKeySkillResponse {
  private Integer id;
  private String title;
  private Integer companyId;
}
