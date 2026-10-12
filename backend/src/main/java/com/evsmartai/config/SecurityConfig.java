package com.evsmartai.config;
import com.evsmartai.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.Arrays;
import java.util.List;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthFilter filter)throws Exception{
  return http.csrf(csrf->csrf.disable()).cors(Customizer.withDefaults()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   // Without this, unauthenticated calls get 403; the frontend needs a real 401 to know the token is missing/expired.
   .exceptionHandling(e->e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
   .authorizeHttpRequests(a->a.requestMatchers("/api/health","/api/auth/**").permitAll().requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().anyRequest().authenticated())
   .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).build();
 }
 /**
  * APP_CORS_ORIGIN accepts one or more comma-separated origins. Wildcards are allowed,
  * e.g. "https://ev-smart-ai.vercel.app,https://ev-smart-*-rithwik9.vercel.app" to also cover Vercel preview URLs.
  * Trailing slashes are stripped because browsers never send them in the Origin header.
  */
 @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origin}") String origins){
  List<String> patterns=Arrays.stream(origins.split(",")).map(String::trim).filter(s->!s.isEmpty()).map(s->s.replaceAll("/+$","")).toList();
  CorsConfiguration c=new CorsConfiguration();c.setAllowedOriginPatterns(patterns);c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type"));c.setAllowCredentials(false);c.setMaxAge(3600L);
  UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;
 }
}
