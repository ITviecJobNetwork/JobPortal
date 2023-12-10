package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.JobType;

import java.util.List;
import java.util.Optional;

public interface JobTypeRepository extends JpaRepository<JobType, Integer> {

  Optional<JobType> findByJobType(String jobType);

  Optional<JobType> findFirstByJobType(String jobType);


}
