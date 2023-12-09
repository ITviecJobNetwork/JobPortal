package vn.hcmute.springboot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.CompanyKeySkill;

public interface CompanyKeySkillRepository extends JpaRepository<CompanyKeySkill, Integer>{

  List<CompanyKeySkill> findByCompanyId(Integer companyId);


}
