package org.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

  private String secretKey = "my-super-secure-secret-key-should-be-very-long";

  private final long validityInMilliseconds = 1000 * 60 * 60 * 2; // 2시간

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  // 토큰 생성
  public String createToken(String username, List<String> roles) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put("roles", roles);

    Date now = new Date();
    Date expiration = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiration)
        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰에서 username 추출
  public String getUsername(String token) {
    return Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  // 토큰에서 role 추출
  public List<String> getRoles(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build()
        .parseClaimsJws(token).getBody();
    Object roles = claims.get("roles");
    if (roles instanceof List<?>) {
      return ((List<?>) roles).stream()
          .map(Object::toString)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
