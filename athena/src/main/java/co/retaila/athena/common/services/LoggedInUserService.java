package co.retaila.athena.common.services;

import co.retaila.athena.common.entities.User;

public interface LoggedInUserService {
    
    User getLoggedInUser(String domain);

}
