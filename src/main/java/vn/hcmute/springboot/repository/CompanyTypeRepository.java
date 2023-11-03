package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.CompanyType;

public interface CompanyTypeRepository extends JpaRepository<CompanyType, Integer> {

  CompanyType findByType(String companyType);

}
