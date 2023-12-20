package vn.hcmute.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.*;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Integer> {
  ApplicationForm findByCandidateAndJob(User candidate, Job job);

  List<ApplicationForm> findByCandidate(User candidate);

  Page<ApplicationForm> findByJobCompanyRecruiter(Recruiters recruiter, Pageable pageable);
  boolean existsByJob(Job job);

  void deleteByJob(Job job);
  @Query("SELECT af FROM ApplicationForm af " +
          "WHERE af.id = :id " +
          "AND af.job.company.recruiter = :recruiter")
  ApplicationForm findByIdAndCompanyRecruiter(
          @Param("id") Integer id,
          @Param("recruiter") Recruiters recruiter
  );




  Page<ApplicationForm> findByJobCompanyRecruiterAndStatus(Recruiters recruiters, ApplicationStatus applicationStatus, Pageable pageable);
}
