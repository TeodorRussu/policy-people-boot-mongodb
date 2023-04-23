package com.policymanagement.policymanagementservice.domain;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Document("policy_data")
@TypeAlias("policy-person")
public class PolicyData {
    @Field("policy_id")
    private String policyId;
    @Field("start_date")
    private LocalDate startDate;
    @Field("policy_version")
    private Long policyVersion;
    @Field("person_id")
    private Long personId;
    @Field("first_name")
    private String firstName;
    @Field("last_name")
    private String lastName;
    @Field("premium")
    private BigDecimal premium;
}
