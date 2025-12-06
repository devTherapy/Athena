package co.retaila.athena.modules.devicecode.viewmodels;

import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.viewmodels.BaseViewModel;
import co.retaila.athena.modules.client.viewmodels.ClientViewModel;
import co.retaila.athena.modules.domain.viewmodels.DomainViewModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCodeViewModel extends BaseViewModel {

    private ClientViewModel client;
    private DomainViewModel requestedDomain;
    private String verificationUri;
    private String requestedScope;
    private LocalDateTime expiry;
    private int intervalInSeconds;
    private boolean isUsed;

    public static DeviceCodeViewModel from(DeviceCode deviceCode) {
        DeviceCodeViewModel response = new DeviceCodeViewModel();

        BeanUtils.copyProperties(deviceCode, response);
        response.setClient(ClientViewModel.from(deviceCode.getClient()));
        response.setRequestedDomain(DomainViewModel.from(deviceCode.getRequestedDomain()));

        return response;
    }

}
