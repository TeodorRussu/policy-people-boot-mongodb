package com.policymanagement.policymanagementservice.data.request;

import com.policymanagement.policymanagementservice.data.InsuredPerson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PolicyCreateRequestData {
    private String startDate;
    private List<InsuredPerson> insuredPersons;
    private String policyId;
}