package vn.hcmute.springboot.serviceImpl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.exception.UnauthorizedException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.response.GetJobResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.ViewJobResponse;
import vn.hcmute.springboot.service.JobService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;
  private final SkillRepository skillRepository;
  private final CandidateLevelRepository candidateLevelRepository;
  private final CompanyRepository companyRepository;
  private final LocationRepository locationRepository;
  private final UserRepository userRepository;
  private final SaveJobRepository saveJobsRepository;
  private final ApplicationFormRepository applyJobRepository;
  private final ViewJobRepository viewJobRepository;

  @Override
  public Page<GetJobResponse> findAllJob(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    var allJobs = jobRepository.findAllJobs(pageable);
    if (allJobs.isEmpty()) {
      throw new NotFoundException("Hiện tại không có công việc nào");
    }
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    List<GetJobResponse> getJobResponses = new ArrayList<>();
    for (Job job : allJobs) {
      boolean isSaved = false;
      boolean isApplied = false;

      if (userName != null) {
        var user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
          var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
          var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
          isSaved = savedJob != null && savedJob.getIsSaved();
          isApplied = applyJob != null && applyJob.getIsApplied();
          var applicationForm = job.getApplicationForms();
          var submittedAt = applicationForm.stream()
                  .filter(applicationForm1 -> applicationForm1.getCandidate().equals(user.get()))
                  .map(ApplicationForm::getSubmittedAt)
                  .findFirst()
                  .orElse(null);
          var response = GetJobResponse.builder()
                  .jobId(job.getId())
                  .title(job.getTitle())
                  .companyId(job.getCompany().getId())
                  .companyName(job.getCompany().getName())
                  .address(job.getCompany().getAddress())
                  .skills(job.getSkills().stream().map(Skill::getTitle).toList())
                  .description(job.getDescription())
                  .createdDate(job.getCreatedAt())
                  .expiredDate(job.getExpireAt())
                  .requirements(job.getRequirements())
                  .jobType(job.getJobType().getJobType())
                  .location(job.getLocation().getCityName())
                  .minSalary(job.getMinSalary())
                  .maxSalary(job.getMaxSalary())
                  .isSaved(isSaved)
                  .isApplied(isApplied)
                  .appliedAt(submittedAt)
                  .build();
          getJobResponses.add(response);
        }
      }
      var skills = skillRepository.findSkillByJob(job);

      List<String> skillNames = skills.stream()
              .map(Skill::getTitle)
              .toList();
      var getJobResponse = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyId(job.getCompany().getId())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(skillNames)
              .description(job.getDescription())
              .createdDate(job.getCreatedAt())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .minSalary(job.getMinSalary())
              .maxSalary(job.getMaxSalary())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .build();
      getJobResponses.add(getJobResponse);
      return allJobs.map(jobs -> {
        String jobLevel = determineJobLevel(jobs);
        return createGetJobResponseWithLevel(jobs, jobLevel);
      });

    }
    return new PageImpl<>(getJobResponses, PageRequest.of(page, size), allJobs.getTotalElements());
  }

  @Override
  public Page<GetJobResponse> findJobByJobSkill(String skill, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Skill skillName = skillRepository.findByName(skill);

    if (skill != null) {
      var jobs = jobRepository.findJobsBySkills(skillName, pageable);
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
            var applicationForm = job.getApplicationForms();
            var submittedAt = applicationForm.stream()
                    .filter(applicationForm1 -> applicationForm1.getCandidate().equals(user.get()))
                    .map(ApplicationForm::getSubmittedAt)
                    .findFirst()
                    .orElse(null);
            var response = GetJobResponse.builder()
                    .jobId(job.getId())
                    .title(job.getTitle())
                    .companyId(job.getCompany().getId())
                    .companyName(job.getCompany().getName())
                    .address(job.getCompany().getAddress())
                    .skills(job.getSkills().stream().map(Skill::getTitle).toList())
                    .description(job.getDescription())
                    .createdDate(job.getCreatedAt())
                    .expiredDate(job.getExpireAt())
                    .requirements(job.getRequirements())
                    .jobType(job.getJobType().getJobType())
                    .location(job.getLocation().getCityName())
                    .minSalary(job.getMinSalary())
                    .maxSalary(job.getMaxSalary())
                    .isSaved(isSaved)
                    .isApplied(isApplied)
                    .appliedAt(submittedAt)
                    .build();
            getJobResponses.add(response);
          }
        }
        var skills = skillRepository.findSkillByJob(job);
        List<String> skillNames = skills.stream()
                .map(Skill::getTitle)
                .toList();
        var getJobResponse = GetJobResponse.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .address(job.getCompany().getAddress())
                .skills(skillNames)
                .description(job.getDescription())
                .createdDate(job.getCreatedAt())
                .expiredDate(job.getExpireAt())
                .requirements(job.getRequirements())
                .jobType(job.getJobType().getJobType())
                .location(job.getLocation().getCityName())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .isSaved(isSaved)
                .isApplied(isApplied)
                .build();

        getJobResponses.add(getJobResponse);
        return jobs.map(jobLevels -> {
          String jobLevel = determineJobLevel(jobLevels);
          return createGetJobResponseWithLevel(jobLevels, jobLevel);
        });
      }
      return new PageImpl<>(getJobResponses, PageRequest.of(page, size), jobs.getTotalElements());
    }
    throw new NotFoundException("Không có công việc với tên kỹ năng " + skill);


  }

  @Override
  public Page<GetJobResponse> findJobByCandidateLevel(String level, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    var candidateLevel = candidateLevelRepository.findByCandidateLevel(level);
    if (candidateLevel != null) {
      var result = jobRepository.findJobsByCandidateLevel(candidateLevel.getCandidateLevel(), pageable);
      var userName = SecurityContextHolder.getContext().getAuthentication().getName();
      List<GetJobResponse> getJobResponses = new ArrayList<>();
      for (Job job : result) {
        boolean isSaved = false;
        boolean isApplied = false;

        if (userName != null) {
          var user = userRepository.findByUsername(userName);
          if (user.isPresent()) {
            var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
            var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
            isSaved = savedJob != null && savedJob.getIsSaved();
            isApplied = applyJob != null && applyJob.getIsApplied();
            var applicationForm = job.getApplicationForms();
            var submittedAt = applicationForm.stream()
                    .filter(applicationForm1 -> applicationForm1.getCandidate().equals(user.get()))
                    .map(ApplicationForm::getSubmittedAt)
                    .findFirst()
                    .orElse(null);
            var response = GetJobResponse.builder()
                    .jobId(job.getId())
                    .title(job.getTitle())
                    .companyId(job.getCompany().getId())
                    .companyName(job.getCompany().getName())
                    .address(job.getCompany().getAddress())
                    .skills(job.getSkills().stream().map(Skill::getTitle).toList())
                    .description(job.getDescription())
                    .createdDate(job.getCreatedAt())
                    .expiredDate(job.getExpireAt())
                    .requirements(job.getRequirements())
                    .jobType(job.getJobType().getJobType())
                    .location(job.getLocation().getCityName())
                    .minSalary(job.getMinSalary())
                    .maxSalary(job.getMaxSalary())
                    .isSaved(isSaved)
                    .isApplied(isApplied)
                    .appliedAt(submittedAt)
                    .build();
            getJobResponses.add(response);
          }
        }
        var skills = skillRepository.findSkillByJob(job);
        List<String> skillNames = skills.stream()
                .map(Skill::getTitle)
                .toList();
        var getJobResponse = GetJobResponse.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .address(job.getCompany().getAddress())
                .skills(skillNames)
                .description(job.getDescription())
                .createdDate(job.getCreatedAt())
                .expiredDate(job.getExpireAt())
                .requirements(job.getRequirements())
                .jobType(job.getJobType().getJobType())
                .location(job.getLocation().getCityName())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .isSaved(isSaved)
                .isApplied(isApplied)
                .build();

        getJobResponses.add(getJobResponse);
        return result.map(jobLevels -> {
          String jobLevel = determineJobLevel(jobLevels);
          return createGetJobResponseWithLevel(jobLevels, jobLevel);
        });
      }
      return new PageImpl<>(getJobResponses, PageRequest.of(page, size), result.getTotalElements());
    }
    throw new NotFoundException("Không có công việc với tên level " + level);
  }

  @Override
  public Page<GetJobResponse> findJobByCompanyName(String companyName, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    var company = companyRepository.findCompanyByName(companyName, pageable);
    if (company != null) {
      var result = jobRepository.findJobsByCompanyName(companyName, pageable);
      var userName = SecurityContextHolder.getContext().getAuthentication().getName();
      List<GetJobResponse> getJobResponses = new ArrayList<>();
      for (Job job : result) {
        boolean isSaved = false;
        boolean isApplied = false;

        if (userName != null) {
          var user = userRepository.findByUsername(userName);
          if (user.isPresent()) {
            var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
            var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
            isSaved = savedJob != null && savedJob.getIsSaved();
            isApplied = applyJob != null && applyJob.getIsApplied();
            var applicationForm = job.getApplicationForms();
            var submittedAt = applicationForm.stream()
                    .filter(applicationForm1 -> applicationForm1.getCandidate().equals(user.get()))
                    .map(ApplicationForm::getSubmittedAt)
                    .findFirst()
                    .orElse(null);
            var response = GetJobResponse.builder()
                    .jobId(job.getId())
                    .title(job.getTitle())
                    .companyId(job.getCompany().getId())
                    .companyName(job.getCompany().getName())
                    .address(job.getCompany().getAddress())
                    .skills(job.getSkills().stream().map(Skill::getTitle).toList())
                    .description(job.getDescription())
                    .createdDate(job.getCreatedAt())
                    .expiredDate(job.getExpireAt())
                    .requirements(job.getRequirements())
                    .jobType(job.getJobType().getJobType())
                    .location(job.getLocation().getCityName())
                    .minSalary(job.getMinSalary())
                    .maxSalary(job.getMaxSalary())
                    .isSaved(isSaved)
                    .isApplied(isApplied)
                    .appliedAt(submittedAt)
                    .build();
            getJobResponses.add(response);
          }
        }
        var skills = skillRepository.findSkillByJob(job);
        List<String> skillNames = skills.stream()
                .map(Skill::getTitle)
                .toList();
        var getJobResponse = GetJobResponse.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .address(job.getCompany().getAddress())
                .skills(skillNames)
                .description(job.getDescription())
                .createdDate(job.getCreatedAt())
                .expiredDate(job.getExpireAt())
                .requirements(job.getRequirements())
                .jobType(job.getJobType().getJobType())
                .location(job.getLocation().getCityName())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .isSaved(isSaved)
                .isApplied(isApplied)
                .build();

        getJobResponses.add(getJobResponse);
        return result.map(jobLevels -> {
          String jobLevel = determineJobLevel(jobLevels);
          return createGetJobResponseWithLevel(jobLevels, jobLevel);
        });
      }
      return new PageImpl<>(getJobResponses, PageRequest.of(page, size), result.getTotalElements());
    }
    throw new NotFoundException("Không có công việc với tên công ty là " + companyName);
  }

  @Override
  public Page<GetJobResponse> findByLocation(String location, int page, int size) {
    var locationName = locationRepository.findByCityName(location);
    Pageable pageable = PageRequest.of(page, size);
    if (locationName != null) {
      var result = jobRepository.findJobByLocation(location, pageable);
      var userName = SecurityContextHolder.getContext().getAuthentication().getName();
      List<GetJobResponse> getJobResponses = new ArrayList<>();
      for (Job job : result) {
        boolean isSaved = false;
        boolean isApplied = false;

        if (userName != null) {
          var user = userRepository.findByUsername(userName);
          if (user.isPresent()) {
            var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
            var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
            isSaved = savedJob != null && savedJob.getIsSaved();
            isApplied = applyJob != null && applyJob.getIsApplied();
            var applicationForm = job.getApplicationForms();
            var submittedAt = applicationForm.stream()
                    .filter(applicationForm1 -> applicationForm1.getCandidate().equals(user.get()))
                    .map(ApplicationForm::getSubmittedAt)
                    .findFirst()
                    .orElse(null);
            var response = GetJobResponse.builder()
                    .jobId(job.getId())
                    .title(job.getTitle())
                    .companyId(job.getCompany().getId())
                    .companyName(job.getCompany().getName())
                    .address(job.getCompany().getAddress())
                    .skills(job.getSkills().stream().map(Skill::getTitle).toList())
                    .description(job.getDescription())
                    .createdDate(job.getCreatedAt())
                    .expiredDate(job.getExpireAt())
                    .requirements(job.getRequirements())
                    .jobType(job.getJobType().getJobType())
                    .location(job.getLocation().getCityName())
                    .minSalary(job.getMinSalary())
                    .maxSalary(job.getMaxSalary())
                    .isSaved(isSaved)
                    .isApplied(isApplied)
                    .appliedAt(submittedAt)
                    .build();
            getJobResponses.add(response);
          }
        }
        var skills = skillRepository.findSkillByJob(job);
        List<String> skillNames = skills.stream()
                .map(Skill::getTitle)
                .toList();
        var getJobResponse = GetJobResponse.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .address(job.getCompany().getAddress())
                .skills(skillNames)
                .description(job.getDescription())
                .createdDate(job.getCreatedAt())
                .expiredDate(job.getExpireAt())
                .requirements(job.getRequirements())
                .jobType(job.getJobType().getJobType())
                .location(job.getLocation().getCityName())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .isSaved(isSaved)
                .isApplied(isApplied)
                .build();

        getJobResponses.add(getJobResponse);
        return result.map(jobLevels -> {
          String jobLevel = determineJobLevel(jobLevels);
          return createGetJobResponseWithLevel(jobLevels, jobLevel);
        });
      }
      return new PageImpl<>(getJobResponses, PageRequest.of(page, size), result.getTotalElements());
    }
    throw new NotFoundException("Không có công việc với tên địa điểm là " + location);
  }

  @Override
  public Page<GetJobResponse> findJobsWithFilters(
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
  ) {
    Sort sort = Sort.unsorted();
    if (salarySortDirection != null && !salarySortDirection.isEmpty()) {
      sort = Sort.by(Sort.Direction.fromString(salarySortDirection), "minSalary");
    }

    Pageable pageable = PageRequest.of(page, size, sort);
    var result = jobRepository.findByKeywordAndFilters(
            location,
            keyword,
            salaryMin,
            salaryMax,
            companyType,
            jobType,
            candidateLevel,
            pageable
    );
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    List<GetJobResponse> getJobResponses = new ArrayList<>();
    for (Job job : result) {
      boolean isSaved = false;
      boolean isApplied = false;

      if (userName != null) {
        var user = userRepository.findByUsername(userName);
        if (user.isPresent()) {
          var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
          var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
          isSaved = savedJob != null && savedJob.getIsSaved();
          isApplied = applyJob != null && applyJob.getIsApplied();
          var applicationForm = job.getApplicationForms();
          var submittedAt = applicationForm.stream()
                  .filter(applicationForm1 -> applicationForm1.getCandidate().equals(user.get()))
                  .map(ApplicationForm::getSubmittedAt)
                  .findFirst()
                  .orElse(null);
          var response = GetJobResponse.builder()
                  .jobId(job.getId())
                  .title(job.getTitle())
                  .companyId(job.getCompany().getId())
                  .companyName(job.getCompany().getName())
                  .address(job.getCompany().getAddress())
                  .skills(job.getSkills().stream().map(Skill::getTitle).toList())
                  .description(job.getDescription())
                  .createdDate(job.getCreatedAt())
                  .expiredDate(job.getExpireAt())
                  .requirements(job.getRequirements())
                  .jobType(job.getJobType().getJobType())
                  .location(job.getLocation().getCityName())
                  .minSalary(job.getMinSalary())
                  .maxSalary(job.getMaxSalary())
                  .isSaved(isSaved)
                  .isApplied(isApplied)
                  .appliedAt(submittedAt)
                  .build();
          getJobResponses.add(response);
        }
      }
      var skills = skillRepository.findSkillByJob(job);
      List<String> skillNames = skills.stream()
              .map(Skill::getTitle)
              .toList();


      var getJobResponse = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyId(job.getCompany().getId())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(skillNames)
              .description(job.getDescription())
              .createdDate(job.getCreatedAt())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .minSalary(job.getMinSalary())
              .maxSalary(job.getMaxSalary())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .appliedAt(null)
              .build();

      getJobResponses.add(getJobResponse);
      return result.map(jobLevels -> {
        String jobLevel = determineJobLevel(jobLevels);
        return createGetJobResponseWithLevel(jobLevels, jobLevel);
      });
    }
    return new PageImpl<>(getJobResponses, PageRequest.of(page, size), result.getTotalElements());
  }

  @Override
  public GetJobResponse findJobById(Integer id) {
    var jobOptional = jobRepository.findById(id);
    if (jobOptional.isEmpty()) {
      throw new NotFoundException("Không tìm thấy công việc với id là " + id);
    }
    var job = jobOptional.get();
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userRepository.findByUsername(userName);
    if (user.isPresent()) {
      var savedJob = saveJobsRepository.findByCandidateAndJob(user.get(), job);
      var applyJob = applyJobRepository.findByCandidateAndJob(user.get(), job);
      boolean isSaved = savedJob != null && savedJob.getIsSaved();
      boolean isApplied = applyJob != null && applyJob.getIsApplied();
      var applicationForm = job.getApplicationForms();
      var submittedAt = applicationForm.stream()
              .filter(applicationForm1 -> applicationForm1.getCandidate().equals(user.get()))
              .map(ApplicationForm::getSubmittedAt)
              .findFirst()
              .orElse(null);
      var response = GetJobResponse.builder()
              .jobId(job.getId())
              .title(job.getTitle())
              .companyId(job.getCompany().getId())
              .companyName(job.getCompany().getName())
              .address(job.getCompany().getAddress())
              .skills(job.getSkills().stream().map(Skill::getTitle).toList())
              .description(job.getDescription())
              .createdDate(job.getCreatedAt())
              .expiredDate(job.getExpireAt())
              .requirements(job.getRequirements())
              .jobType(job.getJobType().getJobType())
              .location(job.getLocation().getCityName())
              .minSalary(job.getMinSalary())
              .maxSalary(job.getMaxSalary())
              .isSaved(isSaved)
              .isApplied(isApplied)
              .appliedAt(submittedAt)
              .build();
      return response;
    }
    return createGetJobResponse(job, false, false);

  }

  @Override
  public MessageResponse viewJob(Integer id) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
    }
    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("Không tìm thấy người dùng");

    }
    var job = jobRepository.findById(id);
    if (job.isEmpty()) {
      throw new NotFoundException("Không tìm thấy công việc");
    } else {

      var viewJob = new ViewJobs();
      viewJob.setCandidate(user.get());
      viewJob.setJob(job.get());
      viewJob.setViewAt(LocalDateTime.now());
      viewJob.setIsViewed(true);
      if(job.get().getViewCounts() == null) {
        job.get().setViewCounts(0);
      }
      job.get().setViewCounts(job.get().getViewCounts() + 1);
      jobRepository.save(job.get());
      viewJobRepository.save(viewJob);

    }
    return new MessageResponse("Xem công việc thành công", HttpStatus.OK);
  }

  @Override
  public ViewJobResponse getViewAtJob(int page, int size, String sort) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
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
      request = PageRequest.of(page, size, Sort.by(Sort.Order.desc("viewAt")));
    }

    if (viewJob.isEmpty()) {
      throw new NotFoundException("Bạn chưa xem công việc nào");
    } else {
      List<GetJobResponse> getJobResponses = viewJob.getContent().stream()
              .map(viewJobResponse -> {
                Job job = viewJobResponse.getJob();

                return createGetJobResponseWithUserDetails(job,user.get());
              })
              .collect(Collectors.toList());
      Page<GetJobResponse> getJobResponsesPage = new PageImpl<>(getJobResponses, viewJob.getPageable(), viewJob.getTotalElements());
      return new ViewJobResponse(getJobResponsesPage);
    }
  }


  private GetJobResponse createGetJobResponse(Job job, boolean isSaved, boolean isApplied) {

    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle)
            .toList();
    return GetJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .companyId(job.getCompany().getId())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .minSalary(job.getMinSalary())
            .maxSalary(job.getMaxSalary())
            .isSaved(isSaved)
            .isApplied(isApplied)
            .appliedAt(null)
            .build();
  }
  private GetJobResponse createGetJobResponseWithLevel(Job job, String level) {
    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle)
            .toList();
    return GetJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .companyId(job.getCompany().getId())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .minSalary(job.getMinSalary())
            .maxSalary(job.getMaxSalary())
            .level(level)
            .isSaved(false)
            .isApplied(false)
            .appliedAt(null)
            .level(level)
            .build();
  }
  private String determineJobLevel(Job job) {
    final int SUPER_HOT_VIEW_THRESHOLD = 1000;
    final int SUPER_HOT_APPLICATION_THRESHOLD = 1000;
    final int HOT_VIEW_THRESHOLD = 500;
    if (job.getViewCounts() == null) {
      job.setViewCounts(0);

    }
    if (job.getApplyCounts() == null) {
      job.setApplyCounts(0);
    }
    if (job.getViewCounts() >= SUPER_HOT_VIEW_THRESHOLD && job.getApplyCounts() >= SUPER_HOT_APPLICATION_THRESHOLD) {
      return JobLevel.SUPER_HOT.toString();

    }

    if (job.getViewCounts() >= HOT_VIEW_THRESHOLD) {
      return JobLevel.HOT.toString();
    }
    jobRepository.save(job);
    return JobLevel.NORMAL.toString();
  }
  private GetJobResponse createGetJobResponseWithUserDetails(Job job, User currentUser) {
    boolean isSaved = saveJobsRepository.findByCandidateAndJob(currentUser, job) != null;
    boolean isApplied = applyJobRepository.findByCandidateAndJob(currentUser, job) != null;
    String level = determineJobLevel(job);
    LocalDate appliedAt = job.getApplicationForms().stream()
            .filter(applicationForm -> applicationForm.getCandidate().equals(currentUser))
            .map(ApplicationForm::getSubmittedAt)
            .findFirst()
            .orElse(null);
    return GetJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .companyId(job.getCompany().getId())
            .address(job.getCompany().getAddress())
            .skills(job.getSkills().stream().map(Skill::getTitle).toList())
            .description(job.getDescription())
            .createdDate(job.getCreatedAt())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .minSalary(job.getMinSalary())
            .maxSalary(job.getMaxSalary())
            .isSaved(isSaved)
            .isApplied(isApplied)
            .appliedAt(appliedAt)
            .level(level)
            .build();
  }





}
