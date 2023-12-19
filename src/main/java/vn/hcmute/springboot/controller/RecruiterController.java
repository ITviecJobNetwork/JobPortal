package vn.hcmute.springboot.controller;


import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.*;
import vn.hcmute.springboot.service.RecruiterService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {
  private final RecruiterService recruiterService;

  @PostMapping("/register")
  public ResponseEntity<MessageResponse> register(@RequestBody RecruiterRegisterRequest request) {
    var recruiterRegister = recruiterService.registerRecruiter(request);

    return new ResponseEntity<>(recruiterRegister, HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
    var userLogin = recruiterService.loginRecruiter(loginRequest);
    return new ResponseEntity<>(userLogin, HttpStatus.OK);
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    recruiterService.refreshToken(request, response);
  }

  @PostMapping("/change-password")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ChangePasswordRequest request) {
    var resetPassword = recruiterService.changePassword(request.getCurrentPassword(),
            request.getNewPassword(), request.getConfirmPassword());

    return ResponseEntity.ok(resetPassword);

  }

  @PostMapping("/change-nickname")
  public ResponseEntity<MessageResponse> changeNickname(
          @RequestBody ChangeNickNameRequest request) {

    var changeNickname = recruiterService.changeNickName(request.getNewNickName());
    return new ResponseEntity<>(changeNickname, HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  @Valid
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
    var resetPassword = recruiterService.resetPassword(request.getEmail(), request.getCurrentPassword(),
            request.getNewPassword(), request.getConfirmPassword());
    return new ResponseEntity<>(resetPassword, HttpStatus.OK);
  }


  @PostMapping("/forgot-password")
  public ResponseEntity<MessageResponse> forgotPassword(
          @Valid @RequestBody ForgotPasswordRequest request) {
    var forgotPassword = recruiterService.sendNewPasswordToEmail(request.getEmail());
    return new ResponseEntity<>(forgotPassword, HttpStatus.OK);
  }

  @PostMapping("/update-profile")
  public ResponseEntity<MessageResponse> updateRecruiterProfile(@Valid @RequestBody
                                                                UpdateProfileRecruiterRequest request) {
    var updateProfile = recruiterService.updateProfile(request);
    return new ResponseEntity<>(updateProfile, HttpStatus.OK);

  }

  @GetMapping("/get-profile")
  public ResponseEntity<RecruiterProfileResponse> getProfile() {
    var recruiter = recruiterService.getProfile();
    return new ResponseEntity<>(recruiter, HttpStatus.OK);
  }


  @PostMapping(value = "/create-company")
  public ResponseEntity<MessageResponse> createCompany(@Valid @RequestBody PostInfoCompanyRequest request) throws IOException {
    var company = recruiterService.createCompany(request);
    return new ResponseEntity<>(company, HttpStatus.OK);
  }

  @PostMapping(value = "/update-company")
  public ResponseEntity<MessageResponse> updateCompany(@Valid @RequestBody UpdateInfoCompanyRequest request) throws IOException {

    var updateCompany = recruiterService.updateCompany(request);
    return new ResponseEntity<>(updateCompany, HttpStatus.OK);
  }

  @DeleteMapping("/delete-company")
  public ResponseEntity<MessageResponse> deleteCompany() {
    var deleteCompany = recruiterService.deleteCompany();
    return new ResponseEntity<>(deleteCompany, HttpStatus.OK);

  }

  @PostMapping(value = "/post-job")
  public ResponseEntity<MessageResponse> postJob(@Valid @RequestBody PostJobRequest request) throws IOException {
    var job = recruiterService.postJob(request);
    return new ResponseEntity<>(job, HttpStatus.OK);
  }

  @PostMapping(value = "/update-job")
  public ResponseEntity<MessageResponse> updateJob(@RequestParam("id") Integer jobId, @Valid @RequestBody UpdateJobRequest request) throws IOException {
    var updateJob = recruiterService.updateJob(jobId, request);
    return new ResponseEntity<>(updateJob, HttpStatus.OK);
  }

  @DeleteMapping("/delete-job")
  public ResponseEntity<MessageResponse> deleteJob(@RequestParam("id") Integer jobId) throws IOException {
    var deleteJob = recruiterService.deleteJob(jobId);
    return new ResponseEntity<>(deleteJob, HttpStatus.OK);
  }

  @GetMapping("/getJob/{jobId}")
  public ResponseEntity<GetJobResponse> getJobById(@PathVariable Integer jobId) {
    var getJobById = recruiterService.getJobById(jobId);
    return new ResponseEntity<>(getJobById, HttpStatus.OK);
  }


  @GetMapping("get-application-form")
  public ResponseEntity<Page<ApplicationFormResponse>> getAppliedJob(@RequestParam(value = "page" ,defaultValue = "0") Integer page,
                                                                     @RequestParam(value = "size", defaultValue = "20") Integer size,
                                                                     @RequestParam(value = "type", required = false) String type) {
    PageRequest pageRequest = PageRequest.of(page, size);
    var getAppliedJob = recruiterService.getAppliedJob(pageRequest,type);
    return new ResponseEntity<>(getAppliedJob, HttpStatus.OK);
  }


  @GetMapping("/getApplicationById/{applicationId}")
  public ResponseEntity<ApplicationFormResponse> getApplicationById(@PathVariable Integer applicationId) {
    var getApplicationById = recruiterService.getApplicationById(applicationId);
    return new ResponseEntity<>(getApplicationById, HttpStatus.OK);
  }

  @PostMapping("/updateApplication/{applicationId}")
  public ResponseEntity<MessageResponse> updateApplication(@PathVariable Integer applicationId, @RequestBody UpdateApplicationRequest updateRequest) throws MessagingException {
    var updateApplication = recruiterService.updateStatusJob(applicationId, updateRequest);
    return new ResponseEntity<>(updateApplication, HttpStatus.OK);
  }
  @PostMapping("/addCompanyKeySkill")
  public ResponseEntity<MessageResponse> addCompanyKeySkill(@RequestBody AddCompanyKeySkillRequest request) {
    var addCompanyKeySkill = recruiterService.addCompanyKeySkill(request);
    return new ResponseEntity<>(addCompanyKeySkill, HttpStatus.OK);
  }

  @GetMapping("/list-all-job")
  public ResponseEntity<List<GetJobResponse>> listAllJobResponse() {
    var listAllJobResponse = recruiterService.listAllJobResponse();
    return new ResponseEntity<>(listAllJobResponse, HttpStatus.OK);
  }

  @GetMapping("/get-company")
  public ResponseEntity<CompanyResponse> getCompanyByRecruiter() {
    var company = recruiterService.getCompanyByRecruiter();
    return new ResponseEntity<>(company, HttpStatus.OK);
  }


}
