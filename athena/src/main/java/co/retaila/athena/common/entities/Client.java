package co.retaila.athena.common.entities;

import co.retaila.athena.common.enums.ClientApplicationType;
import co.retaila.athena.common.enums.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String identifier;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String secret;
    @Column(nullable = false)
    private String adminEmail;
    @Column(nullable = false)
    private String redirectUri;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientType clientType;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientApplicationType clientApplicationType;
    @Column(nullable = false)
    private Integer tokenValidityInSeconds;

}
