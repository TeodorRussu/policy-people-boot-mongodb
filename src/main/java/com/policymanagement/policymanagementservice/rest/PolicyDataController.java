package com.policymanagement.policymanagementservice.rest;

import com.policymanagement.policymanagementservice.data.request.PolicyCreateRequestData;
import com.policymanagement.policymanagementservice.data.request.PolicyUpdateRequestData;
import com.policymanagement.policymanagementservice.data.response.PolicyCreateResponse;
import com.policymanagement.policymanagementservice.data.response.PolicyInfoResponse;
import com.policymanagement.policymanagementservice.data.response.PolicyUpdateResponse;
import com.policymanagement.policymanagementservice.service.PolicyDataService;
import com.policymanagement.policymanagementservice.validator.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.policymanagement.policymanagementservice.util.DateUtils.stringToDate;


@RestController
@RequestMapping("/policies")
public class PolicyDataController {
    @Autowired
    PolicyDataService service;

    @Autowired
    InputValidator validator;

    @PostMapping("/create")
    public ResponseEntity<PolicyCreateResponse> createPolicy(@RequestBody PolicyCreateRequestData requestData) {
        validator.validateCreatePolicyInput(requestData);

        var response = service.createNewPolicy(requestData);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update")
    public ResponseEntity<PolicyUpdateResponse> addPolicyUpdate(@RequestBody PolicyUpdateRequestData requestData) {
        validator.validateUpdatePolicyInput(requestData);

        var response = service.addPolicyUpdate(requestData);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/policyId/{policyId}/requestDate/{requestDate}")
    public ResponseEntity<PolicyInfoResponse> getPolicyInfo(@PathVariable() String policyId, @PathVariable String requestDate) {
        validator.validatePolicyInfoInput(policyId);

        LocalDate requestLocalDate = stringToDate(requestDate);
        var response = service.getPolicyInfo(policyId, requestLocalDate);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/policyId/{policyId}")
    public ResponseEntity<PolicyInfoResponse> getPolicyInfo(@PathVariable String policyId) {
        validator.validatePolicyInfoInput(policyId);

        LocalDate requestLocalDate = LocalDate.now();
        var response = service.getPolicyInfo(policyId, requestLocalDate);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
