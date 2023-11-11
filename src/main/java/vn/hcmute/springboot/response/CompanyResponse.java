package vn.hcmute.springboot.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
  private Integer companyId;
  private String companyName;
  private String companyLogo;
  private String companyType;
  private String address;
  private String description;
  private String website;
  private String phoneNumber;
  private String industry;
  private LocalDate createdDate;
  private Integer countJobOpenings;
  private Integer companySize;
  private String country;
  private LocalDate foundedDate;
}
