package co.retaila.athena.common.services.impl;

import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.exceptions.InvalidOperationException;
import co.retaila.athena.common.exceptions.ServerErrorException;
import co.retaila.athena.common.repositories.UserRepository;
import co.retaila.athena.common.services.LoggedInUserService;
import co.retaila.lib.security.athena.services.AthenaLoggedInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoggedInUserServiceImpl implements LoggedInUserService {

    private final UserRepository userRepository;
    private final AthenaLoggedInService athenaLoggedInService;

    @Override
    public User getLoggedInUser(String domain) {
        try {
            Optional<String> usernameExists = athenaLoggedInService.getUsername();
            if (usernameExists.isEmpty())
                throw new InvalidOperationException("Invalid user access token");

            String username = usernameExists.get();
            User user = userRepository
                    .findByUsernameAndDomainName(username, domain)
                    .orElse(null);

            if (user == null)
                throw new InvalidOperationException(String.format("User with username: '%s' specified in access token does not exist in domain: '%s'", username, domain));

            return user;
        }
        catch (InvalidOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            log.warn("Error occurred while getting logged in user");
            throw new ServerErrorException();
        }
    }

}
