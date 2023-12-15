package vn.hcmute.springboot.security;


import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final LogoutHandler logoutHandler;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(AbstractHttpConfigurer::disable)
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
                            new AntPathRequestMatcher("/api/users/**"),
                            new AntPathRequestMatcher("/api/job/**"),
                            new AntPathRequestMatcher("/api/company/**"),
                            new AntPathRequestMatcher("/api/profile/**")
                    ).permitAll()
                    .requestMatchers(
                            new AntPathRequestMatcher("/api/recruiter/**")
                    ).permitAll()
                    .requestMatchers(
                            new AntPathRequestMatcher("/api/admin/**")
                    ).permitAll()

                    .anyRequest().authenticated()
            );
    httpSecurity.csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);



    return httpSecurity.build();
  }

  @Bean
  public WebMvcConfigurer corsMappingConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD")
                .maxAge(3600)
                .allowedHeaders("*")
                .exposedHeaders("*");
      }
    };
  }
}