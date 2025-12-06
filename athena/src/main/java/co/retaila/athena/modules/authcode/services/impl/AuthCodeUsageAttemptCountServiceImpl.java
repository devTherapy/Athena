package co.retaila.athena.modules.authcode.services.impl;

import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.AuthCodeUsageAttemptCount;
import co.retaila.athena.common.exceptions.ServerErrorException;
import co.retaila.athena.common.repositories.AuthCodeUsageAttemptCountRepository;
import co.retaila.athena.modules.authcode.services.AuthCodeUsageAttemptCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCodeUsageAttemptCountServiceImpl implements AuthCodeUsageAttemptCountService {

    private final AuthCodeUsageAttemptCountRepository authCodeUsageAttemptCountRepository;

    @Override
    public void initializeAuthCodeUsageAttempt(AuthCode authCode) {
        AuthCodeUsageAttemptCount attemptCount = AuthCodeUsageAttemptCount.builder()
                .authCode(authCode)
                .attemptCount(0)
                .build();

        authCodeUsageAttemptCountRepository.save(attemptCount);
    }

    @Override
    public void incrementAuthCodeUsageAttemptCount(AuthCode authCode) {
        authCodeUsageAttemptCountRepository.incrementAttemptCount(authCode.getId());
    }

    @Override
    public AuthCodeUsageAttemptCount findByAuthCode(AuthCode authCode) {
        AuthCodeUsageAttemptCount attemptCount = authCodeUsageAttemptCountRepository.findByAuthCode(authCode)
                .orElse(null);

        if (attemptCount == null) {
            log.error(String.format("Usage attempt count not initialized for auth code with id: %s", authCode.getId()));
            throw new ServerErrorException();
        }

        return attemptCount;
    }

}
