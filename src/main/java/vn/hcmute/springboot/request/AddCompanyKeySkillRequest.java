package vn.hcmute.springboot.request;

import lombok.*;

import java.util.List;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddCompanyKeySkillRequest {
  private List<String> companyKeySkill;
}
