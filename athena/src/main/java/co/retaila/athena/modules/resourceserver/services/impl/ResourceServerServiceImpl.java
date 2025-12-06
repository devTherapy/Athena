package co.retaila.athena.modules.resourceserver.services.impl;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientResourceId;
import co.retaila.athena.common.entities.ResourceServer;
import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.common.repositories.ClientResourceIdRepository;
import co.retaila.athena.common.repositories.ResourceServerRepository;
import co.retaila.athena.modules.resourceserver.queries.SearchResourceServersQuery;
import co.retaila.athena.modules.resourceserver.requests.CreateResourceServerRequest;
import co.retaila.athena.modules.resourceserver.services.ResourceServerService;
import co.retaila.athena.modules.resourceserver.viewmodels.ResourceServerViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServerServiceImpl implements ResourceServerService {

    private final ClientResourceIdRepository clientResourceIdRepository;
    private final ResourceServerRepository resourceServerRepository;

    @Override
    public Page<ResourceServerViewModel> searchResourceServers(SearchResourceServersQuery query) {
        return resourceServerRepository.findAll(query.getPredicate(), query.getPageable()).map(ResourceServerViewModel::from);
    }

    @Override
    public ResourceServerViewModel createResourceServer(CreateResourceServerRequest request) {
        validateResourceServerCreationRequest(request);
        ResourceServer resourceServer = buildResourceServer(request);

        return ResourceServerViewModel.from(resourceServerRepository.save(resourceServer));
    }

    private void validateResourceServerCreationRequest(CreateResourceServerRequest request) {
        if (resourceServerRepository.existsByResourceId(request.getResourceId())) {
            throw new BadRequestException(String.format("ResourceServer with resourceId: '%s' already exists", request.getResourceId()));
        }
    }

    private ResourceServer buildResourceServer(CreateResourceServerRequest request) {
        return ResourceServer.builder()
                .resourceId(request.getResourceId())
                .build();
    }

    @Override
    public List<ResourceServer> getClientResourceServers(Client client) {
        return clientResourceIdRepository.findByClient(client).stream()
                .map(ClientResourceId::getResourceServer)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceServer> validateResourceIds(List<String> resourceIds) {
        List<ResourceServer> resourceServers = resourceServerRepository.findByResourceIdIn(resourceIds);

        if (resourceServers.size() < resourceIds.size()) {
            var foundResourceServers = resourceServers.stream().map(ResourceServer::getResourceId).collect(Collectors.toList());
            var resourceServersNotFound = resourceIds.stream()
                    .filter(resourceServer -> !foundResourceServers.contains(resourceServer))
                    .collect(Collectors.toList());

            throw new BadRequestException(
                    String.format("The following resourceServers sent do not exist: %s", String.join(", ", resourceServersNotFound))
            );
        }

        return resourceServers;
    }
    
}
