package com.pm.authservice.util;

public class AppConstants {
    private AppConstants() {}

    public static final String UTF_8 = "UTF-8";
    public static final String HEADER_NAME_LANGUAGE_ISO = "Lang-ISO";
    public static final String APPLICATION_BUNDLE = "application_messages";
    public static final String VALIDATION_BUNDLE = "messages";

    public static final Integer MAX_RESULTS_CSV_EXPORT=10000;

    public static final String EMAIL_TOPIC = "email";

    public static final String STATUS_ACTIVE = "account.active";
    public static final String STATUS_ALL = "status.all";

    public static final String SEARCH_TYPE_AND = "search.type.and";
    public static final String SEARCH_TYPE_OR = "search.type.or";

    public static final String OUTBOX_USER_CREATED="UserCreated";
    public static final String OUTBOX_USER_UPDATED="UserUpdated";
    public static final String OUTBOX_USER_ACTIVATED="UserActivated";
    public static final String OUTBOX_USER_DEACTIVATED="UserDeactivated";
    public static final String OUTBOX_USER_DELETED="UserDeleted";
    public static final String OUTBOX_USER_VERIFIED="UserVerified";


    public static final String ELASTIC_SEARCH_INDEXING_METHOD_OUTBOX="elastic.search.indexing.outbox";
    public static final String ELASTIC_SEARCH_INDEXING_METHOD_HTTP="elastic.search.indexing.http";
    public static final String ELASTIC_SEARCH_INDEXING_METHOD_BROKER="elastic.search.indexing.broker";
}
