package com.hyper_engineer.mcp_server_webflux.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;

@Component
public class AnalyticsTools {

    private final WebClient webClient;

    @Autowired
    public AnalyticsTools(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8085").build();
    }

    @Tool(name = "analyticsExportCsvForCohort", description = "Export a cohort's selected columns as CSV/NDJSON stream or schedule export; returns a job id or download URL.")
    public Map<String, Object> exportCsvForCohort(Map<String, Object> payload) {
        // payload should include: cohortDef, columns, format (csv|ndjson), optional schedule
        Map<String, Object> resp = this.webClient.post()
                .uri("/api/analytics/export-csv")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        if (resp == null) return Collections.emptyMap();
        return resp;
    }
}

