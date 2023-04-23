package com.policymanagement.policymanagementservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.policymanagement.policymanagementservice.data.response.PolicyInfoResponse;
import com.policymanagement.policymanagementservice.domain.PolicyData;
import com.policymanagement.policymanagementservice.repository.PolicyDataRepository;
import com.policymanagement.policymanagementservice.util.DateUtils;
import org.assertj.core.api.Assertions;
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
public class PolicyDataControllerTestInfoRequests {


    @MockBean
    PolicyDataRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetPolicyInfo_validRequestAndSuccessResponse() throws Exception {
        setUpGetPolicyInfoMocks();

        JsonNode expectedResponse = getJsonDataFromTestFile("testResponses/getInfoApi/response_body_success.json");

        ResponseEntity<PolicyInfoResponse> response = restTemplate.getForEntity(
                new URL("http://localhost:" + port + "/policies/policyId/CU423DF89/requestDate/03.10.2023").toString(), PolicyInfoResponse.class);

        JsonNode actualResponse = convertResponseIntoJson(response);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(expectedResponse).isEqualTo(actualResponse);
    }

    @Test
    public void testGetPolicyInfo_invalidDateAndErrorResponse() throws Exception {
        setUpGetPolicyInfoMocks();

        JsonNode expectedResponse = getJsonDataFromTestFile("testResponses/error/response_body_invalid_date_format.json");

        ResponseEntity<Object> response = restTemplate.getForEntity(
                new URL("http://localhost:" + port + "/policies/policyId/CU423DF89/requestDate/111.222.333").toString(), Object.class);

        var responseBodyJson = objectMapper.writeValueAsString(response.getBody());
        JsonNode actualResponse = objectMapper.readTree(responseBodyJson);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(expectedResponse).isEqualTo(actualResponse);
    }

    @Test
    public void testGetPolicyInfo_NoDateProvidedAndUseCurrentDate() throws Exception {
        setUpGetPolicyInfoMocks();

        String responseFromFile = getStringDataFromFile("testResponses/getInfoApi/response_body_no_date_provided_success.json");
        var todayDate = DateUtils.dateToString(LocalDate.now());

        responseFromFile = String.format(responseFromFile, todayDate);
        JsonNode expectedResponse = objectMapper.readTree(responseFromFile);

        ResponseEntity<PolicyInfoResponse> response = restTemplate.getForEntity(
                new URL("http://localhost:" + port + "/policies/policyId/CU423DF89").toString(), PolicyInfoResponse.class);

        JsonNode actualResponse = convertResponseIntoJson(response);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(expectedResponse).isEqualTo(actualResponse);
    }

    @Test
    public void testGetPolicyInfo_NoDataAvailableForCurrentDate() throws Exception {
        Mockito.when(repository.findByPolicyIdAndLatestStartDate(Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());

        String responseFromFile = getStringDataFromFile("testResponses/error/response_body_no_data_available.json");
        var todayDate = DateUtils.dateToString(LocalDate.now());

        responseFromFile = String.format(responseFromFile, todayDate);
        JsonNode expectedResponse = objectMapper.readTree(responseFromFile);

        ResponseEntity<Object> response = restTemplate.getForEntity(
                new URL("http://localhost:" + port + "/policies/policyId/CU423DF89/requestDate/03.10.2023").toString(), Object.class);

        var responseBodyJson = objectMapper.writeValueAsString(response.getBody());
        JsonNode actualResponse = objectMapper.readTree(responseBodyJson);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(expectedResponse).isEqualTo(actualResponse);
    }

    private String getStringDataFromFile(String name) throws URISyntaxException, IOException {
        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(name)).toURI());
        return new String(readAllBytes(path));
    }

    private JsonNode convertResponseIntoJson(ResponseEntity<PolicyInfoResponse> response) throws JsonProcessingException {
        var responseBodyJson = objectMapper.writeValueAsString(response.getBody());
        return objectMapper.readTree(responseBodyJson);
    }

    private JsonNode getJsonDataFromTestFile(String name) throws URISyntaxException, IOException {
        String fileContent = getStringDataFromFile(name);
        return objectMapper.readTree(fileContent);
    }

    private void setUpGetPolicyInfoMocks() {
        var entities = List.of(
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

        Mockito.when(repository.saveAll(Mockito.any())).thenReturn(entities);
        Mockito.when(repository.findByPolicyIdAndLatestStartDate(Mockito.any(), Mockito.any())).thenReturn(entities);
    }
}