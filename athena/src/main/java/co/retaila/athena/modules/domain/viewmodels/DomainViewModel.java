package co.retaila.athena.modules.domain.viewmodels;

import co.retaila.athena.common.entities.Domain;
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
public class DomainViewModel extends BaseViewModel {
    private String name;
    private Integer tokenValidityInSeconds;
    private Integer authCodeValidityInSeconds;
    private Integer deviceCodeValidityInSeconds;

    public static DomainViewModel from(Domain domain) {
        DomainViewModel response = new DomainViewModel();

        BeanUtils.copyProperties(domain, response);

        return response;
    }

}
