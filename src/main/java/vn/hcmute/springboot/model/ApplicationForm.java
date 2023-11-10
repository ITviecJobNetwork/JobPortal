package vn.hcmute.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "application_form")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationForm {

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

  @Column(name = "candidate_name", length = 255)
  private String candidateName;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "link_CV", length = 255)
  private String linkCV;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 255)
  private ApplicationStatus status;

  @Column(name = "attachment", length = 255)
  private String attachment;


  @Column(name = "submitted_at")
  private LocalDate submittedAt;

  @Column(name = "cover_letter")
  private String coverLetter;

  @Column(name = "is_applied")
  private Boolean isApplied;
}
