package com.policymanagement.policymanagementservice.validator;

import com.policymanagement.policymanagementservice.data.request.PolicyCreateRequestData;
import com.policymanagement.policymanagementservice.data.request.PolicyUpdateRequestData;
import com.policymanagement.policymanagementservice.exception.InputValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

import static com.policymanagement.policymanagementservice.util.DateUtils.stringToDate;

@Component
public class InputValidator {
    public void validateCreatePolicyInput(PolicyCreateRequestData requestData) {
        var inputDate = requestData.getStartDate();
        var startDate = stringToDate(inputDate);
        var insuredPersons = requestData.getInsuredPersons();

        if (inputDate == null || inputDate.isBlank()) {
            throw new InputValidationException("The request start date is missing");
        }

        if (startDate.isBefore(ChronoLocalDate.from(LocalDate.now().atStartOfDay().plusDays(1L)))) {
            throw new InputValidationException("The request date should be in future starting from today");
        }

        if (insuredPersons == null || insuredPersons.isEmpty()) {
            throw new InputValidationException("The request insured persons list is empty");
        }
    }

    public void validateUpdatePolicyInput(PolicyUpdateRequestData requestData) {
        var inputDate = requestData.getEffectiveDate();
        var policyId = requestData.getPolicyId();
        var startDate = stringToDate(requestData.getEffectiveDate());
        var insuredPersons = requestData.getInsuredPersons();

        if (inputDate == null || inputDate.isBlank()) {
            throw new InputValidationException("The request effective date is missing");
        }

        if (policyId == null || policyId.isBlank()) {
            throw new InputValidationException("The request should provide a policy Id");
        }

        if (startDate.isBefore(ChronoLocalDate.from(LocalDate.now().atStartOfDay().plusDays(1L)))) {
            throw new InputValidationException("The request date should be in future starting from today");
        }

        if (insuredPersons == null || insuredPersons.isEmpty()) {
            throw new InputValidationException("The request insured persons list is empty");
        }
    }

    public void validatePolicyInfoInput(String policyId) {
        if (policyId == null) {
            throw new InputValidationException("The request should provide a policy Id");
        }
    }

}
