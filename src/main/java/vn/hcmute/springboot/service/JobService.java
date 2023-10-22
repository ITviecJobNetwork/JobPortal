package vn.hcmute.springboot.service;


import java.util.List;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.Job;

public interface JobService {
  List<Job> findAllJob();
  List<Job> findJobByJobSkill(String skill);

  List<Job> findJobByCandidateLevel(String level);

  List<Job> findJobByCompanyName(String companyName);

  List<Job> findByLocation(String location);

  List<Job> findJobByKeyWord (String keyword);

}
