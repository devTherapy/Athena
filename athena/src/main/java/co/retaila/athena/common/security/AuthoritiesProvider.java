package co.retaila.athena.common.security;

import co.retaila.athena.common.constants.AuthorityConstants;
import co.retaila.athena.common.entities.Authority;

import java.util.ArrayList;
import java.util.List;

public class AuthoritiesProvider {

    // Authority authorities
    public static Authority CAN_VIEW_AUTHORITY = new Authority(AuthorityConstants.CAN_VIEW_AUTHORITY);
    public static Authority CAN_SEARCH_AUTHORITIES = new Authority(AuthorityConstants.CAN_SEARCH_AUTHORITIES);
    public static Authority CAN_CREATE_AUTHORITY = new Authority(AuthorityConstants.CAN_CREATE_AUTHORITY);

    // Client authorities
    public static Authority CAN_VIEW_CLIENT = new Authority(AuthorityConstants.CAN_VIEW_CLIENT);
    public static Authority CAN_SEARCH_CLIENTS = new Authority(AuthorityConstants.CAN_SEARCH_CLIENTS);
    public static Authority CAN_CREATE_CLIENT = new Authority(AuthorityConstants.CAN_CREATE_CLIENT);
    public static Authority CAN_UPDATE_CLIENT = new Authority(AuthorityConstants.CAN_UPDATE_CLIENT);

    // Device Code authorities
    public static Authority CAN_VIEW_DEVICE_CODE = new Authority(AuthorityConstants.CAN_VIEW_DEVICE_CODE);

    // Domain authorities
    public static Authority CAN_VIEW_DOMAIN = new Authority(AuthorityConstants.CAN_VIEW_DOMAIN);
    public static Authority CAN_SEARCH_DOMAINS = new Authority(AuthorityConstants.CAN_SEARCH_DOMAINS);
    public static Authority CAN_CREATE_DOMAIN = new Authority(AuthorityConstants.CAN_CREATE_DOMAIN);

    // Register authorities
    public static Authority CAN_REGISTER_RESOURCE_SERVER = new Authority(AuthorityConstants.CAN_REGISTER_RESOURCE_SERVER);

    // Resource Server authorities
    public static Authority CAN_SEARCH_RESOURCE_SERVERS = new Authority(AuthorityConstants.CAN_SEARCH_RESOURCE_SERVERS);
    public static Authority CAN_CREATE_RESOURCE_SERVER = new Authority(AuthorityConstants.CAN_CREATE_RESOURCE_SERVER);

    // Role authorities
    public static Authority CAN_VIEW_ROLE = new Authority(AuthorityConstants.CAN_VIEW_ROLE);
    public static Authority CAN_SEARCH_ROLES = new Authority(AuthorityConstants.CAN_SEARCH_ROLES);
    public static Authority CAN_CREATE_ROLE = new Authority(AuthorityConstants.CAN_CREATE_ROLE);
    public static Authority CAN_UPDATE_ROLE = new Authority(AuthorityConstants.CAN_UPDATE_ROLE);

    // Scope authorities
    public static Authority CAN_VIEW_SCOPE = new Authority(AuthorityConstants.CAN_VIEW_SCOPE);
    public static Authority CAN_SEARCH_SCOPES = new Authority(AuthorityConstants.CAN_SEARCH_SCOPES);
    public static Authority CAN_CREATE_SCOPE = new Authority(AuthorityConstants.CAN_CREATE_SCOPE);
    public static Authority CAN_UPDATE_SCOPE = new Authority(AuthorityConstants.CAN_UPDATE_SCOPE);

    // User authorities
    public static Authority CAN_VIEW_USER = new Authority(AuthorityConstants.CAN_VIEW_USER);
    public static Authority CAN_SEARCH_USERS = new Authority(AuthorityConstants.CAN_SEARCH_USERS);
    public static Authority CAN_CREATE_USER = new Authority(AuthorityConstants.CAN_CREATE_USER);
    public static Authority CAN_UPDATE_USER = new Authority(AuthorityConstants.CAN_UPDATE_USER);
    public static Authority CAN_VERIFY_USER_EMAIL = new Authority(AuthorityConstants.CAN_VERIFY_USER_EMAIL);
    public static Authority CAN_CHANGE_USER_PASSWORD = new Authority(AuthorityConstants.CAN_CHANGE_USER_PASSWORD);

    public static List<Authority> getAllAuthorities() {
        var authorities = new ArrayList<Authority>();

        // Authority authorities
        authorities.add(CAN_VIEW_AUTHORITY);
        authorities.add(CAN_SEARCH_AUTHORITIES);
        authorities.add(CAN_CREATE_AUTHORITY);

        // Client authorities
        authorities.add(CAN_VIEW_CLIENT);
        authorities.add(CAN_SEARCH_CLIENTS);
        authorities.add(CAN_CREATE_CLIENT);
        authorities.add(CAN_UPDATE_CLIENT);

        // Device Code authorities
        authorities.add(CAN_VIEW_DEVICE_CODE);

        // Domain authorities
        authorities.add(CAN_VIEW_DOMAIN);
        authorities.add(CAN_SEARCH_DOMAINS);
        authorities.add(CAN_CREATE_DOMAIN);

        // Register authorities
        authorities.add(CAN_REGISTER_RESOURCE_SERVER);

        // Resource Server authorities
        authorities.add(CAN_SEARCH_RESOURCE_SERVERS);
        authorities.add(CAN_CREATE_RESOURCE_SERVER);

        // Role authorities
        authorities.add(CAN_VIEW_ROLE);
        authorities.add(CAN_SEARCH_ROLES);
        authorities.add(CAN_CREATE_ROLE);
        authorities.add(CAN_UPDATE_ROLE);

        // Scope authorities
        authorities.add(CAN_VIEW_SCOPE);
        authorities.add(CAN_SEARCH_SCOPES);
        authorities.add(CAN_CREATE_SCOPE);
        authorities.add(CAN_UPDATE_SCOPE);

        // User authorities
        authorities.add(CAN_VIEW_USER);
        authorities.add(CAN_SEARCH_USERS);
        authorities.add(CAN_CREATE_USER);
        authorities.add(CAN_UPDATE_USER);
        authorities.add(CAN_VERIFY_USER_EMAIL);
        authorities.add(CAN_CHANGE_USER_PASSWORD);

        return authorities;
    }

}
