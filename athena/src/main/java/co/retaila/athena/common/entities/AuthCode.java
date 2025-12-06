package co.retaila.athena.common.entities;

import co.retaila.athena.modules.auth.enums.CodeChallengeMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "auth_codes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "authCode"})
        }
)
public class AuthCode extends BaseEntity{

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Client client;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Domain requestedDomain;
    @Column(nullable = false)
    private String authCode;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String requestRedirectUri;
    @Lob
    @Column(nullable = false)
    private String requestedScope;
    @Lob
    private String codeChallenge;
    @Enumerated(EnumType.STRING)
    private CodeChallengeMethod codeChallengeMethod;
    @Column(nullable = false)
    private LocalDateTime expiry;
    @Column(nullable = false)
    private boolean isUsed;

    public boolean isPkceAuthCode() {
        return StringUtils.isNotBlank(this.getCodeChallenge()) && this.getCodeChallengeMethod() != null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.getExpiry());
    }

}
