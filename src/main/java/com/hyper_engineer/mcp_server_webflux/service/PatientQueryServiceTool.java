package com.hyper_engineer.mcp_server_webflux.service;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class PatientQueryServiceTool {
    private final WebClient webClient;

    public PatientQueryServiceTool(WebClient.Builder webClientBuilder) {
        // Build the WebClient instance using the pre-configured builder
        this.webClient = webClientBuilder.baseUrl("http://localhost:8085").build();
    }

    @Tool(
            name = "test.testTool",
            description = "A test tool to verify tool integration.")
    public String testTool() {
        return this.webClient.get()
                .uri("/api/test")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<String>() {})
                .block();
    }

    @Tool(
            name = "patient.byDiseaseCount",
            description = "Partition patients into single-disease vs multi-disease groups for segmentation and resource planning.")
    public Mono<Map<String, Object>> getAllVideos() {
        return this.webClient.get()
                .uri("/api/patients/by-disease-count")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}
