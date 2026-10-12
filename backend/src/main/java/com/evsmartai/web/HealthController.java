package com.evsmartai.web;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api")
@CrossOrigin(
    origins = "https://ev-smart-qzy9popyj-rithwik9.vercel.app/",
    allowCredentials = "true"
)
public class HealthController {
 @GetMapping("/health") public Map<String,Object> health(){return Map.of("status","UP","service","EV Smart AI backend");}
}
