package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.CompanyFollow;

public interface CompanyFollowRepository extends JpaRepository<CompanyFollow, Integer> {

  CompanyFollow findByUserIdAndCompanyId(Integer userId, Integer companyId);

  CompanyFollow findByCompanyId(Integer companyId);



}
