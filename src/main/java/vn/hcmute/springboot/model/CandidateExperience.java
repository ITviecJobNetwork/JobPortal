package vn.hcmute.springboot.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
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
  private String startTime;


  @Column(name = "end_time")
  private String endTime;

  @Column(name = "description")
  private String description;

}
