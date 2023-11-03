package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favourite_job_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteJobType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "candidate_id",nullable = false,unique = true)
  private User candidate;

  @Column(name = "min_salary", length = 255)
  private Double minSalary;

  @Column(name="max_salary",length = 255)
  private Double maxSalary;

  @Column(name="current_salary",length = 255)
  private Double currentSalary;

  @Column(name = "locations", length = 255)
  private String locations;

  @Column(name = "experience")
  private String experience;


  @ManyToOne
  @JoinColumn(name = "job_type")
  private JobType jobType;

  @ManyToMany
  @JoinTable(
      name = "favourite_job_type_job_type_skills",
      joinColumns = @JoinColumn(name = "favourite_job_type_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_id")
  )
  private List<Skill> jobTypeSkills;

  @ManyToMany
  @JoinTable(
      name = "favourite_job_type_job_type",
      joinColumns = @JoinColumn(name = "favourite_job_type_id"),
      inverseJoinColumns = @JoinColumn(name = "job_type_id")
  )
  private List<JobType> jobTypes;

  @Column(name="company_size")
  private String companySize;

  @ManyToMany
  @JoinTable(
      name = "favourite_job_type_company_type",
      joinColumns = @JoinColumn(name = "favourite_job_type_id"),
      inverseJoinColumns = @JoinColumn(name = "company_type_id")
  )
  private List<CompanyType> companyTypes;


}
