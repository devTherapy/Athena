package co.retaila.athena.modules.revocation.services;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.User;

import java.util.List;

public interface RevocationService {

    List<Authority> getUserRevocations(User user);
    void createRevocations(User user, List<Authority> authorities);
    List<Authority> getAuthoritiesToRevoke(User user, List<Authority> authorities);
    void deleteRevocations(User user, List<Authority> authorities);
    List<Authority> getAuthoritiesToPermit(User user, List<Authority> authorities);

}
