package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.User;

public interface ApplyJobRepository extends JpaRepository<ApplicationForm, Integer> {
  ApplicationForm findByCandidateAndJob(User user, Job job);


}
