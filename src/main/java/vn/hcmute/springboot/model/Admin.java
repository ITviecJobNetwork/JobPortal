package vn.hcmute.springboot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static vn.hcmute.springboot.model.Permission.*;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_DELETE;

@Entity
@Table(name = "admin")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "fullname",unique = true)
  private String fullName;

  @Column(name = "password")
  private String password;

  @Enumerated(EnumType.STRING)
  @Column( name = "gender")
  private Gender gender;

  @Column(name = "phone_number", unique = true)
  private String phoneNumber;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name="location")
  private String location;

  @Column(name = "email", unique = true)
  @Email
  private String email;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "admin",fetch = FetchType.EAGER)
  private List<Token> tokens;

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

    return Collections.emptyList();
  }

  @Override
  public String getUsername() {
    return email;
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
