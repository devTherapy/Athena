package co.retaila.athena.modules.scope.viewmodels;

import co.retaila.athena.common.entities.Scope;
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
public class ScopeViewModel extends BaseViewModel {

    private String name;
    private String description;
    private String domain;


    public static ScopeViewModel from(Scope scope) {
        ScopeViewModel response = new ScopeViewModel();

        BeanUtils.copyProperties(scope, response);
        response.setDomain(scope.getDomain().getName());

        return response;
    }
}
