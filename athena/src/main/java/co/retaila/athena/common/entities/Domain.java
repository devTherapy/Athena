package co.retaila.athena.common.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "domains")
public class Domain extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private Integer tokenValidityInSeconds;
    @Column(nullable = false)
    private Integer authCodeValidityInSeconds;
    @Column(nullable = false)
    private Integer deviceCodeValidityInSeconds;

}
