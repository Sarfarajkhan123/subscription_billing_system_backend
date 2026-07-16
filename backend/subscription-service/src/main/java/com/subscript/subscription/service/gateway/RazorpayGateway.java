package com.subscript.subscription.service.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Thin Razorpay Test-Mode gateway using only JDK built-ins (no SDK dependency):
 *  - createOrder() calls the Razorpay Orders REST API with Basic auth.
 *  - isValidSignature() re-computes the HMAC-SHA256 of "orderId|paymentId" with
 *    the secret and compares it to the signature returned by Checkout.
 *
 * The key SECRET is injected from backend config only and never returned to any
 * caller; only {@link #getKeyId()} (the public key id) is exposed.
 */
@Component
public class RazorpayGateway {

    private static final String ORDERS_URL = "https://api.razorpay.com/v1/orders";

    private final String keyId;
    private final String keySecret;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public RazorpayGateway(
            @Value("${razorpay.key.id}") String keyId,
            @Value("${razorpay.key.secret}") String keySecret) {
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    /** Public key id — safe to send to the frontend. */
    public String getKeyId() {
        return keyId;
    }

    /** Creates a Razorpay order and returns its id. amount is in paise. */
    public String createOrder(long amountPaise, String currency, String receipt) {
        try {
            String body = mapper.writeValueAsString(Map.of(
                    "amount", amountPaise,
                    "currency", currency,
                    "receipt", receipt,
                    "payment_capture", 1));

            String auth = Base64.getEncoder().encodeToString(
                    (keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(ORDERS_URL))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException("Razorpay order creation failed: " + resp.body());
            }
            JsonNode node = mapper.readTree(resp.body());
            return node.get("id").asText();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Razorpay order creation error: " + e.getMessage(), e);
        }
    }

    /** True when the Checkout signature matches HMAC-SHA256(orderId|paymentId, secret). */
    public boolean isValidSignature(String orderId, String paymentId, String signature) {
        if (orderId == null || paymentId == null || signature == null) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(
                    (orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8));
            StringBuilder expected = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                expected.append(Character.forDigit((b >> 4) & 0xF, 16));
                expected.append(Character.forDigit(b & 0xF, 16));
            }
            return constantTimeEquals(expected.toString(), signature);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
