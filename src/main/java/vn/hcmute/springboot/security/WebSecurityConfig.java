package vn.hcmute.springboot.security;


import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

  private final UserDetailsServiceImpl userDetailsService;
  private final JwtAuthEntryPoint unauthorizedHandler;
  private final WebConfigProperties webConfigProperties;

  @Bean
  public JwtAuthTokenFilter authenticationJwtTokenFilter() {
    return new JwtAuthTokenFilter();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests((authorize) -> authorize
            // static resources
            .requestMatchers(
                new AntPathRequestMatcher("/"),
                new AntPathRequestMatcher("/**/*.html"),
                new AntPathRequestMatcher("/**/*.{png,jpg,jpeg,svg.ico}"),
                new AntPathRequestMatcher("/**/*.css"),
                new AntPathRequestMatcher("/**/*.js")
            ).permitAll()
            // swagger
            .requestMatchers(
                new AntPathRequestMatcher("/swagger-resources/**"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/webjars/**"),
                new AntPathRequestMatcher("/configuration/**")
            ).permitAll()
            // others
            .requestMatchers(
                new AntPathRequestMatcher("/api/auth/**"),
                new AntPathRequestMatcher("/api/test/**")
            ).permitAll()
            .anyRequest().authenticated()
        );

    httpSecurity.addFilterBefore(authenticationJwtTokenFilter(),
        UsernamePasswordAuthenticationFilter.class);
    httpSecurity.cors(Customizer.withDefaults());

    return httpSecurity.build();
  }

  @Bean
  public WebMvcConfigurer corsMappingConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(@NotNull CorsRegistry registry) {
        var cors = webConfigProperties.getCors();
        registry.addMapping("/**")
            .allowedOrigins(cors.getAllowedOrigins())
            .allowedMethods(cors.getAllowedMethods())
            .maxAge(cors.getMaxAge())
            .allowedHeaders(cors.getAllowedHeaders())
            .exposedHeaders(cors.getExposedHeaders());
      }
    };
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    var authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration)
      throws Exception {
    return authConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public GrantedAuthorityDefaults grantedAuthorityDefaults() {
    // remove the ROLE_ prefix
    return new GrantedAuthorityDefaults("");
  }

}
