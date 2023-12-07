package vn.hcmute.springboot.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
@Builder
public class SkillResponse {
  private List<String> skills;
}
