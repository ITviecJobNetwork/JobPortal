package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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

  @ManyToOne
  @JoinColumn(name = "user_id",nullable = false,unique = true)
  private User candidate;

  @Column(name = "major")
  private String major;

  @Column(name = "school")
  private String school;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "start_time")
  private Date startTime;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "end_time")
  private Date endTime;

  @Column(name = "description")
  private String description;

}
