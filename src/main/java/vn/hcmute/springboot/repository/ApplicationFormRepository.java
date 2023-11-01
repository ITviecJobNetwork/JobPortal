package vn.hcmute.springboot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.User;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Integer> {
  List<ApplicationForm> findByCandidateAndJob(User candidate, Job job);

  List<ApplicationForm> findByCandidate(User candidate);

}
