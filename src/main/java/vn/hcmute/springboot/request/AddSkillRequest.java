package vn.hcmute.springboot.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Data
public class AddSkillRequest {
  List<String> skillName;
  List<String> level;
}
