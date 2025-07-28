package org.example.licensingservice.service;

import java.util.UUID;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.example.licensingservice.config.ServiceConfig;
import org.example.licensingservice.model.License;
import org.example.licensingservice.model.Organization;
import org.example.licensingservice.repository.LicenseRepository;
import org.example.licensingservice.service.client.OrganizationFeignClient;
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

    private Organization retrieveOrganizationInfo(String organizationId) {
        try {
            return organizationFeignClient.getOrganization(organizationId);
        } catch (FeignException e) {
            log.error("Error retrieving organization info for id {}. Status: {}. Message: {}", organizationId, e.status(), e.getMessage());
            return null;
        }
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
}
