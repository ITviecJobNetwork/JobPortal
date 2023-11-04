package vn.hcmute.springboot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.CompanyReview;

public interface CompanyReviewRepository extends JpaRepository<CompanyReview, Integer> {
  List<CompanyReview> findByCompanyId(Integer companyId);

}
