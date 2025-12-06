package co.retaila.athena.common.constants;

import java.util.List;

public class AuthorityConstants {

    public static final String AUTH_PREFIX = "hasAuthority('";
    public static final String AUTH_SUFFIX = "')";

    // Authority authorities
    public static final String CAN_VIEW_AUTHORITY = "can.view.authority";
    public static final String CAN_SEARCH_AUTHORITIES = "can.search.authorities";
    public static final String CAN_CREATE_AUTHORITY = "can.create.authority";

    // Client authorities
    public static final String CAN_VIEW_CLIENT = "can.view.client";
    public static final String CAN_SEARCH_CLIENTS = "can.search.clients";
    public static final String CAN_CREATE_CLIENT = "can.create.client";
    public static final String CAN_UPDATE_CLIENT = "can.update.client";

    // Device Code authorities
    public static final String CAN_VIEW_DEVICE_CODE = "can.view.device.code";

    // Domain authorities
    public static final String CAN_VIEW_DOMAIN = "can.view.domain";
    public static final String CAN_SEARCH_DOMAINS = "can.search.domains";
    public static final String CAN_CREATE_DOMAIN = "can.create.domain";

    // Register authorities
    public static final String CAN_REGISTER_RESOURCE_SERVER = "can.register.resource.server";

    // Resource Server authorities
    public static final String CAN_SEARCH_RESOURCE_SERVERS = "can.search.resource.servers";
    public static final String CAN_CREATE_RESOURCE_SERVER = "can.create.resource.server";

    // Role authorities
    public static final String CAN_VIEW_ROLE = "can.view.role";
    public static final String CAN_SEARCH_ROLES = "can.search.roles";
    public static final String CAN_CREATE_ROLE = "can.create.role";
    public static final String CAN_UPDATE_ROLE = "can.update.role";

    // Scope authorities
    public static final String CAN_VIEW_SCOPE = "can.view.scope";
    public static final String CAN_SEARCH_SCOPES = "can.search.scopes";
    public static final String CAN_CREATE_SCOPE = "can.create.scope";
    public static final String CAN_UPDATE_SCOPE = "can.update.scope";

    // User authorities
    public static final String CAN_VIEW_USER = "can.view.user";
    public static final String CAN_SEARCH_USERS = "can.search.users";
    public static final String CAN_CREATE_USER = "can.create.user";
    public static final String CAN_UPDATE_USER = "can.update.user";
    public static final String CAN_VERIFY_USER_EMAIL = "can.verify.user.email";
    public static final String CAN_CHANGE_USER_PASSWORD = "can.change.user.password";

    public static List<String> getFirstPartyClientAuthorities() {
        return List.of(
                CAN_REGISTER_RESOURCE_SERVER,
                CAN_VIEW_USER,
                CAN_SEARCH_USERS,
                CAN_CREATE_USER,
                CAN_UPDATE_USER,
                CAN_VERIFY_USER_EMAIL,
                CAN_CHANGE_USER_PASSWORD
        );
    }

}
