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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.proxy.HibernateProxy;
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
  private String username;
  private String password;
  private String email;
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  private UserStatus status = UserStatus.INACTIVE;

  @Enumerated(EnumType.STRING)
  private Role role;


  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

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
    if (role.name().equals("CANDIDATE")){
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
