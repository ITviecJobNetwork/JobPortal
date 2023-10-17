package vn.hcmute.springboot.security;



import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import vn.hcmute.springboot.exception.UnauthorizedException;


// class to generate jwt token
@Component
public class JwtProvider {

  private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.accessTokenExpirationMs}")
  private int accessTokenExpirationMs;

  @Value("${jwt.refreshTokenExpirationMs}")
  private int refreshTokenExpirationMs;

  public String generateAccessToken(Authentication authentication) {
    var expirationTime = Instant.now().plus(accessTokenExpirationMs, ChronoUnit.MILLIS);
    var expirationDate = Date.from(expirationTime);
    var userPrincipal = (UserPrinciple) authentication.getPrincipal();
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    return Jwts.builder()
        .claim("sub", userPrincipal.getUsername())
        .claim("role", userPrincipal.getRole())
        .claim("authorities", userPrincipal.getAuthorities())
        .claim("type", TokenType.ACCESS_TOKEN)
        .setExpiration(expirationDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(Authentication authentication) {
    var expirationTime = Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS);
    var expirationDate = Date.from(expirationTime);
    var userPrincipal = (UserPrinciple) authentication.getPrincipal();
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    return Jwts.builder()
        .claim("sub", userPrincipal.getUsername())
        .claim("type", TokenType.REFRESH_TOKEN)
        .setExpiration(expirationDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUserNameFromJwtToken(String token) {
    var secretBytes = jwtSecret.getBytes();
    var jwsClaims = Jwts.parserBuilder()
        .setSigningKey(secretBytes)
        .build()
        .parseClaimsJws(token);
    return jwsClaims.getBody()
        .getSubject();
  }

  public TokenType getTokenType(String token) {
    var secretBytes = jwtSecret.getBytes();
    var jwsClaims = Jwts.parserBuilder()
        .setSigningKey(secretBytes)
        .build()
        .parseClaimsJws(token);
    return TokenType.valueOf(jwsClaims.getBody().get("type", String.class));
  }

  public boolean validateJwtToken(String token) {
    try {
      var secretBytes = jwtSecret.getBytes();
      Jwts.parserBuilder().setSigningKey(secretBytes).build().parseClaimsJws(token);
      return true;
    } catch (SignatureException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
      throw new UnauthorizedException("invalid-jwt-signature");
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
      throw new UnauthorizedException("invalid-jwt-token");
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
      throw new UnauthorizedException("jwt-token-is-expired");
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
      throw new UnauthorizedException("jwt-token-is-unsupported");
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
      throw new UnauthorizedException("jwt-claims-string-is-empty");
    }
  }

}
