package vn.hcmute.springboot.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.Recruiters;
import vn.hcmute.springboot.model.User;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Integer> {
  List<ApplicationForm> findByCandidateAndJob(User candidate, Job job);

  List<ApplicationForm> findByCandidate(User candidate);

  List<ApplicationForm> findByJobCompanyRecruiter(Recruiters recruiter);

  @Query("SELECT af FROM ApplicationForm af " +
          "WHERE af.id = :id " +
          "AND af.job.company.recruiter = :recruiter")
  ApplicationForm findByIdAndCompanyRecruiter(
          @Param("id") Integer id,
          @Param("recruiter") Recruiters recruiter
  );



}
