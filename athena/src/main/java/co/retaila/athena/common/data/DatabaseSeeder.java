package co.retaila.athena.common.data;

import co.retaila.athena.common.constants.ClientConstants;
import co.retaila.athena.common.constants.DomainConstants;
import co.retaila.athena.common.constants.RoleConstants;
import co.retaila.athena.common.constants.SuperuserConstants;
import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.ClientAuthority;
import co.retaila.athena.common.entities.ClientDomain;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.RoleAuthority;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.entities.UserRole;
import co.retaila.athena.common.enums.ClientApplicationType;
import co.retaila.athena.common.enums.ClientType;
import co.retaila.athena.common.properties.AppProperties;
import co.retaila.athena.common.repositories.AuthorityRepository;
import co.retaila.athena.common.repositories.ClientAuthorityRepository;
import co.retaila.athena.common.repositories.ClientDomainRepository;
import co.retaila.athena.common.repositories.ClientRepository;
import co.retaila.athena.common.repositories.DomainRepository;
import co.retaila.athena.common.repositories.RoleAuthorityRepository;
import co.retaila.athena.common.repositories.RoleRepository;
import co.retaila.athena.common.repositories.UserRepository;
import co.retaila.athena.common.repositories.UserRoleRepository;
import co.retaila.athena.common.security.AuthoritiesProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final DomainRepository domainRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final ClientRepository clientRepository;
    private final ClientDomainRepository clientDomainRepository;
    private final ClientAuthorityRepository clientAuthorityRepository;

    private Domain installAthenaDomain() {
        Optional<Domain> athenaDomainExists = domainRepository.findByName(DomainConstants.ATHENA);

        if (athenaDomainExists.isPresent())
            return athenaDomainExists.get();

        Domain athenaDomain = DomainConstants.buildAthenaDomain();

        return domainRepository.save(athenaDomain);
    }

    private List<Authority> installAthenaAuthorities(Domain athenaDomain) {
        var authorities = new ArrayList<Authority>();

        for (Authority authority : AuthoritiesProvider.getAllAuthorities()) {
            if (authorityRepository.existsByName(authority.getName()))
                continue;

            authority.setDomain(athenaDomain);
            authorities.add(authority);
        }

        return authorityRepository.saveAll(authorities);
    }

    private Role installDefaultRole(Domain athenaDomain) {
        Optional<Role> defaultRoleExists = roleRepository.findByNameAndDomain(RoleConstants.SUPERUSER, athenaDomain);

        if (defaultRoleExists.isPresent())
            return defaultRoleExists.get();

        Role defaultRole = Role.builder()
                .name(RoleConstants.SUPERUSER)
                .description("Default superuser role")
                .domain(athenaDomain)
                .build();

        return roleRepository.save(defaultRole);
    }

    private User seedSuperUser(Domain athenaDomain) {
        User superUser = userRepository
                .findByUsernameAndDomainName(SuperuserConstants.SUPERUSER_USERNAME, athenaDomain.getName())
                .orElse(null);

        if (superUser != null) {
            return superUser;
        }

        superUser = User.builder()
                .username(SuperuserConstants.SUPERUSER_USERNAME)
                .email(SuperuserConstants.SUPERUSER_EMAIL)
                .emailVerified(true)
                .firstName(SuperuserConstants.SUPERUSER_FIRST_NAME)
                .lastName(SuperuserConstants.SUPERUSER_LAST_NAME)
                .password(passwordEncoder.encode(appProperties.getSuperuserPassword()))
                .domain(athenaDomain)
                .build();

        return userRepository.save(superUser);
    }

    private Client seedDefaultClient() {
        Optional<Client> defaultClientExists = clientRepository.findByIdentifier(ClientConstants.ATHENA);

        if (defaultClientExists.isPresent())
            return defaultClientExists.get();

        Client defaultClient = Client.builder()
                .identifier(ClientConstants.ATHENA)
                .name(ClientConstants.ATHENA)
                .secret(passwordEncoder.encode(appProperties.getClientSecret()))
                .adminEmail(ClientConstants.ADMIN_EMAIL)
                .redirectUri("")
                .clientType(ClientType.FIRST_PARTY)
                .clientApplicationType(ClientApplicationType.SERVICE)
                .tokenValidityInSeconds(ClientConstants.TOKEN_VALIDITY_IN_SECONDS)
                .build();

        return clientRepository.save(defaultClient);
    }

    private void installSuperUserRoleMapping(User superUser, Role role) {
        if (userRoleRepository.existsByUserAndRole(superUser, role))
            return;

        UserRole superUserRoleMapping = UserRole.builder()
                .user(superUser)
                .role(role)
                .build();

        userRoleRepository.save(superUserRoleMapping);
    }

    private void installDefaultRoleAuthorityMappings(Role defaultRole, List<Authority> athenaAuthorities) {
        var roleAuthorityMappings = new ArrayList<RoleAuthority>();

        for (var authority : athenaAuthorities) {
            if (roleAuthorityRepository.existsByRoleAndAuthority(defaultRole, authority))
                continue;

            var mapping = RoleAuthority.builder()
                    .role(defaultRole)
                    .authority(authority)
                    .build();

            roleAuthorityMappings.add(mapping);
        }

        roleAuthorityRepository.saveAll(roleAuthorityMappings);
    }

    private void installDefaultClientDomainMapping(Client defaultClient, Domain rootDomain) {
        if (clientDomainRepository.existsByClientAndDomain(defaultClient, rootDomain))
            return;

        ClientDomain clientDomainMapping = ClientDomain.builder()
                .client(defaultClient)
                .domain(rootDomain)
                .build();

        clientDomainRepository.save(clientDomainMapping);
    }

    private void installDefaultClientAuthorityMappings(Client defaultClient, Domain rootDomain, List<Authority> athenaAuthorities) {
        var clientAuthorityMappings = new ArrayList<ClientAuthority>();

        for (var authority : athenaAuthorities) {
            if (!authority.getDomain().getName().equals(rootDomain.getName()))
                continue;

            if (clientAuthorityRepository.existsByClientAndAuthority(defaultClient, authority))
                continue;

            var mapping = ClientAuthority.builder()
                    .client(defaultClient)
                    .authority(authority)
                    .build();

            clientAuthorityMappings.add(mapping);
        }

        clientAuthorityRepository.saveAll(clientAuthorityMappings);
    }


    @Override
    public void run(ApplicationArguments args) {
        Domain rootDomain = installAthenaDomain();
        List<Authority> athenaAuthorities = installAthenaAuthorities(rootDomain);
        Role defaultRole = installDefaultRole(rootDomain);
        User superUser = seedSuperUser(rootDomain);
        Client defaultClient = seedDefaultClient();

        installSuperUserRoleMapping(superUser, defaultRole);
        installDefaultRoleAuthorityMappings(defaultRole, athenaAuthorities);
        installDefaultClientDomainMapping(defaultClient, rootDomain);
        installDefaultClientAuthorityMappings(defaultClient, rootDomain, athenaAuthorities);
    }

}
