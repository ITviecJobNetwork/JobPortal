package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  private User candidate;



  @ManyToOne
  @JoinColumn(name = "job_id",unique = true)
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

  @Column(name="cover_letter")
  private String coverLetter;

}
