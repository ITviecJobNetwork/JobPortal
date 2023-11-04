package vn.hcmute.springboot.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.CompanyReview;

public interface CompanyReviewRepository extends JpaRepository<CompanyReview, Integer> {
  Page<CompanyReview> findByCompanyId(Integer companyId, Pageable pageable);

}
