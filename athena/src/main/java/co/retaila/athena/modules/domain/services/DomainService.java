package co.retaila.athena.modules.domain.services;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.modules.domain.queries.SearchDomainsQuery;
import co.retaila.athena.modules.domain.requests.CreateDomainRequest;
import co.retaila.athena.modules.domain.viewmodels.DomainViewModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DomainService {

    Page<DomainViewModel> searchDomains(SearchDomainsQuery query);
    DomainViewModel getDomain(Long id);
    DomainViewModel getDomain(String name);
    DomainViewModel createDomain(CreateDomainRequest request);
    List<Domain> getClientDomains(Client client);
    Domain validateAuthDomain(String domain, Domain domainRequestedForAuthCode);
    Domain validateAuthDomain(String domainName);
    Domain validateDomain(String domainName);
    List<Domain> validateDomains(List<String> domains);
    Domain getDefaultDomain();

}
