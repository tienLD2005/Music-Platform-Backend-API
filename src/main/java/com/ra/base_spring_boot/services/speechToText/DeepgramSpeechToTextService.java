package com.ra.base_spring_boot.services.speechToText;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DeepgramSpeechToTextService implements SpeechToTextService {

    @Value("${deepgram.api.key}")
    private String deepgramApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertAudioToText(String audioUrl) {
        try {
            // Endpoint batch STT (pre-recorded)
            // Sử dụng model=2-general, tier=nova, language=vi như gợi ý từ Deepgram
            final String url = "https://api.deepgram.com/v1/listen"
                    + "?model=2-general"
                    + "&tier=nova"
                    + "&language=vi"
                    + "&smart_format=true";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Token " + deepgramApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("url", audioUrl);

            HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

            log.info("Gửi request tới Deepgram: URL={}, fileUrl={}", url, audioUrl);
            ResponseEntity<String> resp = restTemplate.exchange(
                    url, HttpMethod.POST, req, String.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                log.error("Deepgram non-OK: {}, body: {}", resp.getStatusCode(), resp.getBody());
                throw new IllegalStateException("Lỗi gọi Deepgram API: " + resp.getStatusCode() + " " + resp.getBody());
            }

            JsonNode root = objectMapper.readTree(resp.getBody());

            // Path chính thức: results.channels[0].alternatives[0].transcript
            String transcript = null;
            JsonNode results = root.path("results");
            if (results.isObject()) {
                JsonNode channels = results.path("channels");
                if (channels.isArray() && channels.size() > 0) {
                    JsonNode alternatives = channels.get(0).path("alternatives");
                    if (alternatives.isArray() && alternatives.size() > 0) {
                        transcript = alternatives.get(0).path("transcript").asText(null);
                    }
                }
            }
            if (transcript == null || transcript.isBlank()) {
                // Fallback một số shape response khác (hiếm)
                JsonNode channel = root.path("channel");
                if (channel.isObject()) {
                    JsonNode alts = channel.path("alternatives");
                    if (alts.isArray() && alts.size() > 0) {
                        transcript = alts.get(0).path("transcript").asText(null);
                    }
                }
            }
            if (transcript == null || transcript.isBlank()) {
                log.warn("Deepgram response không có transcript, raw: {}", resp.getBody());
                throw new IllegalStateException("Không tìm thấy transcript trong phản hồi Deepgram");
            }
            log.info("Nhận transcript từ Deepgram: {}", transcript);
            return transcript.trim();

        } catch (RestClientException e) {
            log.error("Lỗi gọi Deepgram API: {}", e.getMessage());
            throw new RuntimeException("Lỗi gọi Deepgram API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Lỗi phân tích kết quả Deepgram: {}", e.getMessage());
            throw new RuntimeException("Lỗi phân tích kết quả Deepgram: " + e.getMessage(), e);
        }
    }
}