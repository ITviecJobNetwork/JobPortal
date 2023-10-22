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
@Table(name = "candidates_conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidatesConversations {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "candidates_id_1",nullable = false,unique = true)
  private User candidate1;

  @ManyToOne
  @JoinColumn(name = "candidates_id_2",nullable = false,unique = true)
  private User candidate2;


}
