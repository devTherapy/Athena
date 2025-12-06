package co.retaila.athena.modules.resourceserver.controllers;

import co.retaila.athena.modules.resourceserver.queries.SearchResourceServersQuery;
import co.retaila.athena.modules.resourceserver.requests.CreateResourceServerRequest;
import co.retaila.athena.modules.resourceserver.services.ResourceServerService;
import co.retaila.athena.modules.resourceserver.viewmodels.ResourceServerViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_PREFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.AUTH_SUFFIX;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_CREATE_RESOURCE_SERVER;
import static co.retaila.athena.common.constants.AuthorityConstants.CAN_SEARCH_RESOURCE_SERVERS;

@RestController
@RequestMapping("api/resource-servers")
@RequiredArgsConstructor
public class ResourceServerController {

    private final ResourceServerService resourceServerService;

    @PreAuthorize(AUTH_PREFIX + CAN_SEARCH_RESOURCE_SERVERS + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Page<ResourceServerViewModel> searchResourceServers(@Valid SearchResourceServersQuery query) {
        return resourceServerService.searchResourceServers(query);
    }

    @PreAuthorize(AUTH_PREFIX + CAN_CREATE_RESOURCE_SERVER + AUTH_SUFFIX)
    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResourceServerViewModel createResourceServer(@Valid @RequestBody CreateResourceServerRequest request) {
        return resourceServerService.createResourceServer(request);
    }

}
