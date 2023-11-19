package vn.hcmute.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User candidate;

  @ManyToOne
  @JoinColumn(name = "company_id")
  @JsonIgnore
  private Company company;

  @Column(name = "rating")
  private Integer rating;

  @Column(name = "title")
  private String title;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "created_date")
  private LocalDate createdDate;

  @Column(name="status")
  @Enumerated(EnumType.STRING)
  private CompanyReviewStatus status;


}
