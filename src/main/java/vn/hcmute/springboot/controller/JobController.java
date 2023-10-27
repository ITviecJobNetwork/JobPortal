package vn.hcmute.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.serviceImpl.JobServiceImpl;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

  private final JobServiceImpl jobService;

  @GetMapping()
  public ResponseEntity<Page<Job>> getAllJobs(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    var jobs = jobService.findAllJob(page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(jobService.findAllJob(page, size),HttpStatus.OK);
  }

  @GetMapping("/searchBySkill")
  public ResponseEntity<Page<Job>> findJobsBySkill(@RequestParam("skill") String skillName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByJobSkill(skillName, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/searchByCandidateLevel")
  public ResponseEntity<Page<Job>> findJobByLevel(
      @RequestParam("candidateLevel") String candidateLevelName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByCandidateLevel(candidateLevelName, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/searchByCompany")
  public ResponseEntity<Page<Job>> findJobByCompany(@RequestParam("company") String companyName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByCompanyName(companyName, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/searchByLocation")
  public ResponseEntity<Page<Job>> findJobByLocation(
      @RequestParam("location") String locationName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findByLocation(locationName, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }


  @GetMapping("/searchByKeyword")
  public ResponseEntity<Page<Job>> findJobs(
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByKeyWord(keyword, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }


}
