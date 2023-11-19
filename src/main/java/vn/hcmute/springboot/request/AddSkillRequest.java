package vn.hcmute.springboot.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.LevelSkill;

import java.util.List;


@Getter
@Setter
@Data
public class AddSkillRequest {
  List<String> skillTitle;
  List<LevelSkill> level;
}
