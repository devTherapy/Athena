package co.retaila.athena.common.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppProperties {

    @Value("${athena.config.public-key}")
    private String publicKey;
    @Value("${athena.config.private-key}")
    private String privateKey;
    @Value("${athena.config.auth-code.length}")
    private int authCodeLength;
    @Value("${athena.config.authorization-uri}")
    private String authorizationUri;
    @Value("${athena.config.device.login.poll-interval-seconds}")
    private int deviceLoginPollIntervalInSeconds;
    @Value("${athena.config.device.login.verification-uri}")
    private String deviceLoginVerificationUri;
    @Value("${athena.config.device.device-code.length}")
    private int deviceCodeLength;
    @Value("${athena.config.device.user-code.length}")
    private int userCodeLength;
    @Value("${athena.config.device.user-code.alphabet}")
    private String userCodeAlphabet;
    @Value("${athena.config.client.secret}")
    private String clientSecret;
    @Value("${athena.config.superuser.password}")
    private String superuserPassword;

}
