package com.policymanagement.policymanagementservice.repository;


import com.policymanagement.policymanagementservice.domain.PolicyData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
public interface PolicyDataRepository extends MongoRepository<PolicyData, String> {

    @Query(value = "{ 'policy_id': ?0 }", fields = "{ 'person_id': 1, 'policy_version': 1, '_id': 0 }")
    List<PolicyData> findAllDistinctByPolicyIdWithSelectedFields(String existingPolicyId);

    @Query("{$and:[{'policy_id': ?0}, {'start_date': {$lt: ?1}}]}")
    List<PolicyData> findByPolicyIdAndLatestStartDate(String policyId, LocalDate latestStartDate);

}
