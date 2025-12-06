package co.retaila.athena.modules.user.viewmodels;

import co.retaila.athena.common.entities.User;
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
public class UserViewModel extends BaseViewModel {

    private String username;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String domain;

    public static UserViewModel from(User user) {
        UserViewModel response = new UserViewModel();

        BeanUtils.copyProperties(user, response);
        response.setDomain(user.getDomain().getName());

        return response;
    }
}
