package com.evsmartai.web;
import com.evsmartai.model.AppUser;
import com.evsmartai.repo.UserRepository;
import com.evsmartai.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
@CrossOrigin(
    origins = "https://ev-smart-qzy9popyj-rithwik9.vercel.app/",
    allowCredentials = "true"
)
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
 public AuthController(UserRepository users,PasswordEncoder encoder,JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
 public record RegisterRequest(@NotBlank @Size(min=2,max=100) String name,@NotBlank @Email @Size(max=190) String email,@NotBlank @Size(min=8,max=72) String password){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 @PostMapping("/register") public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r){
  String email=r.email().trim().toLowerCase();if(users.existsByEmail(email))return ResponseEntity.status(409).body(Map.of("message","An account with this email already exists"));
  AppUser u=new AppUser();u.setName(r.name().trim());u.setEmail(email);u.setPasswordHash(encoder.encode(r.password()));users.save(u);
  return ResponseEntity.status(201).body(Map.of("token",jwt.generate(email),"name",u.getName(),"email",u.getEmail()));
 }
 @PostMapping("/login") public ResponseEntity<?> login(@Valid @RequestBody LoginRequest r){
  var u=users.findByEmail(r.email().trim().toLowerCase()).orElse(null);
  if(u==null||!encoder.matches(r.password(),u.getPasswordHash()))return ResponseEntity.status(401).body(Map.of("message","Invalid email or password"));
  return ResponseEntity.ok(Map.of("token",jwt.generate(u.getEmail()),"name",u.getName(),"email",u.getEmail()));
 }
}
