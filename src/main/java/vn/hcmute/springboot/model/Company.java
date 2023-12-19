package vn.hcmute.springboot.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import lombok.*;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "address")
  private String address;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "website")
  private String website;

  @Column(name = "industry")
  private String industry;

  @Column(name = "founded_date")
  private LocalDate foundedDate;

  @Column(name = "description")
  private String description;

  @Column(name = "created_date")
  private LocalDate createdDate;

  @Column(name = "last_modified_by")
  private String lastModifiedBy;

  @Column(name = "last_modified_date")
  private LocalDate lastModifiedDate;

  @OneToOne
  @JoinColumn(name = "recruiter_id")
  @JsonIgnore
  private Recruiters recruiter;


  @Column(name="count_job_opening")
  private Integer countJobOpening;

  @Column(name="logo")
  private String logo;

  @ManyToOne
  @JoinColumn(name = "companyType_id")
  private CompanyType companyType;

  @Column(name="country")
  private String country;

  @Column(name="max_company_size")
  private Integer maxCompanySize;

  @Column(name="min_company_size")
  private Integer minCompanySize;

  @OneToMany(mappedBy = "company", fetch = FetchType.EAGER)
  @JsonIgnore
  private List<CompanyKeySkill> companyKeySkill;
}

