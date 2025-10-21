package com.hyper_engineer.mcp_server_webflux.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class PatientTools {

    private final WebClient webClient;

    @Autowired
    public PatientTools(WebClient.Builder webClientBuilder) {
        // Using relative base URL; WebClient configured in your app should have baseUrl as required.
        this.webClient = webClientBuilder.baseUrl("http://localhost:8085").build();
    }

    @Tool(name = "patientListTests", description = "List a patient's test history ordered by date with pagination and sort support.")
    public List<Map<String, Object>> listTests(UUID patientId, String sort, Integer page, Integer size) {
        try {
            // Build URI with optional query params
            UriComponentsBuilder b = UriComponentsBuilder.fromPath("/api/patients/{id}/tests");
            if (sort != null) b.queryParam("sort", sort);
            if (page != null) b.queryParam("page", page);
            if (size != null) b.queryParam("size", size);

            String uri = b.buildAndExpand(Collections.singletonMap("id", patientId)).toUriString();

            return this.webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            // return empty list on 404 or bubble up other statuses as runtime exception
            if (e.getRawStatusCode() == 404) return Collections.emptyList();
            throw e;
        }
    }
}