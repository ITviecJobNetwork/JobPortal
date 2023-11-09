package vn.hcmute.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "view_jobs")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewJobs {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "candidate_id")
  @JsonIgnore
  private User candidate;

  @ManyToOne
  @JoinColumn(name = "job_id")
  private Job job;

  @Column(name="view_at")
  private LocalDateTime viewAt;

  @Column(name="is_viewed")
  private Boolean isViewed;


}
