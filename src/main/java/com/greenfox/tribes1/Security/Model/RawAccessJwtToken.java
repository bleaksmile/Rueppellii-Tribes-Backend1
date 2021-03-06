package com.greenfox.tribes1.Security.Model;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;

public class RawAccessJwtToken implements JwtToken {
  private String token;

  public RawAccessJwtToken(String token) {
    this.token = token;
  }

  public Jws<Claims> parseClaims(String signingKey) {

    try {
      return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
    } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
      throw new BadCredentialsException("Invalid JWT token: ", ex);
    }
  }

  @Override
  public String getToken() {
    return token;
  }
}
