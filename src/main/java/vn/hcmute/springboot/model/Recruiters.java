package vn.hcmute.springboot.model;

import static vn.hcmute.springboot.model.Permission.RECRUITER_CREATE;
import static vn.hcmute.springboot.model.Permission.RECRUITER_DELETE;
import static vn.hcmute.springboot.model.Permission.RECRUITER_READ;
import static vn.hcmute.springboot.model.Permission.RECRUITER_UPDATE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "recruiters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruiters implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "company_name", length = 255)
  private String companyName;

  @Column(name = "nickName", length = 255)
  private String nickname;

  @Column(name = "password", length = 255)
  private String password;

  @Column(name = "phoneNumber", length = 255)
  private String phoneNumber;

  @Column(name = "birthDate")
  private LocalDate birthDate;

  @Column(name = "username", length = 255)
  private String username;

  @Column(name = "location")
  private String location;


  @Column(name = "working_from")
  private String workingFrom;

  @Column(name = "working_to")
  private String workingTo;

  @Column(name = "overtime_policy", length = 255)
  private String overtimePolicy;

  @Column(name = "recruitment_procedure", length = 255)
  private String recruitmentProcedure;

  @Column(name = "benefit", length = 255)
  private String benefit;

  @Column(name = "introduction", length = 255)
  private String introduction;




  @Column(name = "fb_url", length = 255)
  private String fbUrl;

  @Column(name = "website_url", length = 255)
  private String websiteUrl;

  @Column(name = "linkedIn_url", length = 255)
  private String linkedInUrl;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "last_modified_date")
  private LocalDate lastModifiedDate;

  @Column(name = "last_signInTime")
  private LocalDateTime lastSignInTime;


  @OneToOne(mappedBy = "recruiter")
  private Company company;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private RecruiterStatus status;

  @OneToMany(mappedBy = "recruiters",fetch = FetchType.EAGER)
  private List<Token> tokens;

  @Column(name="fullname")
  private String fullname;

  @Column(name="work_title")
  private String workTitle;

  public <E> Recruiters(String username, String password, ArrayList<E> es) {
    this.username = username;
    this.password = password;

  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Set.of(
            RECRUITER_READ,
            RECRUITER_UPDATE,
            RECRUITER_CREATE,
            RECRUITER_DELETE
        ).stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
        .toList();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }


}
