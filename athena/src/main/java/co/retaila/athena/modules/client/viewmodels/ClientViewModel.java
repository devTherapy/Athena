package co.retaila.athena.modules.client.viewmodels;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.viewmodels.BaseViewModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientViewModel extends BaseViewModel {

    private String identifier;
    private String name;
    private String adminEmail;
    private String redirectUri;
    private Integer tokenValidityInSeconds;

    public static ClientViewModel from(Client client) {
        ClientViewModel response = new ClientViewModel();

        BeanUtils.copyProperties(client, response);

        return response;
    }

}
