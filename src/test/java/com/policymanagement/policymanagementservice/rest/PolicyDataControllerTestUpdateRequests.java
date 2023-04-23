package com.policymanagement.policymanagementservice.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.policymanagement.policymanagementservice.data.InsuredPerson;
import com.policymanagement.policymanagementservice.data.request.PolicyUpdateRequestData;
import com.policymanagement.policymanagementservice.domain.PolicyData;
import com.policymanagement.policymanagementservice.repository.PolicyDataRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.nio.file.Files.readAllBytes;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PolicyDataControllerTestUpdateRequests {

    @MockBean
    PolicyDataRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    private static List<PolicyData> createMockEntities() {
        return List.of(
                PolicyData.builder()
                        .policyId("CU423DF89")
                        .policyVersion(1L)
                        .personId(1L)
                        .firstName("Jane")
                        .lastName("Johnson")
                        .premium(BigDecimal.valueOf(12.90))
                        .startDate(LocalDate.of(2022, 4, 3))
                        .build(),
                PolicyData.builder()
                        .policyId("CU423DF89")
                        .policyVersion(1L)
                        .personId(2L)
                        .firstName("Jack")
                        .lastName("Doe")
                        .startDate(LocalDate.of(2022, 4, 3))
                        .premium(BigDecimal.valueOf(15.90))
                        .build());
    }

    @BeforeEach
    public void setUp() {
        setUpGetPolicyInfoMocks();
    }

    @Test
    void testUpdatePolicy_noPolicyIdProvidedReturnsError() throws Exception {
        JsonNode expectedResponse = getJsonDataFromTestFile("testResponses/error/no_policy_id.json");

        var requestBody = getCreateNewPolicyRequestData("01.01.2025", "");
        ResponseEntity<Object> response = restTemplate.postForEntity(
                new URL("http://localhost:" + port + "/policies/update").toString(), requestBody, Object.class);

        var responseBodyJson = objectMapper.writeValueAsString(response.getBody());
        JsonNode actualResponse = objectMapper.readTree(responseBodyJson);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void testUpdatePolicy_noPersonsProvidedReturnsError() throws Exception {
        JsonNode expectedResponse = getJsonDataFromTestFile("testResponses/error/insured_persons_list_empty.json");

        var requestBody = PolicyUpdateRequestData.builder()
                .effectiveDate("12.01.2024")
                .insuredPersons(Collections.emptyList())
                .policyId("AA11CC2DD")
                .build();

        ResponseEntity<Object> response = restTemplate.postForEntity(
                new URL("http://localhost:" + port + "/policies/update").toString(), requestBody, Object.class);

        var responseBodyJson = objectMapper.writeValueAsString(response.getBody());
        JsonNode actualResponse = objectMapper.readTree(responseBodyJson);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    private String getStringDataFromFile(String name) throws URISyntaxException, IOException {
        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(name)).toURI());
        return new String(readAllBytes(path));
    }

    private JsonNode getJsonDataFromTestFile(String name) throws URISyntaxException, IOException {
        String fileContent = getStringDataFromFile(name);
        return objectMapper.readTree(fileContent);
    }

    private void setUpGetPolicyInfoMocks() {
        List<PolicyData> entities = createMockEntities();

        Mockito.when(repository.saveAll(Mockito.any())).thenReturn(entities);
        Mockito.when(repository.findByPolicyIdAndLatestStartDate(Mockito.any(), Mockito.any())).thenReturn(entities);
    }

    private PolicyUpdateRequestData getCreateNewPolicyRequestData(String date, String policyId) {
        var insuredPersons = List.of(
                InsuredPerson.builder()
                        .firstName("Jane")
                        .secondName("Johnson")
                        .premium(BigDecimal.valueOf(12.90))
                        .build(),
                InsuredPerson.builder()
                        .firstName("Jack")
                        .secondName("Doe")
                        .premium(BigDecimal.valueOf(15.90))
                        .build()
        );

        return PolicyUpdateRequestData.builder()
                .effectiveDate(date)
                .insuredPersons(insuredPersons)
                .policyId(policyId)
                .build();
    }
}