package vn.hcmute.springboot.service;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.Job;

public interface JobService {
  Page<Job> findAllJob(int page, int size);
  Page<Job> findJobByJobSkill(String skill,int page, int size);

  Page<Job> findJobByCandidateLevel(String level, int page, int size);

  Page<Job> findJobByCompanyName(String companyName,int page, int size);

  Page<Job> findByLocation(String location,int page, int size);

  Page<Job> findJobByKeyWord (String keyword,int page, int size);

}
