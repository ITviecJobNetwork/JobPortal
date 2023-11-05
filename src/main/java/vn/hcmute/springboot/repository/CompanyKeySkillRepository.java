package vn.hcmute.springboot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.CompanyKeySkill;
import vn.hcmute.springboot.model.Skill;

public interface CompanyKeySkillRepository extends JpaRepository<CompanyKeySkill, Integer>{
  List<CompanyKeySkill> findByRecruiterId(Integer recruiterId);


}
