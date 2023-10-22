package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recruiter_candidate_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterCandidateMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "recruiter_candidate_conversation_id",nullable = false,unique = true)
  private RecruiterCandidateConversations recruiterCandidateConversation;

  @Column(name = "message_content", length = 255)
  private String messageContent;

  @ManyToOne
  @JoinColumn(name = "sender_id",nullable = false,unique = true)
  private User sender;

  @Column(name = "created_by")
  private Date createdBy;

}
