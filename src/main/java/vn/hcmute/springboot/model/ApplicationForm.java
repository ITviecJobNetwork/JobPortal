package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.util.Date;
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
  @JoinColumn(name = "candidate_id",nullable = false,unique = true)
  private User candidate;

  @ManyToOne
  @JoinColumn(name = "job_id",nullable = false,unique = true)
  private Job job;

  @Column(name = "candidate_name", length = 255)
  private String candidateName;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "link_CV", length = 255)
  private String linkCV;

  @Column(name = "status", length = 255)
  private String status;

  @Column(name = "attachment", length = 255)
  private String attachment;

  @Column(name = "submitted_at")
  private Date submittedAt;

}
