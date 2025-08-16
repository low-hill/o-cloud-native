package org.example.licensingservice.service;

import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.example.licensingservice.config.ServiceConfig;
import org.example.licensingservice.model.License;
import org.example.licensingservice.model.Organization;
import org.example.licensingservice.repository.LicenseRepository;
import org.example.licensingservice.client.OrganizationFeignClient;
import org.example.licensingservice.context.UserContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LicenseService {

    private final MessageSource messages;

    private final LicenseRepository licenseRepository;

    private final ServiceConfig config;

    private final OrganizationFeignClient organizationFeignClient;

    public License getLicense(String licenseId, String organizationId){
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(
                messages.getMessage("license.search.error.message", new Object[]{licenseId, organizationId}, LocaleContextHolder.getLocale()));
        }

        Organization organization = retrieveOrganizationInfo(organizationId);
        if (ObjectUtils.isNotEmpty(organization)) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }


    @CircuitBreaker(name = "organizationService", fallbackMethod = "buildFallbackOrganization")
    @Retry(name = "retryOrganizationService", fallbackMethod = "buildFallbackOrganization")
    @Bulkhead(name = "bulkheadOrganizationService", fallbackMethod = "buildFallbackOrganization")
    private Organization retrieveOrganizationInfo(String organizationId) {
        log.debug("retrieveOrganizationInfo Correlation id: {}",
            UserContextHolder.getContext().getCorrelationId());
        try {
            return organizationFeignClient.getOrganization(organizationId);
        } catch (FeignException e) {
            log.error("Error retrieving organization info for id {}. Status: {}. Message: {}", organizationId, e.status(), e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unused")
    private Organization buildFallbackOrganization(String organizationId, Throwable t) {
        log.warn("Executing fallback for organization-service. Organization ID: {}, Error: {}", organizationId, t.getMessage());
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Fallback-Organization");
        organization.setContactName("Fallback-Contact");
        organization.setContactEmail("fallback@example.com");
        organization.setContactPhone("000-000-0000");
        return organization;
    }

    public License createLicense(License license){
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);

        return license.withComment(config.getProperty());
    }

    public License updateLicense(License license){
        licenseRepository.save(license);

        return license.withComment(config.getProperty());
    }

    public String deleteLicense(String licenseId){

        licenseRepository.deleteById(licenseId);
        return messages.getMessage("license.delete.message", new Object[]{licenseId}, LocaleContextHolder.getLocale());
    }

    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
    @Bulkhead(name = "bulkheadLicenseService", type= Bulkhead.Type.THREADPOOL, fallbackMethod = "buildFallbackLicenseList")
    public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
        log.debug("getLicensesByOrganization Correlation id: {}",
                UserContextHolder.getContext().getCorrelationId());
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    @SuppressWarnings("unused")
    private List<License> buildFallbackLicenseList(String organizationId, Throwable t){
        log.warn("Executing fallback for license-service. Organization ID: {}, Error: {}", organizationId, t.getMessage());
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Service currently unavailable. Please try again later.");
        license.setLicenseType("Fallback");
        license.setComment("This is a fallback license due to a service issue.");
        fallbackList.add(license);
        return fallbackList;
    }

    private void randomlyRunLong() throws TimeoutException{
        Random rand = new Random();
        int randomNum = rand.nextInt(3) + 1;
        if (randomNum==3) sleep();
    }
    private void sleep() throws TimeoutException{
        try {
            Thread.sleep(5000);
            throw new java.util.concurrent.TimeoutException();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
