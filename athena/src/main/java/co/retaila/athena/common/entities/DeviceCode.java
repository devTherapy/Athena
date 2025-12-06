package co.retaila.athena.common.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
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
        name = "device_codes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "deviceCode"})
        }
)
public class DeviceCode extends BaseEntity{

    @ManyToOne
    @JoinColumn(nullable = false)
    private Client client;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Domain requestedDomain;
    @Column(nullable = false)
    private String deviceCode;
    @Column(nullable = false, unique = true)
    private String userCode;
    @Column(nullable = false)
    private String verificationUri;
    @Lob
    private String requestedScope;
    @Column(nullable = false)
    private LocalDateTime expiry;
    @Column(nullable = false)
    private int intervalInSeconds;
    @Column(nullable = false)
    private boolean isUsed;

    public boolean scopeNotRequested() {
        return this.requestedScope == null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.getExpiry());
    }

}
