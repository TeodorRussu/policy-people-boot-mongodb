package com.policymanagement.policymanagementservice.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyUpdateResponse extends BaseResponseData {
    private String effectiveDate;
}
