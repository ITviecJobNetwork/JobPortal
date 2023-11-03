package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.JobType;

public interface JobTypeRepository extends JpaRepository<JobType, Integer> {

  JobType findByJobType(String jobType);

}
