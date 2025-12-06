package co.retaila.athena.modules.domain.services.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.constants.DomainConstants;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientDomain;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.common.exceptions.NotFoundException;
import co.retaila.athena.common.repositories.ClientDomainRepository;
import co.retaila.athena.common.repositories.DomainRepository;
import co.retaila.athena.modules.domain.queries.SearchDomainsQuery;
import co.retaila.athena.modules.domain.requests.CreateDomainRequest;
import co.retaila.athena.modules.domain.services.DomainService;
import co.retaila.athena.modules.domain.viewmodels.DomainViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;
    private final ClientDomainRepository clientDomainRepository;

    @Override
    public Page<DomainViewModel> searchDomains(SearchDomainsQuery query) {
        return domainRepository.findAll(query.getPredicate(), query.getPageable()).map(DomainViewModel::from);
    }

    @Override
    public DomainViewModel getDomain(Long id) {
        Domain domain = validateDomain(id);
        return DomainViewModel.from(domain);
    }

    private Domain validateDomain(Long id) {
        Domain domain = domainRepository.findById(id).orElse(null);

        if (domain == null)
            throw new NotFoundException(String.format("Domain with id: '%d' does not exist", id));

        return domain;
    }

    @Override
    public DomainViewModel getDomain(String name) {
        Domain domain = validateDomain(name);
        return DomainViewModel.from(domain);
    }

    @Override
    public DomainViewModel createDomain(CreateDomainRequest request) {
        validateDomainCreationRequest(request);
        Domain domain = buildDomain(request);

        return DomainViewModel.from(domainRepository.save(domain));
    }

    private void validateDomainCreationRequest(CreateDomainRequest request) {
        if (domainRepository.existsByName(request.getName())) {
            throw new BadRequestException(String.format("Domain with name: '%s' already exists", request.getName()));
        }
    }

    private Domain buildDomain(CreateDomainRequest request) {
        int tokenValidityInSeconds = request.getTokenValidityInSeconds() != null ?
                request.getTokenValidityInSeconds() : DomainConstants.TOKEN_VALIDITY_IN_SECONDS;
        int authCodeValidityInSeconds = request.getAuthCodeValidityInSeconds() != null ?
                request.getAuthCodeValidityInSeconds() : DomainConstants.AUTH_CODE_VALIDITY_IN_SECONDS;
        int deviceCodeValidityInSeconds = request.getDeviceCodeValidityInSeconds() != null ?
                request.getDeviceCodeValidityInSeconds() : DomainConstants.DEVICE_CODE_VALIDITY_IN_SECONDS;

        return Domain.builder()
                .name(request.getName())
                .tokenValidityInSeconds(tokenValidityInSeconds)
                .authCodeValidityInSeconds(authCodeValidityInSeconds)
                .deviceCodeValidityInSeconds(deviceCodeValidityInSeconds)
                .build();
    }

    @Override
    public List<Domain> getClientDomains(Client client) {
        return clientDomainRepository.findByClient(client).stream()
                .map(ClientDomain::getDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Domain validateAuthDomain(String domain, Domain domainRequestedForAuthCode) {
        Domain requestedDomain = validateAuthDomain(domain);

        if (!domainRequestedForAuthCode.getName().equals(requestedDomain.getName()))
            throw new AuthException(ErrorConstants.INVALID_REQUEST, "Mismatch with domain requested on authorization");

        return requestedDomain;
    }

    @Override
    public Domain validateAuthDomain(String domain) {
        try {
            return validateDomain(domain);
        }
        catch (NotFoundException ex) {
            throw new AuthException(ErrorConstants.INVALID_REQUEST, ex.getMessage());
        }
    }

    @Override
    public Domain validateDomain(String name) {
        Domain domain = domainRepository.findByName(name).orElse(null);

        if (domain == null)
            throw new NotFoundException(String.format("Domain with name: '%s' does not exist", name));

        return domain;
    }

    @Override
    public List<Domain> validateDomains(List<String> domainNames) {
        List<Domain> domains = domainRepository.findByNameIn(domainNames);

        if (domains.size() < domainNames.size()) {
            var foundDomains = domains.stream().map(Domain::getName).collect(Collectors.toList());
            var domainsNotFound = domainNames.stream()
                    .filter(domain -> !foundDomains.contains(domain))
                    .collect(Collectors.toList());

            throw new BadRequestException(
                    String.format("The following domains sent do not exist: %s", String.join(", ", domainsNotFound))
            );
        }

        return domains;
    }

    @Override
    public Domain getDefaultDomain() {
        try {
            return validateDomain(DomainConstants.ATHENA);
        }
        catch (NotFoundException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

}
