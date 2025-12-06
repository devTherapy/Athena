package co.retaila.athena.modules.domain.controllers;

import co.retaila.athena.modules.domain.queries.SearchDomainsQuery;
import co.retaila.athena.modules.domain.requests.CreateDomainRequest;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.domain.viewmodels.DomainViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_PREFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_SUFFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_CREATE_DOMAIN;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_SEARCH_DOMAINS;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_VIEW_DOMAIN;

@RestController
@RequestMapping("api/domains")
@RequiredArgsConstructor
public class DomainController {

    private final DomainService domainService;

    @PreAuthorize(AUTH_PREFIX + CAN_SEARCH_DOMAINS + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Page<DomainViewModel> searchDomains(@Valid SearchDomainsQuery query) {
        return domainService.searchDomains(query);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_DOMAIN + AUTH_SUFFIX)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DomainViewModel getDomain(@PathVariable long id) {
        return domainService.getDomain(id);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_VIEW_DOMAIN + AUTH_SUFFIX)
    @RequestMapping(path = "/name/{name}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DomainViewModel getDomain(@PathVariable String name) {
        return domainService.getDomain(name);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_CREATE_DOMAIN + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public DomainViewModel createDomain(@Valid @RequestBody CreateDomainRequest request) {
        return domainService.createDomain(request);
    }

}
