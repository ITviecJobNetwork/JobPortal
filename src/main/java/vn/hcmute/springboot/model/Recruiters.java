package vn.hcmute.springboot.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
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

  @Column(name = "company_name")
  private String companyName;
  private String username;
  private String password;
  @Column(name = "phone_number")
  private String phoneNumber;
  @Column(name = "birth_date")
  private Timestamp birthDate;
  private String email;
  @Column(name = "location_id")
  private String locationId;

  @Column(name = "company_type_id")
  private Integer companyTypeId;
  private String country;

  @Column(name = "company_size")
  private Integer companySize;

  @Column(name = "working_days")
  private Timestamp workingDays;

  @Column(name = "overtime_policy")
  private String overtimePolicy;

  @Column(name = "recruitment_procedure")
  private String recruitmentProcedure;
  private String benefit;
  private String introduction;

  @Column(name = "key_skills_id")
  private String keySkillsId;
  @Column(name = "fb_url")
  private String fbUrl;
  @Column(name = "website_url")
  private String websiteUrl;
  @Column(name = "linkedin_url")
  private String linkedInUrl;

  @Enumerated(EnumType.STRING)
  private Role role;
}
