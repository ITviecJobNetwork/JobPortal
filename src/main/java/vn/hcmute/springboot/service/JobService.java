package vn.hcmute.springboot.service;


import java.util.List;
import org.springframework.data.domain.Page;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.response.GetJobResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.ViewJobResponse;

public interface JobService {
  Page<GetJobResponse> findAllJob(int page, int size);
  Page<GetJobResponse> findJobByJobSkill(String skill,int page, int size);

  Page<GetJobResponse> findJobByCandidateLevel(String level, int page, int size);

  Page<GetJobResponse> findJobByCompanyName(String companyName,int page, int size);

  Page<GetJobResponse> findByLocation(String location,int page, int size);

  Page<GetJobResponse> findJobsWithFilters(
      String keyword,
      Double salaryMin,
      Double salaryMax,
      List<String> companyType,
      List<String> jobType,
      List<String> candidateLevel,
      int page,
      int size
  );
  GetJobResponse findJobById(Integer id);

  MessageResponse viewJob(Integer id);

  ViewJobResponse getViewAtJob(int page, int size, String sort);




}
