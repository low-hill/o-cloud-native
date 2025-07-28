package org.example.organizationservice.service;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.example.organizationservice.model.Organization;
import org.example.organizationservice.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrganizationService {

    private final OrganizationRepository repository;

    public Organization findById(String organizationId) {
        Optional<Organization> opt = repository.findById(organizationId);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public Organization create(Organization organization){
        organization.setId( UUID.randomUUID().toString());
        organization = repository.save(organization);
        return organization;

    }

    public void update(Organization organization){
        repository.save(organization);
    }

    public void delete(String organizationId){
        repository.deleteById(organizationId);
    }
}
