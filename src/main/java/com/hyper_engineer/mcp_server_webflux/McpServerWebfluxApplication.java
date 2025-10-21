package com.hyper_engineer.mcp_server_webflux;

import com.hyper_engineer.mcp_server_webflux.service.AnalyticsTools;
import com.hyper_engineer.mcp_server_webflux.service.PatientQueryServiceTool;
import com.hyper_engineer.mcp_server_webflux.service.PatientTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class McpServerWebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpServerWebfluxApplication.class, args);
	}

    @Bean
    public List<ToolCallback> toolCallbacks(
            PatientQueryServiceTool patientQueryServiceTool,
            AnalyticsTools analyticsTools,
            PatientTools patientTools) {
        return List.of(ToolCallbacks.from(patientQueryServiceTool, analyticsTools, patientTools));
    }

}
