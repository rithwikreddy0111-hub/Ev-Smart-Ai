package com.evsmartai;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class EvSmartAiApplication {
 public static void main(String[] args) {
  // Load a local .env for development, but never let it override real environment variables
  // (system properties outrank OS env vars in Spring, so a stray .env would silently win on a server).
  Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
  dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE).forEach(e -> {
   if (System.getenv(e.getKey()) == null && System.getProperty(e.getKey()) == null) System.setProperty(e.getKey(), e.getValue());
  });
  SpringApplication.run(EvSmartAiApplication.class,args); }
}
