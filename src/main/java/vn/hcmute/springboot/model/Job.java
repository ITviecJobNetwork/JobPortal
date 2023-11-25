package vn.hcmute.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
  private LocalDateTime createdAt;

  @Column(name = "expire_at")
  private LocalDate expireAt;

  @JsonIgnore
  @OneToMany(mappedBy = "job", fetch = FetchType.EAGER)
  private List<ApplicationForm> applicationForms;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private JobStatus status;


  @Column(name="view_counts")
  private Integer viewCounts=0;

  @Column(name="apply_counts")
  private Integer applyCounts=0;

}
