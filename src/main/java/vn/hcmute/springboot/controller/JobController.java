package vn.hcmute.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.springboot.response.GetJobResponse;
import vn.hcmute.springboot.response.KeywordResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.ViewJobResponse;
import vn.hcmute.springboot.serviceImpl.JobServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

  private final JobServiceImpl jobService;

  @GetMapping()
  public ResponseEntity<?> getAllJobs(
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<GetJobResponse> jobs = jobService.findAllJob(page, size);
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }


  @GetMapping("/searchBySkill")
  public ResponseEntity<Page<GetJobResponse>> findJobsBySkill(@RequestParam("skill") String skillName,
                                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                                              @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<GetJobResponse> jobs = jobService.findJobByJobSkill(skillName, page, size);
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/searchByCandidateLevel")
  public ResponseEntity<Page<GetJobResponse>> findJobByLevel(
          @RequestParam("candidateLevel") String candidateLevelName,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<GetJobResponse> jobs = jobService.findJobByCandidateLevel(candidateLevelName, page, size);
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/searchByCompany")
  public ResponseEntity<Page<GetJobResponse>> findJobByCompany(@RequestParam("company") String companyName,
                                                               @RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<GetJobResponse> jobs = jobService.findJobByCompanyName(companyName, page, size);
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/searchByLocation")
  public ResponseEntity<Page<GetJobResponse>> findJobByLocation(
          @RequestParam("location") String locationName,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<GetJobResponse> jobs = jobService.findByLocation(locationName, page, size);

    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }


  @GetMapping("/searchByKeyword")
  public ResponseEntity<Page<GetJobResponse>> findJobs(
          @RequestParam(value = "location", required = false) String location,
          @RequestParam(value = "keyword", required = false) String keyword,
          @RequestParam(value = "salaryMin", required = false) Double salaryMin,
          @RequestParam(value = "salaryMax", required = false) Double salaryMax,
          @RequestParam(value = "companyType", required = false) List<String> companyType,
          @RequestParam(value = "jobType", required = false) List<String> jobType,
          @RequestParam(value = "candidateLevel", required = false) List<String> candidateLevel,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size,
          @RequestParam(value = "salarySortDirection", defaultValue = "DESC") String salarySortDirection) {

    Page<GetJobResponse> jobs = jobService.findJobsWithFilters(location,keyword, salaryMin, salaryMax, companyType,
            jobType, candidateLevel, page, size, salarySortDirection);

    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }


  @GetMapping("{id}")
  public ResponseEntity<GetJobResponse> getJobById(@PathVariable Integer id) {
    var job = jobService.findJobById(id);
    return new ResponseEntity<>(job, HttpStatus.OK);
  }


  @PostMapping("/{id}/viewJob")
  public ResponseEntity<MessageResponse> viewJob(@PathVariable Integer id) {
    var viewJob = jobService.viewJob(id);
    return new ResponseEntity<>(viewJob, HttpStatus.OK);
  }

  @GetMapping("/viewAtJob")
  public ResponseEntity<ViewJobResponse> getViewAtJob(
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size,
          @RequestParam(value = "sort", defaultValue = "Xem gần nhất") String sort) {
    var viewJob = jobService.getViewAtJob(page, size, sort);
    return new ResponseEntity<>(viewJob, HttpStatus.OK);
  }

  @GetMapping("/suggestKeyWord")
  public ResponseEntity<KeywordResponse> suggestKeyWord() {
    var keyWord = jobService.suggestKeyWord();
    return new ResponseEntity<>(keyWord, HttpStatus.OK);
  }
}
