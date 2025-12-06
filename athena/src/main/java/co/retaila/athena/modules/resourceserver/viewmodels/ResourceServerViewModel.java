package co.retaila.athena.modules.resourceserver.viewmodels;

import co.retaila.athena.common.entities.ResourceServer;
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
public class ResourceServerViewModel extends BaseViewModel {

    private String resourceId;

    public static ResourceServerViewModel from(ResourceServer resourceServer) {
        ResourceServerViewModel response = new ResourceServerViewModel();

        BeanUtils.copyProperties(resourceServer, response);

        return response;
    }

}
