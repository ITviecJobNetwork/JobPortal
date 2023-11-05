package vn.hcmute.springboot.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "address")
  private String address;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "website")
  private String website;

  @Column(name = "industry")
  private String industry;

  @Column(name = "founded_date")
  private Date foundedDate;

  @Column(name = "description")
  private String description;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_date")
  private Date createdDate;

  @Column(name = "last_modified_by")
  private String lastModifiedBy;

  @Column(name = "last_modified_date")
  private Date lastModifiedDate;

  @Column(name="is_followed")
  private Boolean isFollowed;

  @Column(name="is_followed_at")
  private LocalDate isFollowedAt;

  @ManyToOne
  @JoinColumn(name = "user_follow_id")
  @JsonIgnore
  private User user;

  @OneToOne
  @JoinColumn(name = "recruiter_id")
  @JsonIgnore
  private Recruiters recruiter;

}

