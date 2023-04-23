package com.policymanagement.policymanagementservice.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyCreateResponse extends BaseResponseData {
    private String startDate;
}
