package co.retaila.athena.modules.resourceserver.services;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ResourceServer;
import co.retaila.athena.modules.resourceserver.queries.SearchResourceServersQuery;
import co.retaila.athena.modules.resourceserver.requests.CreateResourceServerRequest;
import co.retaila.athena.modules.resourceserver.viewmodels.ResourceServerViewModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ResourceServerService {

    Page<ResourceServerViewModel> searchResourceServers(SearchResourceServersQuery query);
    ResourceServerViewModel createResourceServer(CreateResourceServerRequest request);
    List<ResourceServer> getClientResourceServers(Client client);
    List<ResourceServer> validateResourceIds(List<String> resourceIds);

}
