package co.retaila.athena.common.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "authorities",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "domain_id"})
        }
)
public class Authority extends BaseEntity {

    @Column(nullable = false)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Domain domain;

    public Authority(String name) {
        this.name = name;
    }

}
