package vn.hcmute.springboot.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.SaveJobs;
import vn.hcmute.springboot.model.User;

public interface SaveJobRepository extends JpaRepository<SaveJobs, Integer> {
  List<SaveJobs> findByCandidate(User candidate);
  SaveJobs findByCandidateAndJob(User user,Job job);
  SaveJobs findJobByIsSavedTrue();
  boolean existsByCandidateAndJob(User user,Job job);

  Optional<SaveJobs> findByIdAndCandidate(Integer id, User candidate);

  boolean existsByJob(Job job);
  void deleteByJob(Job job);


}
