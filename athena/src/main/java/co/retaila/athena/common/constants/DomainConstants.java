package co.retaila.athena.common.constants;

import co.retaila.athena.common.entities.Domain;

public class DomainConstants {

    public static final String ATHENA = "athena";
    public static final int TOKEN_VALIDITY_IN_SECONDS = 3600;
    public static final int AUTH_CODE_VALIDITY_IN_SECONDS = 30;
    public static final int DEVICE_CODE_VALIDITY_IN_SECONDS = 600;

    public static Domain buildAthenaDomain(){
        return Domain.builder()
                .name(DomainConstants.ATHENA)
                .tokenValidityInSeconds(DomainConstants.TOKEN_VALIDITY_IN_SECONDS)
                .authCodeValidityInSeconds(DomainConstants.AUTH_CODE_VALIDITY_IN_SECONDS)
                .deviceCodeValidityInSeconds(DomainConstants.DEVICE_CODE_VALIDITY_IN_SECONDS)
                .build();
    }
}
