package com.policymanagement.policymanagementservice.data.response;

import com.policymanagement.policymanagementservice.data.InsuredPerson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseData {
    private String policyId;
    private List<InsuredPerson> insuredPersons;
    private BigDecimal totalPremium;
}
