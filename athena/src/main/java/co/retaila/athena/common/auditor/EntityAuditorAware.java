package co.retaila.athena.common.auditor;

import co.retaila.lib.security.athena.services.AthenaLoggedInService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EntityAuditorAware implements AuditorAware<String> {

    private final AthenaLoggedInService athenaLoggedInService;

    @Override
    public Optional<String> getCurrentAuditor() {
        try{
            Optional<String> username = athenaLoggedInService.getUsername();

            if (username.isEmpty())
                return Optional.of("system");

            return username;
        }
        catch (Exception ex) {
            return Optional.of("system");
        }
    }

}
