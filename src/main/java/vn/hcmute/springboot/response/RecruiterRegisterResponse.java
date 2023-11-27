package vn.hcmute.springboot.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.RecruiterStatus;


@Getter
@Setter
public class RecruiterRegisterResponse {
  private Integer recruiterId;
  private String fullName;
  private String workTitle;
  private String hearAboutUs;
  private String username;
  private String phoneNumber;
  private String companyName;
  private String companyLocation;
  private String websiteUrl;
  private RecruiterStatus status;
}
