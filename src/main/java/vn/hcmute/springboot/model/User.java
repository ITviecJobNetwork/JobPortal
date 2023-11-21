package vn.hcmute.springboot.model;


import static vn.hcmute.springboot.model.Permission.ADMIN_CREATE;
import static vn.hcmute.springboot.model.Permission.ADMIN_DELETE;
import static vn.hcmute.springboot.model.Permission.ADMIN_READ;
import static vn.hcmute.springboot.model.Permission.ADMIN_UPDATE;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_CREATE;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_DELETE;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_READ;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_UPDATE;
import static vn.hcmute.springboot.model.Permission.RECRUITER_CREATE;
import static vn.hcmute.springboot.model.Permission.RECRUITER_DELETE;
import static vn.hcmute.springboot.model.Permission.RECRUITER_READ;
import static vn.hcmute.springboot.model.Permission.RECRUITER_UPDATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Table(name = "users")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class  User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column( name = "first_name")
  private String firstname;

  @Column( name = "last_name")
  private String lastname;

  @Column(name = "nickname",unique = true)
  private String nickname;

  @Column(name = "password")
  private String password;

  @Enumerated(EnumType.STRING)
  @Column( name = "gender")
  private Gender gender;

  @Column(name = "phone_number", unique = true)
  private String phoneNumber;

  @DateTimeFormat(pattern = "dd/MM/yyyy")
  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name="location")
  private String location;

  @Column(name = "username", unique = true)
  @Email
  private String username;

  @Column(name = "about_me")
  private String aboutMe;

  @Column(name = "link_website_profile")
  private String linkWebsiteProfile;

  @Column(name = "city")
  private String city;

  @Column(name = "avatar")
  private String avatar;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "position")
  private String position;

  @Column(name = "address")
  private String address;


  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private UserStatus status = UserStatus.INACTIVE;

  @Column(name = "cover_letter")
  private String coverLetter;

  @Column(name = "link_cv")
  private String linkCV;

  @Column(name="otp")
  private String otp;

  @Column(name = "otp_generated_time")
  private LocalDateTime otpGeneratedTime;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(name = "created_date")
  private Date createdDate;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "last_modified_date")
  private Date lastModifiedDate;

  @Column(name = "last_modified_by")
  private String lastModifiedBy;

  @Column(name="last_sigin_time")
  private LocalDateTime lastSignInTime;

  @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
  private List<Token> tokens;



  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "user_skill",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "skill_id")
  )
  private List<CandidateSkill> skills;

  @ManyToOne(fetch = FetchType.EAGER)
  private CandidateEducation education;

  @ManyToMany (fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_experience",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "experience_id")
  )
  private List<CandidateExperience> experiences;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy
        ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
        : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer()
        .getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    User user = (User) o;
    return getId() != null && Objects.equals(getId(), user.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
        .getPersistentClass().hashCode() : getClass().hashCode();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if(role.name().equals("ADMIN")){
      return Set.of(
          ADMIN_READ,
          ADMIN_UPDATE,
          ADMIN_DELETE,
          ADMIN_CREATE,
          RECRUITER_READ,
          RECRUITER_UPDATE,
          RECRUITER_CREATE,
          RECRUITER_DELETE,
          CANDIDATE_READ,
          CANDIDATE_UPDATE,
          CANDIDATE_CREATE,
          CANDIDATE_DELETE
      ).stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
          .toList();
    }
    if (role.name().equals("RECRUITER")){
      return Set.of(
          RECRUITER_READ,
          RECRUITER_UPDATE,
          RECRUITER_CREATE,
          RECRUITER_DELETE
      ).stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
          .toList();
    }
    if (role.name().equals("USER")){
      return Set.of(
          CANDIDATE_READ,
          CANDIDATE_UPDATE,
          CANDIDATE_CREATE,
          CANDIDATE_DELETE
      ).stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
          .toList();
    }
    return Collections.emptyList();
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
