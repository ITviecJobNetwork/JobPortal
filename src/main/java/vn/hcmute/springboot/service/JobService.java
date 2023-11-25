package vn.hcmute.springboot.service;


import org.springframework.data.domain.Page;
import vn.hcmute.springboot.response.GetJobResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.ViewJobResponse;

import java.util.List;

public interface JobService {
  Page<GetJobResponse> findAllJob(int page, int size);

  Page<GetJobResponse> findJobByJobSkill(String skill, int page, int size);

  Page<GetJobResponse> findJobByCandidateLevel(String level, int page, int size);

  Page<GetJobResponse> findJobByCompanyName(String companyName, int page, int size);

  Page<GetJobResponse> findByLocation(String location, int page, int size);

  Page<GetJobResponse> findJobsWithFilters(
          String location,
          String keyword,
          Double salaryMin,
          Double salaryMax,
          List<String> companyType,
          List<String> jobType,
          List<String> candidateLevel,
          int page,
          int size,
          String salarySortDirection
  );

  GetJobResponse findJobById(Integer id);

  MessageResponse viewJob(Integer id);

  ViewJobResponse getViewAtJob(int page, int size, String sort);


}
