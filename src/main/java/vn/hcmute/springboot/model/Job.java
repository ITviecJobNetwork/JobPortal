package vn.hcmute.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "title", length = 255)
  private String title;

  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;

  @ManyToOne
  @JoinColumn(name = "job_type_id")
  private JobType jobType;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;

  @Column(name = "min_salary")
  private Double minSalary;


  @Column(name = "max_salary")
  private Double maxSalary;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "job_skills",
      joinColumns = @JoinColumn(name = "jobs_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_id")
  )
  private Set<Skill> skills = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "job_candidate_level",
      joinColumns = @JoinColumn(name = "job_id"),
      inverseJoinColumns = @JoinColumn(name = "candidate_level_id")
  )
  private Set<CandidateLevel> candidateLevels = new HashSet<>();



  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "requirements", length = 255)
  private String requirements;

  @Column(name = "created_by", length = 255)
  private String createdBy;

  @Column(name = "created_at")
  private Date createdAt;

  @Column(name = "expire_at")
  private Date expireAt;

  @JsonIgnore
  @OneToMany(mappedBy = "job",fetch = FetchType.EAGER)
  private List<ApplicationForm> applicationForms;

}
