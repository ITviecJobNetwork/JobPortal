package vn.hcmute.springboot.security;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.hcmute.springboot.model.RoleName;
import vn.hcmute.springboot.model.User;


public class UserPrinciple implements UserDetails {

  @Serial
  private static final long serialVersionUID = 1L;

  @Getter(AccessLevel.PUBLIC)
  private final Integer id;

  private final String username;

  @JsonIgnore
  private final String password;

  @Getter
  private final RoleName role;

  private final Collection<? extends GrantedAuthority> authorities;

  public UserPrinciple(Integer id, String username, String password,
      RoleName role, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
    this.authorities = authorities;
  }

  public static UserPrinciple build(User user) {
    var authorities = user.getPrivileges().stream()
        .map(privilege -> new SimpleGrantedAuthority(privilege.getName().name()))
        .toList();

    return new UserPrinciple(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.getRole().getName(),
        authorities
    );
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var user = (UserPrinciple) o;
    return Objects.equals(id, user.id);
  }

}

