package com.gymplus.core;

public enum Settings {
    ;

    @ConfigurableParameter(value= "Vault URL")
    public static String VAULT_URL;

    @ConfigurableParameter(value= "APP Role ID")
    public static String VAULT_APPROLE_ID;

    @ConfigurableParameter(value= "APP Role Secret ID Gen Path")
    public static String VAULT_APPROLE_GEN_PATH;

    @ConfigurableParameter(value= "Vault Secrets Path")
    public static String VAULT_SECRETS_PATH;

    @ConfigurableParameter(value = "idm client id")
    public static String CLIENT_ID;

    @ConfigurableParameter(value = "idm client password")
    public static String CLIENT_PASSWORD;

    @ConfigurableParameter(value = "asms specific idm client credentials")
    public static String CLIENT_CREDENTIALS;

    @ConfigurableParameter(value = "api version of azure")
    public static String API_VERSION;

    @ConfigurableParameter(value = "app id in azure")
    public static String APP_ID;

    @ConfigurableParameter(value = "app key in azure")
    public static String APP_KEY;

    @ConfigurableParameter(value = "idm admin login")
    public static String ADMIN_LOGIN;

    @ConfigurableParameter(value = "idm admin password")
    public static String ADMIN_PASSWORD;

    @ConfigurableParameter(value = "azure tenant")
    public static String TENANT;

    @ConfigurableParameter(value = "url of azure graph api")
    public static String GRAPH_URL;

    @ConfigurableParameter(value = "orl of oauth service in azure")
    public static String OAUTH_URL;

    @ConfigurableParameter(value = "naproxy.gm.com")
    public static String PROXY_HOST;

    @ConfigurableParameter(value = "8080")
    public static String PROXY_PORT;

    @ConfigurableParameter(value = "OC getProfile service client id")
    public static String PROFILE_SERVICE_CLIENT_ID;

    @ConfigurableParameter(value = "OC getProfile service client secret", encrypted = true)
    public static String PROFILE_SERVICE_SECRET;

    @ConfigurableParameter(value = "OC getProfile service url")
    public static String PROFILE_SERVICE_URL;

    @ConfigurableParameter(value = "Prefix for extension fields")
    public static String EXTENSION_PREFIX;

    @ConfigurableParameter(value = "locale to use for profile service")
    public static String PROFILE_SERVICE_LOCALE;

    @ConfigurableParameter(value = "Temp password expiration period (days)")
    public static String PASSWORD_EXPIRATION_PERIOD;

    @ConfigurableParameter(value = "Commit number")
    public static String VERSION;

    @ConfigurableParameter(value = "Number of retry attempts to read user from Azure")
    public static String READ_USER_RETRY_COUNT = "0";

    @ConfigurableParameter(value = "Retry interval for readUser in seconds")
    public static String READ_USER_RETRY_INTERVAL = "1";

    @ConfigurableParameter(value = "GM Hyperscale Redis Master Node 1")
    public static String REDIS_MASTER_NODE1;

    @ConfigurableParameter(value = "GM Hyperscale Redis Master Node 2")
    public static String REDIS_MASTER_NODE2;

    @ConfigurableParameter(value = "GM Hyperscale Redis Master Node 3")
    public static String REDIS_MASTER_NODE3;

    @ConfigurableParameter(value = "GM Hyperscale requirepass value from client.conf")
    public static String REDIS_PASS;

    @ConfigurableParameter(value = "GM Hyperscale redis connection at startup, retry interval in seconds")
    public static String REDIS_AUTO_CONNECT_RETRY_INTERVAL = "30";

    @ConfigurableParameter(value = "Allow temporary support for legacy credentials")
    public static String ALLOW_GENERIC_CREDENTIALS;


}
