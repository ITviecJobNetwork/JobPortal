package vn.hcmute.springboot.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.repository.JobRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.response.JobResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.serviceImpl.JobServiceImpl;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

  private final JobServiceImpl jobService;
  private final JobRepository jobRepository;
  private final UserRepository userRepository;

  @GetMapping()
  public ResponseEntity<JobResponse> getAllJobs(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findAllJob(page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
          new JobResponse(jobs, "Không tìm thấy công việc", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(new JobResponse(jobs), HttpStatus.OK);
  }


  @GetMapping("/searchBySkill")
  public ResponseEntity<JobResponse> findJobsBySkill(@RequestParam("skill") String skillName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByJobSkill(skillName, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
          new JobResponse(jobs, "Không tìm thấy công việc", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(new JobResponse(jobs), HttpStatus.OK);
  }

  @GetMapping("/searchByCandidateLevel")
  public ResponseEntity<JobResponse> findJobByLevel(
      @RequestParam("candidateLevel") String candidateLevelName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByCandidateLevel(candidateLevelName, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
          new JobResponse(jobs, "Không tìm thấy công việc", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(new JobResponse(jobs), HttpStatus.OK);
  }

  @GetMapping("/searchByCompany")
  public ResponseEntity<JobResponse> findJobByCompany(@RequestParam("company") String companyName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByCompanyName(companyName, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
          new JobResponse(jobs, "Không tìm thấy công việc", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(new JobResponse(jobs), HttpStatus.OK);
  }

  @GetMapping("/searchByLocation")
  public ResponseEntity<JobResponse> findJobByLocation(
      @RequestParam("location") String locationName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findByLocation(locationName, page, size);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
          new JobResponse(jobs, "Không tìm thấy công việc", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(new JobResponse(jobs), HttpStatus.OK);
  }


  @GetMapping("/searchByKeyword")
  public ResponseEntity<JobResponse> findJobs(
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "salaryMin", required = false) Double salaryMin,
      @RequestParam(value = "salaryMax", required = false) Double salaryMax,
      @RequestParam(value = "companyType", required = false) List<String> companyType,
      @RequestParam(value = "jobType", required = false) List<String> jobType,
      @RequestParam(value = "candidateLevel", required = false) List<String> candidateLevel,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {

    Page<Job> jobs = jobService.findJobsWithFilters(keyword, salaryMin, salaryMax, companyType,
        jobType, candidateLevel, page, size);

    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
          new JobResponse(jobs, "Không tìm thấy công việc", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(new JobResponse(jobs), HttpStatus.OK);
  }


  @GetMapping("{id}")
  public ResponseEntity<Job> getJobById(@PathVariable Integer id) {
    var jobOptional = jobRepository.findById(id);
    if (jobOptional.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Job job = jobOptional.get();
    return new ResponseEntity<>(job, HttpStatus.OK);
  }
  @PostMapping("/{id}/readAt")
  public ResponseEntity<JobResponse> isReadAtJob(@PathVariable Integer id){
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if(userName == null) {
      return new ResponseEntity<>(new JobResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName);
    if(user.isEmpty()){
      return new ResponseEntity<>((new JobResponse("Bạn chưa đăng nhập",HttpStatus.UNAUTHORIZED)),HttpStatus.UNAUTHORIZED);

    }
    var job = jobRepository.findById(id);
    if(job.isEmpty()){
      return new ResponseEntity<>((new JobResponse("Công việc không tồn tại",HttpStatus.NOT_FOUND)),HttpStatus.NOT_FOUND);
    }
    else{
      job.get().setReadAt(LocalDateTime.now());
      job.get().setIsReadAt(true);
      jobRepository.save(job.get());
    }
    return new ResponseEntity<>((new JobResponse("Bạn đã đọc công việc này",HttpStatus.OK)),HttpStatus.OK);
  }

  @GetMapping("/isReadAt")
  public ResponseEntity<JobResponse> getReadAtJob(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value="sort", defaultValue = "Xem gần nhất") String sort){
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName);
    if(user.isEmpty()){
      return new ResponseEntity<>((new JobResponse("Bạn chưa đăng nhập",HttpStatus.UNAUTHORIZED)),HttpStatus.UNAUTHORIZED);
    }
    PageRequest request = PageRequest.of(page,size);


    if("Sắp hết hạn".equals(sort)){
      request = PageRequest.of(page,size, Sort.by(Order.asc("expireAt")));
    }
    if("Đăng mới nhất".equals(sort)){
      request = PageRequest.of(page,size, Sort.by(Order.desc("createdAt")));
    }
    if("Xem gần nhất".equals(sort)){
      request = PageRequest.of(page,size, Sort.by(Order.desc("readAt")));
    }
    Page<Job> job = jobRepository.findJobByIsReadAtTrue(request);
    if(job.isEmpty()){
      return new ResponseEntity<>((new JobResponse("Bạn chưa xem công việc nào",HttpStatus.NOT_FOUND)),HttpStatus.NOT_FOUND);
    }
    else{

      return new ResponseEntity<>((new JobResponse(job)),HttpStatus.OK);
    }
  }




}
