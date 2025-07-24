package org.example.licensingservice.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.example.licensingservice.config.ServiceConfig;
import org.example.licensingservice.model.License;
import org.example.licensingservice.repository.LicenseRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LicenseService {

    private final MessageSource messages;

    private final LicenseRepository licenseRepository;

    private final ServiceConfig config;

    public License getLicense(String licenseId, String organizationId){
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(String.format(messages.getMessage("license.search.error.message", null, null),licenseId, organizationId));
        }
        return license.withComment(config.getProperty());
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
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(messages.getMessage("license.delete.message", null, null),licenseId);
        return responseMessage;

    }
}
