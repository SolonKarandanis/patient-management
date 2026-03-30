package com.pm.authservice.service.fts;

import com.pm.authservice.util.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FtsUtil {
    protected FtsUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ES_FIELD_TYPE_KEYWORD = ".keyword";

    public static final String ES_USER_FIELD_ID = "id";
    public static final String ES_USER_FIELD_PUBLIC_ID = "publicId";
    public static final String ES_USER_FIELD_USERNAME = "username";
    public static final String ES_USER_FIELD_FIRST_NAME = "firstName";
    public static final String ES_USER_FIELD_LAST_NAME = "lastName";
    public static final String ES_USER_FIELD_EMAIL = "email";
    public static final String ES_USER_FIELD_STATUS = "status";
    public static final String ES_USER_FIELD_IS_ENABLED= "isEnabled";
    public static final String ES_USER_FIELD_IS_VERIFIED = "isVerified";
    public static final String ES_USER_FIELD_ROLE_NAMES="rolesNames";


    protected static final Map<String, String> userColumnsForFTSSortOrGroupOps = new HashMap<>();
    static {
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_ID, ES_USER_FIELD_ID);
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_PUBLIC_ID, ES_USER_FIELD_PUBLIC_ID);
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_USERNAME, ES_USER_FIELD_USERNAME);
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_FIRST_NAME,
                ES_USER_FIELD_FIRST_NAME + ES_FIELD_TYPE_KEYWORD);
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_LAST_NAME,
                ES_USER_FIELD_LAST_NAME + ES_FIELD_TYPE_KEYWORD);
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_EMAIL,
                ES_USER_FIELD_EMAIL + ES_FIELD_TYPE_KEYWORD);
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_STATUS, ES_USER_FIELD_STATUS);
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_IS_ENABLED, ES_USER_FIELD_IS_ENABLED);
        userColumnsForFTSSortOrGroupOps.put(ES_USER_FIELD_IS_VERIFIED, ES_USER_FIELD_IS_VERIFIED);
    }

    public static Map<String, String> getUserColumnsForFTSSortOrGroupOps() {
        return Map.copyOf(userColumnsForFTSSortOrGroupOps);
    }

    private static final List<String> permittedSearchUsersStatusValues = new ArrayList<>();
    static {
        permittedSearchUsersStatusValues.add(AppConstants.STATUS_ACTIVE);
        permittedSearchUsersStatusValues.add(AppConstants.STATUS_ALL);
    }

    public static List<String> getPermittedSearchUsersStatusValues() {
        return List.copyOf(permittedSearchUsersStatusValues);
    }

    private static final List<String> permittedSearchUsersSearchMethodValues = new ArrayList<>();
    static {
        permittedSearchUsersSearchMethodValues.add(AppConstants.SEARCH_TYPE_AND);
        permittedSearchUsersSearchMethodValues.add(AppConstants.SEARCH_TYPE_OR);
    }

    public static List<String> getPermittedSearchUsersSearchMethodValues() {
        return List.copyOf(permittedSearchUsersSearchMethodValues);
    }

}
