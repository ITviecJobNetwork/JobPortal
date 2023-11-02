package vn.hcmute.springboot.service;


import java.util.List;
import org.springframework.data.domain.Page;
import vn.hcmute.springboot.model.Job;

public interface JobService {
  Page<Job> findAllJob(int page, int size);
  Page<Job> findJobByJobSkill(String skill,int page, int size);

  Page<Job> findJobByCandidateLevel(String level, int page, int size);

  Page<Job> findJobByCompanyName(String companyName,int page, int size);

  Page<Job> findByLocation(String location,int page, int size);

  Page<Job> findJobsWithFilters(
      String keyword,
      Double salaryMin,
      Double salaryMax,
      List<String> companyType,
      List<String> jobType,
      List<String> candidateLevel,
      int page,
      int size
  );




}
