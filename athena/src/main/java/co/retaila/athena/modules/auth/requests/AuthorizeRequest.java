package co.retaila.athena.modules.auth.requests;

import co.retaila.athena.modules.auth.enums.CodeChallengeMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeRequest {

    private String responseType;
    private String clientId;
    private String domain;
    private String redirectUri;
    private String scope;
    private String state;
    private String codeChallenge;
    private CodeChallengeMethod codeChallengeMethod;

    public boolean codeChallengeIsPlain() {
        return CodeChallengeMethod.PLAIN.equals(this.getCodeChallengeMethod());
    }

}
