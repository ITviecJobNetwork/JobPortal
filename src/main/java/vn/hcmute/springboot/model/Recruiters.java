package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recruiters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recruiters {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "company_name", length = 255)
  private String companyName;

  @Column(name = "username", length = 255)
  private String username;

  @Column(name = "password", length = 255)
  private String password;

  @Column(name = "phoneNumber", length = 255)
  private String phoneNumber;
  @Column(name = "birthDate")
  private Date birthDate;

  @Column(name = "email", length = 255)
  private String email;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;


  @ManyToOne
  @JoinColumn(name = "company_type_id")
  private CompanyType companyType;

  @Column(name = "country", length = 255)
  private String country;

  @Column(name = "company_size")
  private Integer companySize;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "working_days")
  private Date workingDays;

  @Column(name = "overtime_policy", length = 255)
  private String overtimePolicy;

  @Column(name = "recruitment_procedure", length = 255)
  private String recruitmentProcedure;

  @Column(name = "benefit", length = 255)
  private String benefit;

  @Column(name = "introduction", length = 255)
  private String introduction;

  @Column(name = "key_skills_id", length = 255)
  private String keySkillsId;

  @Column(name = "fb_url", length = 255)
  private String fbUrl;

  @Column(name = "website_url", length = 255)
  private String websiteUrl;

  @Column(name = "linkedIn_url", length = 255)
  private String linkedInUrl;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_date")
  private Date createdDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_modified_date")
  private Date lastModifiedDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_signInTime")
  private Date lastSignInTime;


  @OneToOne(mappedBy = "recruiter")
  private Company company;
}
