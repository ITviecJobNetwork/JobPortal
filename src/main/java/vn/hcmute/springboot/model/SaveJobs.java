package vn.hcmute.springboot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "save_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveJobs {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "candidate_id")
  private User candidate;

  @ManyToOne
  @JoinColumn(name = "job_id")
  private Job job;

  @Column(name="save_at")
  private LocalDateTime saveAt;

  @Column(name="is_saved")
  private Boolean isSaved;


}
