package co.retaila.athena.modules.authcode.services;

import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.AuthCodeUsageAttemptCount;

public interface AuthCodeUsageAttemptCountService {

    void initializeAuthCodeUsageAttempt(AuthCode authCode);
    void incrementAuthCodeUsageAttemptCount(AuthCode authCode);
    AuthCodeUsageAttemptCount findByAuthCode(AuthCode authCode);

}
