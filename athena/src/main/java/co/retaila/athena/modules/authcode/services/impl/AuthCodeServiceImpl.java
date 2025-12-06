package co.retaila.athena.modules.authcode.services.impl;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.AuthCodeUsageAttemptCount;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.exceptions.AuthException;
import co.retaila.athena.common.properties.AppProperties;
import co.retaila.athena.common.repositories.AuthCodeRepository;
import co.retaila.athena.common.utils.CommonUtils;
import co.retaila.athena.modules.auth.models.CodeAuthorizeRequestValidationResult;
import co.retaila.athena.modules.auth.requests.AuthorizeRequest;
import co.retaila.athena.modules.auth.services.AuthTokenService;
import co.retaila.athena.modules.authcode.services.AuthCodeService;
import co.retaila.athena.modules.authcode.services.AuthCodeUsageAttemptCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCodeServiceImpl implements AuthCodeService {

    private final AuthCodeRepository authCodeRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final AuthTokenService authTokenService;
    private final AuthCodeUsageAttemptCountService authCodeUsageAttemptCountService;

    @Override
    public AuthCode validateClientAuthCode(Client client, String authCode) {
        List<AuthCode> clientAuthCodes = authCodeRepository.findByClient(client);

        return clientAuthCodes.stream()
                .filter(clientAuthCode -> passwordEncoder.matches(authCode, clientAuthCode.getAuthCode()))
                .findFirst()
                .orElseThrow(
                        () -> new AuthException(ErrorConstants.INVALID_GRANT, "Invalid auth code")
                );
    }

    @Override
    public String generateAuthCode(Client client) {
        String authCode;

        do {
            authCode = CommonUtils.generateRandomAlphaNumeric(appProperties.getAuthCodeLength());
        }
        while (authCodeExistsForClient(client, authCode));

        return authCode;
    }

    private boolean authCodeExistsForClient(Client client, String authCode) {
        List<AuthCode> clientAuthCodes = authCodeRepository.findByClient(client);

        return clientAuthCodes.stream()
                .anyMatch(clientAuthCode -> passwordEncoder.matches(authCode, clientAuthCode.getAuthCode()));
    }

    @Transactional
    @Override
    public void persistAuthCode(String authCodeString, AuthorizeRequest request, CodeAuthorizeRequestValidationResult validationResult) {
        AuthCode authCode = saveAuthCode(authCodeString, request, validationResult);
        authCodeUsageAttemptCountService.initializeAuthCodeUsageAttempt(authCode);
    }

    private AuthCode saveAuthCode(String authCodeString, AuthorizeRequest request, CodeAuthorizeRequestValidationResult validationResult) {
        int validityInSeconds = validationResult.getDomain().getAuthCodeValidityInSeconds();

        AuthCode authCode = AuthCode.builder()
                .user(validationResult.getUser())
                .client(validationResult.getClient())
                .requestedDomain(validationResult.getDomain())
                .authCode(passwordEncoder.encode(authCodeString))
                .state(request.getState())
                .requestRedirectUri(request.getRedirectUri())
                .requestedScope(request.getScope())
                .codeChallenge(request.getCodeChallenge())
                .codeChallengeMethod(request.getCodeChallengeMethod())
                .expiry(LocalDateTime.now().plusSeconds(validityInSeconds))
                .isUsed(false)
                .build();

        return authCodeRepository.save(authCode);
    }

    /**
     * Caller must not know about user's tokens invalidation. Information omitted from thrown exception.
     *
     * @param authCode must not be {@literal null}.
     */
    @Override
    public void useAuthCode(AuthCode authCode) {
        AuthCodeUsageAttemptCount attemptCount = authCodeUsageAttemptCountService.findByAuthCode(authCode);

        if (authCode.isUsed() || attemptCount.isMultipleAttempts()) {
            log.warn("Multiple auth code usage attempts. Invalidating all user's tokens...");
            authTokenService.invalidateUserTokens(authCode.getUser());
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Multiple auth code usage attempts");
        }

        if (authCode.isExpired())
            throw new AuthException(ErrorConstants.INVALID_GRANT, "Expired auth code");

        authCodeRepository.markAuthCodeAsUsed(authCode.getId());
    }

}
