package com.evsmartai.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/stations")
public class ChargingStationController {
    private static final Logger log = LoggerFactory.getLogger(ChargingStationController.class);
    private static final String[] SERVERS = {
        "https://overpass-api.de/api/interpreter",
        "https://maps.mail.ru/osm/tools/overpass/api/interpreter",
        "https://overpass.private.coffee/api/interpreter"
    };
    // Overpass operators ask clients to identify themselves; the default Java agent is often rejected.
    private static final String USER_AGENT = "EVSmartAI/1.0 (+https://github.com/rithwikreddy0111-hub/Ev-Smart-Ai)";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public ChargingStationController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(7000);
        factory.setReadTimeout(30000);
        this.restTemplate = new RestTemplate(factory);
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> nearby(@RequestParam double lat, @RequestParam double lon) {
        if (!Double.isFinite(lat) || !Double.isFinite(lon) || Math.abs(lat) > 90 || Math.abs(lon) > 180) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid latitude or longitude."));
        }

        // Fixed-point formatting: plain string concatenation can emit "1.0E-5", which Overpass rejects.
        String query = String.format(Locale.ROOT,
            "[out:json][timeout:25];nwr[\"amenity\"=\"charging_station\"](around:50000,%.6f,%.6f);out center tags;", lat, lon);

        for (String server : SERVERS) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
                MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
                form.add("data", query);

                ResponseEntity<String> response = restTemplate.postForEntity(server, new HttpEntity<>(form, headers), String.class);
                JsonNode elements = objectMapper.readTree(response.getBody()).path("elements");
                if (!elements.isArray()) {
                    throw new IllegalStateException("Charging station provider returned an unexpected response.");
                }

                List<Map<String, Object>> rows = new ArrayList<>();
                Set<String> seen = new HashSet<>();
                for (JsonNode item : elements) {
                    double itemLat = item.has("lat") ? item.path("lat").asDouble(Double.NaN) : item.path("center").path("lat").asDouble(Double.NaN);
                    double itemLon = item.has("lon") ? item.path("lon").asDouble(Double.NaN) : item.path("center").path("lon").asDouble(Double.NaN);
                    String id = item.path("type").asText("node") + "-" + item.path("id").asText();
                    if (!Double.isFinite(itemLat) || !Double.isFinite(itemLon) || !seen.add(id)) continue;

                    Map<String, Object> tags = objectMapper.convertValue(item.path("tags"), new TypeReference<Map<String, Object>>() {});
                    if (tags == null) tags = new LinkedHashMap<>();
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", id);
                    row.put("name", tags.getOrDefault("name", tags.getOrDefault("operator", "Unnamed mapped charging station")));
                    row.put("operator", tags.getOrDefault("operator", "Operator not listed"));
                    row.put("lat", itemLat);
                    row.put("lon", itemLon);
                    row.put("tags", tags);
                    rows.add(row);
                }
                return ResponseEntity.ok(rows);
            } catch (Exception ex) {
                log.warn("Charging station provider failed: {} ({})", server, ex.toString());
            }
        }

        return ResponseEntity.status(502).body(Map.of(
            "message", "All public charging-station data providers are temporarily unavailable. Please retry in a few minutes."
        ));
    }
}
