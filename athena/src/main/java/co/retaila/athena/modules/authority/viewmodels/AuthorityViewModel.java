package co.retaila.athena.modules.authority.viewmodels;

import co.retaila.athena.common.entities.Authority;
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
public class AuthorityViewModel extends BaseViewModel {

    private String name;
    private String description;
    private String domain;

    public static AuthorityViewModel from(Authority authority) {
        AuthorityViewModel response = new AuthorityViewModel();

        BeanUtils.copyProperties(authority, response);
        response.setDomain(authority.getDomain().getName());

        return response;
    }

}
