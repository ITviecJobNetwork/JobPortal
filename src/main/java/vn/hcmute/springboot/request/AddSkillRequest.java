package vn.hcmute.springboot.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@Data
public class AddSkillRequest {
  Set<String> skillName;
}
