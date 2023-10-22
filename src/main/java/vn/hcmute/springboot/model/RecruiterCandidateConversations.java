package vn.hcmute.springboot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recruiter_candidate_conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterCandidateConversations {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "recruiter_id")
  private Recruiters recruiter;

  @ManyToOne
  @JoinColumn(name = "candidate_id")
  private User candidate;


}
