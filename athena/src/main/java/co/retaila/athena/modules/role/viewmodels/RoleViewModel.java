package co.retaila.athena.modules.role.viewmodels;

import co.retaila.athena.common.entities.Role;
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
public class RoleViewModel extends BaseViewModel {

    private String name;
    private String description;
    private String domain;


    public static RoleViewModel from(Role role) {
        RoleViewModel response = new RoleViewModel();

        BeanUtils.copyProperties(role, response);
        response.setDomain(role.getDomain().getName());

        return response;
    }
}
