package vn.hcmute.springboot.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.hcmute.springboot.exception.TokenExpiredException;
import vn.hcmute.springboot.exception.UnauthorizedException;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    if (authException instanceof TokenExpiredException) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
      response.getWriter().write("Token expired");
    } else {
      response.setStatus(HttpServletResponse. SC_BAD_REQUEST);
    }
  }
}
