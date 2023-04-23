package com.policymanagement.policymanagementservice.service;

import com.policymanagement.policymanagementservice.data.InsuredPerson;
import com.policymanagement.policymanagementservice.data.request.PolicyCreateRequestData;
import com.policymanagement.policymanagementservice.data.request.PolicyUpdateRequestData;
import com.policymanagement.policymanagementservice.data.response.PolicyCreateResponse;
import com.policymanagement.policymanagementservice.data.response.PolicyInfoResponse;
import com.policymanagement.policymanagementservice.data.response.PolicyUpdateResponse;
import com.policymanagement.policymanagementservice.domain.ExistingPolicyDetails;
import com.policymanagement.policymanagementservice.domain.PolicyData;
import com.policymanagement.policymanagementservice.exception.NoDataException;
import com.policymanagement.policymanagementservice.repository.PolicyDataRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.policymanagement.policymanagementservice.util.DateUtils.dateToString;
import static com.policymanagement.policymanagementservice.util.DateUtils.stringToDate;

@Service
public class PolicyDataService {
    @Autowired
    PolicyDataRepository repository;

    private static Function<PolicyData, InsuredPerson> getPolicyDataInsuredPersonFunction() {
        return dbPolicyItem -> InsuredPerson.builder()
                .id(dbPolicyItem.getPersonId())
                .premium(dbPolicyItem.getPremium())
                .firstName(dbPolicyItem.getFirstName())
                .secondName(dbPolicyItem.getLastName())
                .build();
    }

    private static Long getMaximumValueByCriteria(List<PolicyData> existingDetails, Function<PolicyData, Long> mapper) {
        return existingDetails.stream().map(mapper).distinct().max(Comparator.naturalOrder()).get();
    }

    private static List<PolicyData> prepareEntriesForDb(String policyId, LocalDate date, long policyVersion, List<InsuredPerson> persons) {
        return persons
                .stream()
                .map(person ->
                        PolicyData.builder()
                                .policyId(policyId)
                                .policyVersion(policyVersion)
                                .personId(person.getId())
                                .firstName(person.getFirstName())
                                .lastName(person.getSecondName())
                                .premium(person.getPremium())
                                .startDate(date)
                                .build()
                )
                .toList();
    }

    private static BigDecimal calculateTotalPremium(List<InsuredPerson> insuredPersons) {
        return insuredPersons.stream().map(InsuredPerson::getPremium).reduce(BigDecimal::add).get();
    }

    @Transactional
    public PolicyCreateResponse createNewPolicy(PolicyCreateRequestData request) {
        var policyVersion = 1L;
        var nextAvailablePersonId = 1L;
        var policyId = generateRandomId();

        var startDate = stringToDate(request.getStartDate());
        var persons = generatePolicyIdsForNewPersons(request.getInsuredPersons(), nextAvailablePersonId);

        var policyPersonsData = prepareEntriesForDb(policyId, startDate, policyVersion, persons);
        var savedData = repository.saveAll(policyPersonsData);

        var insuredPersons = savedData.stream().map(getPolicyDataInsuredPersonFunction()).toList();
        var totalPremium = calculateTotalPremium(insuredPersons);

        return PolicyCreateResponse.builder()
                .startDate(dateToString(startDate))
                .policyId(policyId)
                .insuredPersons(insuredPersons)
                .totalPremium(totalPremium)
                .build();
    }

    @Transactional
    public PolicyUpdateResponse addPolicyUpdate(PolicyUpdateRequestData request) {
        var policyId = request.getPolicyId();
        var existingPolicyDetails = findRelevantExistingValuesForPolicy(policyId);
        var policyVersion = existingPolicyDetails.getLargestExistingPolicyVersion() + 1;
        var nextAvailablePersonId = existingPolicyDetails.getLargestExistingPersonsIds() + 1;

        var effectiveDate = stringToDate(request.getEffectiveDate());
        var persons = generatePolicyIdsForNewPersons(request.getInsuredPersons(), nextAvailablePersonId);

        var policyPersonsData = prepareEntriesForDb(policyId, effectiveDate, policyVersion, persons);
        var savedData = repository.saveAll(policyPersonsData);

        var insuredPersons = savedData.stream().map(getPolicyDataInsuredPersonFunction()).toList();
        var totalPremium = calculateTotalPremium(insuredPersons);

        return PolicyUpdateResponse.builder()
                .effectiveDate(dateToString(effectiveDate))
                .policyId(policyId)
                .insuredPersons(insuredPersons)
                .totalPremium(totalPremium)
                .build();
    }

    @Transactional(readOnly = true)
    public PolicyInfoResponse getPolicyInfo(String policyId, LocalDate requestDate) {
        var personsOnMostRecentPolicy = getPolicyInsuredPeopleFromDb(policyId, requestDate);
        var totalPremium = calculateTotalPremium(personsOnMostRecentPolicy);

        return PolicyInfoResponse.builder()
                .requestDate(dateToString(requestDate))
                .policyId(policyId)
                .insuredPersons(personsOnMostRecentPolicy)
                .totalPremium(totalPremium)
                .build();
    }

    private List<InsuredPerson> getPolicyInsuredPeopleFromDb(String policyId, LocalDate requestDate) {
        var policyiesToDate = repository.findByPolicyIdAndLatestStartDate(policyId, requestDate);
        var theMostRecentPolicyVersion = policyiesToDate.stream().map(PolicyData::getPolicyVersion).max(Comparator.naturalOrder()).orElseThrow(NoDataException::new);

        return policyiesToDate.stream()
                .filter(item -> item.getPolicyVersion().equals(theMostRecentPolicyVersion))
                .map(getPolicyDataInsuredPersonFunction())
                .toList();
    }

    private ExistingPolicyDetails findRelevantExistingValuesForPolicy(String existingPolicyId) {
        var existingDetails = repository.findAllDistinctByPolicyIdWithSelectedFields(existingPolicyId);
        Function<PolicyData, Long> personIdMapper = PolicyData::getPersonId;
        Function<PolicyData, Long> policyVersionMapper = PolicyData::getPolicyVersion;

        var currentBiggestPersonId = getMaximumValueByCriteria(existingDetails, personIdMapper);
        var currentPolicyVersion = getMaximumValueByCriteria(existingDetails, policyVersionMapper);
        return ExistingPolicyDetails.builder().largestExistingPolicyVersion(currentPolicyVersion).largestExistingPersonsIds(currentBiggestPersonId).build();
    }

    private List<InsuredPerson> generatePolicyIdsForNewPersons(List<InsuredPerson> persons, Long nextAvailablePersonId) {
        for (InsuredPerson person : persons) {
            if (person.getId() == null) {
                person.setId(nextAvailablePersonId++);
            }
        }
        return persons;
    }

    private String generateRandomId() {
        return RandomStringUtils.randomAlphanumeric(9).toUpperCase();
    }

}
