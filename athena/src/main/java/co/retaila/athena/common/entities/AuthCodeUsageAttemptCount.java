package co.retaila.athena.common.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_code_usage_attempt_count")
public class AuthCodeUsageAttemptCount extends BaseEntity{

    @OneToOne
    @JoinColumn(nullable = false)
    private AuthCode authCode;
    @Column(nullable = false)
    private Integer attemptCount;

    public boolean isMultipleAttempts() {
        return this.getAttemptCount() > 1;
    }

}
