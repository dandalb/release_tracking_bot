package com.example.release_tracking_bot.config;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
@Log4j2
@Setter
@Getter
@PropertySource("application.properties")
public class TokenConfig {
    private static final String BASE_URI = "https://api.spotify.com/v1";
    private String token;
    private static final String TOKEN_URI = "https://accounts.spotify.com/api/token";
    @Value("${spotify.client_id}")
    private String clientId;
    @Value("${spotify.client_secret}")
    private String clientSecret;

    //@Scheduled(fixedRate = 1000 * 60 * 55)
    public void requestToken() {
        log.info("Getting token from Spotify API");
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);

        this.token =  (String) WebClient.create()
                .post()
                .uri(TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve()
                .bodyToMono(Map.class)
                .block().get("access_token");
        log.debug("Received token: " + this.token);
    }

    public WebClient getBaseWebClientWithCurrentToken() {
        return WebClient.builder()
                .baseUrl(BASE_URI)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }
}
