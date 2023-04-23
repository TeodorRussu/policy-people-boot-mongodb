package com.policymanagement.policymanagementservice.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuredPerson {
    private String firstName;
    private String secondName;
    private BigDecimal premium;
    private Long id;
}