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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comments {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "content", length = 255)
  private String content;


  @Column(name = "parent_comment_id")
  private Integer parentCommentId;

  @Column(name = "status", length = 255)
  private String status;

  @ManyToOne
  @JoinColumn(name = "candidate_id",nullable = false,unique = true)
  private User candidate;

  @ManyToOne
  @JoinColumn(name = "recruiter_id",nullable = false,unique = true)
  private Recruiters recruiter;

  @Column(name = "count_likes")
  private Integer countLikes;

  @Column(name = "count_dislikes")
  private Integer countDislikes;

  @Column(name = "created_at")
  private Date createdAt;

}

