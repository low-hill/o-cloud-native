package org.example.organizationservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.organizationservice.model.Organization;
import org.example.organizationservice.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value="v1/organization")
public class OrganizationController {

    private final OrganizationService service;


    @GetMapping(value="/{organizationId}")
    public ResponseEntity<Organization> getOrganization( @PathVariable("organizationId") String organizationId) {
        return ResponseEntity.ok(service.findById(organizationId));
    }

    @PutMapping(value="/{organizationId}")
    public void updateOrganization( @PathVariable("organizationId") String organizationId, @RequestBody Organization organization) {
        organization.setId(organizationId);
        service.update(organization);
    }

    @PostMapping
    public ResponseEntity<Organization>  saveOrganization(@RequestBody Organization organization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(organization));
    }

    @DeleteMapping(value="/{organizationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganization( @PathVariable("organizationId") String organizationId) {
        service.delete(organizationId);
    }

}