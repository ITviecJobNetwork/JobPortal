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
@Table(name = "candidates_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CandidatesMessages {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "candidate_conversation_id",nullable = false,unique = true)
  private CandidatesConversations candidateConversation;

  @Column(name = "message_content", length = 255)
  private String messageContent;

  @Column(name = "created_at")
  private Date createdAt;

  @ManyToOne
  @JoinColumn(name = "sender_id",nullable = false,unique = true)
  private User sender;
}
