package vn.hcmute.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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


  public CompanyReview(CompanyReview companyReview) {
    this.id = companyReview.getId();
    this.candidate = companyReview.getCandidate();
    this.company = companyReview.getCompany();
    this.rating = companyReview.getRating();
    this.title = companyReview.getTitle();
    this.content = companyReview.getContent();
    this.createdDate = companyReview.getCreatedDate();
  }
}
