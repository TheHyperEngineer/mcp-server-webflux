package com.hyper_engineer.mcp_server_webflux.service;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                .bodyToMono(new ParameterizedTypeReference<String>() {
                })
                .block();
    }

    @Tool(
            name = "patient.countTests",
            description = "Count tests for a patient. Count total test records for a patient; fast aggregate used in summaries and dashboards.")
    public Map<String, Object> patientCountTests(UUID id) {
        return webClient
                .get()
                .uri("/api/patients/{id}/tests/count", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    @Tool(
            name = "patient.profileWithDiseasesAndHistory",
            description = "Patient profile with diseases and history. Join patient demographic, diagnosed diseases, and full test history for a single consolidated view.")
    public Map<String, Object> patientProfile(UUID id) {
        return webClient
                .get()
                .uri("/api/patients/{id}/profile", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    @Tool(
            name = "patient.noTestsSince",
            description = "Patients with no tests since a date. Returns patients with no tests since a given instant; used for outreach cohorts.")
    public List<Map<String, Object>> patientsNoTestsSince(String sinceIso) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/patients/no-tests-since").queryParam("since", sinceIso).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "patient.mostRecentTest", description = "Patient's most recent test. Returns the latest test record (type and date) for a patient; used for status indicators and due-date logic.")
    public Map<String, Object> patientMostRecentTest(UUID id) {
        return webClient.get().uri("/api/patients/{id}/most-recent-test", id).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "patient.overdueForTest", description = "Find patients overdue for a specific test. Identify patients overdue for a named recommended test using medical_tests.recommended_frequency_months.")
    public List<Map<String, Object>> patientsOverdueForTest(String testName, String asOfIso) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/patients/overdue-for-test").queryParam("testName", testName).queryParam("asOf", asOfIso).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "patient.byDiseaseCount", description = "Partition patients by number of diseases. Partition patients into single-disease vs multi-disease groups for segmentation and resource planning.")
    public Map<String, Object> patientByDiseaseCount() {
        return webClient.get().uri("/api/patients/by-disease-count").retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "patient.diseaseTestMix", description = "Patients with disease and specified test mix. Find patients with a disease who also have at least one of the supplied tests; used for guideline compliance checks.")
    public List<Map<String, Object>> patientDiseaseTestMix(String disease, List<String> tests) {
        String testsParam = String.join(",", tests);
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/patients/disease-test-mix").queryParam("disease", disease).queryParam("tests", testsParam).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "patient.coverage", description = "Coverage of patients with at least one test. Compute percent of patients with at least one test; fast data-quality metric for dashboards.")
    public Map<String, Object> patientCoverage() {
        return webClient.get().uri("/api/patients/coverage").retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "disease.listPatients", description = "List patients for a disease. List patients diagnosed with a particular disease; base cohort for analysis.")
    public List<Map<String, Object>> diseaseListPatients(String disease) {
        return webClient.get().uri("/api/diseases/{disease}/patients", disease).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "disease.distribution", description = "Disease distribution across population. Compute counts and percent distribution of diseases across the population for KPI and dashboards.")
    public List<Map<String, Object>> diseaseDistribution() {
        return webClient.get().uri("/api/diseases/distribution").retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "disease.compliance", description = "Compliance rates for disease-required tests. For a disease, return its recommended tests and per-test compliance rates vs recommended frequency.")
    public Map<String, Object> diseaseCompliance(String disease, String asOfIso) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/diseases/{disease}/compliance").queryParam("asOf", asOfIso).build(disease)).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "disease.comorbidityPatterns", description = "Comorbidity pair frequencies. Compute pairwise disease co-occurrence frequencies to surface common comorbidities.")
    public List<Map<String, Object>> diseaseComorbidityPatterns() {
        return webClient.get().uri("/api/diseases/comorbidity-patterns").retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "disease.rankByPatientCount", description = "Rank diseases by patient count. Rank diseases by number of distinct patients for prioritization and reporting.")
    public List<Map<String, Object>> diseaseRankByCount(Integer topN) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/diseases/rank-by-count").queryParam("top", topN).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "test.patientsReceiving", description = "Patients receiving a test and counts. List patients who ever received a specific test and how many times for utilization analysis.")
    public List<Map<String, Object>> testPatientsReceiving(String testName) {
        return webClient.get().uri("/api/tests/{testName}/patients", testName).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "test.utilizationMonthly", description = "Monthly utilization for a test. Return monthly time-series counts for a test between given dates for capacity planning.")
    public Map<String, Long> testUtilizationMonthly(String testName, String startIso, String endIso) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/tests/{testName}/utilization").queryParam("start", startIso).queryParam("end", endIso).build(testName)).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {
        }).block();
    }

    @Tool(name = "test.intervalsBetween", description = "Intervals between repeat tests per patient. Compute per-patient intervals between repeat tests and return median/mean to analyze retesting cadence.")
    public List<Map<String, Object>> testIntervalsBetween(String testName) {
        return webClient.get().uri("/api/tests/{testName}/intervals", testName).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "test.lowUsage", description = "Find tests with low usage. Identify tests with low distinct patient usage relative to recommended frequency for audit and outreach.")
    public List<Map<String, Object>> testLowUsage(String asOfIso) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/tests/low-usage").queryParam("asOf", asOfIso).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "test.unusuallyFrequent", description = "Flag unusually frequent repeat tests. Flag patients who had the same test more frequently than a threshold (possible redundancy/overuse).")
    public List<Map<String, Object>> testUnusuallyFrequent(String testName, Integer thresholdDays) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/tests/{testName}/unusually-frequent").queryParam("thresholdDays", thresholdDays).build(testName)).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "util.overdueAlerts", description = "Generate overdue alerts for tests. Active alert list of patients overdue for disease-specific recommended tests for outreach automation.")
    public List<Map<String, Object>> utilOverdueAlerts(String asOfIso) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/util/overdue-alerts").queryParam("asOf", asOfIso).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "util.trendByPeriod", description = "Compute time-series trend for a test or cohort. Generic aggregation tool to compute time-series (monthly/quarterly/yearly) for a test or cohort.")
    public List<Map<String, Object>> utilTrendByPeriod(Map<String, Object> payload) {
        return webClient.post().uri("/api/util/trend").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "util.cohortRetention", description = "Compute cohort retention at intervals. Compute retention of a cohort across follow-up intervals for program evaluation.")
    public List<Map<String, Object>> utilCohortRetention(Map<String, Object> payload) {
        return webClient.post().uri("/api/util/cohort-retention").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "util.timeToFirstTest", description = "Compute time from diagnosis to first test. Compute days from diagnosis to first appropriate test for quality metrics and delay analysis.")
    public List<Map<String, Object>> utilTimeToFirstTest(String disease, String testName) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/util/time-to-first-test").queryParam("disease", disease).queryParam("testName", testName).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "util.dataHealthReport", description = "Generate a data quality snapshot. Comprehensive data-quality snapshot including missing fields, referential integrity, duplicates, and date anomalies.")
    public Map<String, Object> utilDataHealthReport(String asOfIso) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/util/data-health").queryParam("asOf", asOfIso).build()).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "analytics.buildFeatureTable", description = "Build ML feature table for patients. Build ML-ready per-patient feature vectors (test counts, recency, comorbidity counts) for model training.")
    public List<Map<String, Object>> analyticsBuildFeatureTable(Map<String, Object> payload) {
        return webClient.post().uri("/api/analytics/feature-table").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "analytics.survivalInputs", description = "Prepare survival model inputs. Prepare time-to-event dataset (patient-level longitudinal features) for survival/risk modeling.")
    public Map<String, Object> analyticsSurvivalInputs(Map<String, Object> payload) {
        return webClient.post().uri("/api/analytics/survival-inputs").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "analytics.clusterPatients", description = "Cluster patients using feature vectors. Run unsupervised clustering on patient feature vectors and return clusters for segmentation.")
    public Map<String, Object> analyticsClusterPatients(Map<String, Object> payload) {
        return webClient.post().uri("/api/analytics/cluster").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "analytics.predictMissedTest", description = "Predict patients likely to miss next test. Score patients for probability of missing next recommended test using a trained model or default heuristic.")
    public List<Map<String, Object>> analyticsPredictMissedTest(Map<String, Object> payload) {
        return webClient.post().uri("/api/analytics/predict-missed-test").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "report.kpis", description = "Compute KPI metrics. Compute KPIs like tests per patient/year and percent meeting bundles for dashboards.")
    public Map<String, Object> reportKpis(String asOfIso) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/report/kpis").queryParam("asOf", asOfIso).build()).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "audit.patientAuditTrail", description = "Patient audit trail for compliance. Produce audit trail of tests and decisions used to determine compliance for a patient (for regulators).")
    public List<Map<String, Object>> auditPatientTrail(UUID id) {
        return webClient.get().uri("/api/audit/patient/{id}/trail", id).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "health.detectMalformedRecords", description = "Detect malformed/missing records. Find records missing required fields or with bad formats; useful for pre-ingest checks.")
    public List<Map<String, Object>> healthDetectMalformedRecords(Integer sampleSize) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/health/malformed-records").queryParam("sampleSize", sampleSize).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "health.findDuplicates", description = "Find duplicate test entries. Find exact duplicate test entries (same patient, same test, same timestamp) to deduplicate.")
    public List<Map<String, Object>> healthFindDuplicates(Integer limit) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/health/duplicates").queryParam("limit", limit).build()).retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).block();
    }

    @Tool(name = "forecast.capacityByTest", description = "Forecast lab capacity per test. Forecast lab capacity needs per test type based on historical utilization and simple growth model.")
    public Map<String, Object> forecastCapacityByTest(Map<String, Object> payload) {
        return webClient.post().uri("/api/forecast/capacity").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

    @Tool(name = "export.patientList", description = "Export filtered patient list for campaigns. Export patient lists for campaigns with filters and scheduled delivery.")
    public Map<String, Object> exportPatientList(Map<String, Object> payload) {
        return webClient.post().uri("/api/export/patients").bodyValue(payload).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        }).block();
    }

}
