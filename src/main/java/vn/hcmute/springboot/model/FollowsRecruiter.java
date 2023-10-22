package vn.hcmute.springboot.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
@Table(name = "follows_recruiter")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FollowsRecruiter {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Integer id;


  @ManyToOne
  @JoinColumn(name = "recruiter_id",nullable = false,unique = true)
  private Recruiters recruiters;

  @ManyToOne
  @JoinColumn(name = "candidate_id",nullable = false,unique = true)
  private User candidate;

  private Date followAt;

}
