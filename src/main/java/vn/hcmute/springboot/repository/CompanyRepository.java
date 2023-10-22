package vn.hcmute.springboot.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer>
{
  Company findByName(String name);
  Optional<Company> findByNameIgnoreCase(String companyName);
}
