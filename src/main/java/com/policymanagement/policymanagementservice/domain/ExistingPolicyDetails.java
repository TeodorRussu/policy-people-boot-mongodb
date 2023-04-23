package com.policymanagement.policymanagementservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistingPolicyDetails {
    Long largestExistingPolicyVersion;
    Long largestExistingPersonsIds;
}
