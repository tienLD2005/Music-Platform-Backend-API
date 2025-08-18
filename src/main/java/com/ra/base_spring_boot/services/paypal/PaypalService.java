package com.ra.base_spring_boot.services.paypal;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalService {
    @Value("${paypal.client-id}") private String clientId;
    @Value("${paypal.client-secret}") private String clientSecret;
    @Value("${paypal.base-url}") private String baseUrl;
    @Value("${paypal.return-url}") private String returnUrl;
    @Value("${paypal.cancel-url}") private String cancelUrl;

    private final WebClient webClient = WebClient.builder().build();

    public String getAccessToken() {
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        Map<String, String> response = webClient.post()
                .uri(baseUrl + "/v1/oauth2/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();
        return response.get("access_token");
    }

    public Map<String, String> createOrder(BigDecimal amount, String currency) {
        String accessToken = getAccessToken();

        Map<String, Object> body = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "amount", Map.of("currency_code", currency, "value", amount.toPlainString())
                )),
                "application_context", Map.of(
                        "return_url", returnUrl,
                        "cancel_url", cancelUrl
                )
        );

        Map<String, Object> response = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .post()
                .uri("/v2/checkout/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        String orderId = (String) response.get("id");  // đây chính là orderId
        List<Map<String, String>> links = (List<Map<String, String>>) response.get("links");
        String approvalUrl = links.stream().filter(l -> "approve".equals(l.get("rel")))
                .findFirst().get().get("href");

        return Map.of(
                "orderId", orderId,
                "approvalUrl", approvalUrl
        );
    }

    public Map<String, Object> captureOrder(String orderId) {
        String accessToken = getAccessToken();
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .post()
                .uri("/v2/checkout/orders/{id}/capture", orderId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
