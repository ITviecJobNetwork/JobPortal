package vn.hcmute.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.Skill;
import vn.hcmute.springboot.model.ViewJobs;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.response.GetJobResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.ViewJobResponse;
import vn.hcmute.springboot.serviceImpl.JobServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

  private final JobServiceImpl jobService;
  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final ViewJobRepository viewJobRepository;
  private final SaveJobRepository saveJobsRepository;
  private final ApplyJobRepository applyJobRepository;
  private final SkillRepository skillRepository;


  @GetMapping()
  public ResponseEntity<Page<GetJobResponse>> getAllJobs(
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findAllJob(page, size);
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    List<GetJobResponse> getJobResponses = new ArrayList<>();
    for (Job job : jobs) {
      boolean isSaved = false;
      boolean isApplied = false;

      if (userName != null) {
        var user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
          var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
          var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
          isSaved = savedJob != null && savedJob.getIsSaved();
          isApplied = applyJob != null && applyJob.getIsApplied();
        }
      }
      var skills = skillRepository.findSkillByJob(job);
      List<String> skillNames = skills.stream()
              .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
              .toList();
      var getJobResponse = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(skillNames)
              .description(job.getDescription())
              .createdDate(job.getCreatedAt().toLocalDate())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .build();

      getJobResponses.add(getJobResponse);
    }

    Page<GetJobResponse> getJobResponsePage = new PageImpl<>(getJobResponses, PageRequest.of(page, size), jobs.getTotalElements());

    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
              new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0),
              HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(getJobResponsePage, HttpStatus.OK);
  }


  @GetMapping("/searchBySkill")
  public ResponseEntity<Page<GetJobResponse>> findJobsBySkill(@RequestParam("skill") String skillName,
                                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                                              @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByJobSkill(skillName, page, size);
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    List<GetJobResponse> getJobResponses = new ArrayList<>();
    for (Job job : jobs) {
      boolean isSaved = false;
      boolean isApplied = false;

      if (userName != null) {
        var user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
          var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
          var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
          isSaved = savedJob != null && savedJob.getIsSaved();
          isApplied = applyJob != null && applyJob.getIsApplied();
        }
      }
      var skills = skillRepository.findSkillByJob(job);
      List<String> skillNames = skills.stream()
              .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
              .toList();
      var getJobResponse = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(skillNames)
              .description(job.getDescription())
              .createdDate(job.getCreatedAt().toLocalDate())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .build();

      getJobResponses.add(getJobResponse);
    }

    Page<GetJobResponse> getJobResponsePage = new PageImpl<>(getJobResponses, PageRequest.of(page, size), jobs.getTotalElements());

    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
              new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0),
              HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(getJobResponsePage, HttpStatus.OK);
  }

  @GetMapping("/searchByCandidateLevel")
  public ResponseEntity<Page<GetJobResponse>> findJobByLevel(
          @RequestParam("candidateLevel") String candidateLevelName,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByCandidateLevel(candidateLevelName, page, size);
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    List<GetJobResponse> getJobResponses = new ArrayList<>();

    for (Job job : jobs) {
      boolean isSaved = false;
      boolean isApplied = false;

      if (userName != null) {
        var user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
          var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
          var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
          isSaved = savedJob != null && savedJob.getIsSaved();
          isApplied = applyJob != null && applyJob.getIsApplied();
        }
      }
      var skills = skillRepository.findSkillByJob(job);
      List<String> skillNames = skills.stream()
              .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
              .toList();
      var getJobResponse = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(skillNames)
              .description(job.getDescription())
              .createdDate(job.getCreatedAt().toLocalDate())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .build();

      getJobResponses.add(getJobResponse);
    }

    Page<GetJobResponse> getJobResponsePage = new PageImpl<>(getJobResponses, PageRequest.of(page, size), jobs.getTotalElements());

    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
              new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0),
              HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(getJobResponsePage, HttpStatus.OK);
  }

  @GetMapping("/searchByCompany")
  public ResponseEntity<Page<GetJobResponse>> findJobByCompany(@RequestParam("company") String companyName,
                                                               @RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findJobByCompanyName(companyName, page, size);
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    List<GetJobResponse> getJobResponses = new ArrayList<>();

    for (Job job : jobs) {
      boolean isSaved = false;
      boolean isApplied = false;

      if (userName != null) {
        var user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
          var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
          var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
          isSaved = savedJob != null && savedJob.getIsSaved();
          isApplied = applyJob != null && applyJob.getIsApplied();
        }
      }
      var skills = skillRepository.findSkillByJob(job);
      List<String> skillNames = skills.stream()
              .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
              .toList();
      var getJobResponse = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(skillNames)
              .description(job.getDescription())
              .createdDate(job.getCreatedAt().toLocalDate())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .build();

      getJobResponses.add(getJobResponse);
    }

    Page<GetJobResponse> getJobResponsePage = new PageImpl<>(getJobResponses, PageRequest.of(page, size), jobs.getTotalElements());

    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
              new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0),
              HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(getJobResponsePage, HttpStatus.OK);
  }

  @GetMapping("/searchByLocation")
  public ResponseEntity<Page<GetJobResponse>> findJobByLocation(
          @RequestParam("location") String locationName,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Job> jobs = jobService.findByLocation(locationName, page, size);
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    List<GetJobResponse> getJobResponses = new ArrayList<>();

    for (Job job : jobs) {
      boolean isSaved = false;
      boolean isApplied = false;

      if (userName != null) {
        var user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
          var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
          var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
          isSaved = savedJob != null && savedJob.getIsSaved();
          isApplied = applyJob != null && applyJob.getIsApplied();
        }
      }
      var skills = skillRepository.findSkillByJob(job);
      List<String> skillNames = skills.stream()
              .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
              .toList();
      var getJobResponse = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(skillNames)
              .description(job.getDescription())
              .createdDate(job.getCreatedAt().toLocalDate())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .build();

      getJobResponses.add(getJobResponse);
    }

    Page<GetJobResponse> getJobResponsePage = new PageImpl<>(getJobResponses, PageRequest.of(page, size), jobs.getTotalElements());

    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
              new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0),
              HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(getJobResponsePage, HttpStatus.OK);
  }


  @GetMapping("/searchByKeyword")
  public ResponseEntity<Page<GetJobResponse>> findJobs(
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

    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    List<GetJobResponse> getJobResponses = new ArrayList<>();

    for (Job job : jobs) {
      boolean isSaved = false;
      boolean isApplied = false;

      if (userName != null) {
        var user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
          var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
          var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
          isSaved = savedJob != null && savedJob.getIsSaved();
          isApplied = applyJob != null && applyJob.getIsApplied();
        }
      }
      var skills = skillRepository.findSkillByJob(job);
      List<String> skillNames = skills.stream()
              .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
              .toList();
      var getJobResponse = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(skillNames)
              .description(job.getDescription())
              .createdDate(job.getCreatedAt().toLocalDate())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .build();

      getJobResponses.add(getJobResponse);
    }

    Page<GetJobResponse> getJobResponsePage = new PageImpl<>(getJobResponses, PageRequest.of(page, size), jobs.getTotalElements());

    if (jobs.isEmpty()) {
      return new ResponseEntity<>(
              new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0),
              HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(getJobResponsePage, HttpStatus.OK);
  }


  @GetMapping("{id}")
  public ResponseEntity<GetJobResponse> getJobById(@PathVariable Integer id) {
    var jobOptional = jobRepository.findById(id);
    if (jobOptional.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    var job = jobOptional.get();
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      var getJobResponse = createGetJobResponse(job, false, false);
      return new ResponseEntity<>(getJobResponse, HttpStatus.OK);
    }
    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      var getJobResponse = createGetJobResponse(job, false, false);
      return new ResponseEntity<>(getJobResponse, HttpStatus.OK);
    }
    var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
    var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
    boolean isSaved = savedJob != null && savedJob.getIsSaved();
    boolean isApplied = applyJob != null && applyJob.getIsApplied();

    var getJobResponse = createGetJobResponse(job, isSaved, isApplied);
    return new ResponseEntity<>(getJobResponse, HttpStatus.OK);
  }

  private GetJobResponse createGetJobResponse(Job job, boolean isSaved, boolean isApplied) {

    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
            .toList();
    return GetJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt().toLocalDate())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .isSaved(isSaved)
            .isApplied(isApplied)
            .build();
  }


  @PostMapping("/{id}/viewJob")
  public ResponseEntity<MessageResponse> viewJob(@PathVariable Integer id) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      return new ResponseEntity<>((new MessageResponse("Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED)),
              HttpStatus.UNAUTHORIZED);

    }
    var job = jobRepository.findById(id);
    if (job.isEmpty()) {
      return new ResponseEntity<>(
              (new MessageResponse("Công việc không tồn tại", HttpStatus.NOT_FOUND)), HttpStatus.NOT_FOUND);
    } else {

      var viewJob = new ViewJobs();
      viewJob.setCandidate(user.get());
      viewJob.setJob(job.get());
      viewJob.setViewAt(LocalDateTime.now());
      viewJob.setIsViewed(true);
      viewJobRepository.save(viewJob);

    }
    return new ResponseEntity<>((new MessageResponse("Xem công việc thành công", HttpStatus.OK)),
            HttpStatus.OK);
  }

  @GetMapping("/viewAtJob")
  public ResponseEntity<ViewJobResponse> getViewAtJob(
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size,
          @RequestParam(value = "sort", defaultValue = "Xem gần nhất") String sort) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      return new ResponseEntity<>((new ViewJobResponse("Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED)),
              HttpStatus.UNAUTHORIZED);
    }
    PageRequest request = PageRequest.of(page, size);
    Page<ViewJobs> viewJob = viewJobRepository.findJobsViewedByUser(user.get(), request);
    if ("Sắp hết hạn".equals(sort)) {
      viewJob = viewJobRepository.findJobsViewedByUserAndSortByExpireAt(user.get(), request);
    }
    if ("Đăng mới nhất".equals(sort)) {
      viewJob = viewJobRepository.findJobsViewedByUserAndSortByCreatedAt(user.get(), request);
    }
    if ("Xem gần nhất".equals(sort)) {
      request = PageRequest.of(page, size, Sort.by(Order.desc("viewAt")));
    }

    if (viewJob.isEmpty()) {
      return new ResponseEntity<>(
              (new ViewJobResponse("Bạn chưa xem công việc nào", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    } else {
      List<GetJobResponse> getJobResponses = viewJob.getContent().stream()
              .map(viewJobResponse -> createGetJobResponse(viewJobResponse.getJob(), false, false)) // Assuming there's a method to map ViewJob to GetJobResponse
              .collect(Collectors.toList());
      Page<GetJobResponse> getJobResponsesPage = new PageImpl<>(getJobResponses, viewJob.getPageable(), viewJob.getTotalElements());
      return new ResponseEntity<>(new ViewJobResponse(getJobResponsesPage), HttpStatus.OK);
    }
  }


}
