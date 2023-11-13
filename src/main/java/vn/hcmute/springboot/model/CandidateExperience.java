package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidate_experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateExperience {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToMany(mappedBy = "experiences",fetch = FetchType.EAGER)
  private Set<User> users;


  @Column(name = "job_title")
  private String jobTitle;

  @Column(name = "company_name")
  private String companyName;


  @Column(name = "start_time")
  private LocalDate startTime;


  @Column(name = "end_time")
  private LocalDate endTime;

  @Column(name = "description")
  private String description;

}
