package vn.hcmute.springboot.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "candidate_education")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor




public class CandidateEducation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "major")
  private String major;

  @Column(name = "school")
  private String school;

  @Column(name = "start_time")
  private LocalDate startTime;

  @Column(name = "end_time")
  private LocalDate endTime;

  @OneToMany(mappedBy = "education", cascade = CascadeType.ALL)
  @Column(name="user_id")
  private Set<User> users;
}
