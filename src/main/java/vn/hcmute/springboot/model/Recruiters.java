package vn.hcmute.springboot.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recruiters")
@Getter
@Setter
@NoArgsConstructor
public class Recruiters {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "company_name", nullable = false, unique = true)
  private String companyName;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "first_name", nullable = false)
  private String firstname;

  @Column(name = "last_name", nullable = false)
  private String lastname;

  @Column(name = "birth_date")
  private Date birthDate;

  @Column(name = "email", nullable = false, unique = true)
  @Email
  private String email;

  @Column(name = "location_id", nullable = false, unique = true)
  private UUID locationId;

  @Column(name = "company_type_id", nullable = false, unique = true)
  private UUID companyTypeId;

  @Column(name = "country", nullable = false)
  private String country;

  @Column(name = "company_size")
  private Integer companySize;

  @Column(name = "working_days", nullable = false)
  private Date workingDays;

  @Column(name = "overtime_policy")
  private String overtimePolicy;

  @Column(name = "recruitment_procedure")
  private String recruitmentProcedure;

  @Column(name = "benefit", nullable = false)
  private String benefit;

  @Column(name = "introduction", nullable = false)
  private String introduction;

  @Column(name = "key_skills_id", unique = true)
  private UUID keySkillsId;

  @Column(name = "fb_url")
  private String fbUrl;

  @Column(name = "website_url")
  private String websiteUrl;

  @Column(name = "linkedin_url")
  private String linkedInUrl;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(name = "created_date")
  private Date createdDate;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "last_modified_date")
  private Date lastModifiedDate;

  @Column(name = "last_modified_by")
  private String lastModifiedBy;

  @Column(name="last_sigin_time")
  private Date lastSignInTime;
}
