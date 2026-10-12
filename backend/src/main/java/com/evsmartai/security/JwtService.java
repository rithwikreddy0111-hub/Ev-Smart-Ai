package com.evsmartai.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Service
public class JwtService {
 private static final Logger log=LoggerFactory.getLogger(JwtService.class);
 private final SecretKey key; private final long expiration;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms}") long expiration){
  if(secret.getBytes(StandardCharsets.UTF_8).length<32) throw new IllegalArgumentException("JWT_SECRET must be at least 32 bytes");
  if(secret.startsWith("dev_only_change_this")) log.warn("Using the built-in development JWT secret. Anyone can forge tokens with it; set JWT_SECRET to a long random value in production.");
  this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expiration=expiration;
 }
 public String generate(String email){long now=System.currentTimeMillis();return Jwts.builder().subject(email).issuedAt(new Date(now)).expiration(new Date(now+expiration)).signWith(key).compact();}
 public String subject(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
}
