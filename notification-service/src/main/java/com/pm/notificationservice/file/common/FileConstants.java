package com.pm.notificationservice.file.common;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

public class FileConstants {
    public static final String EMAIL_ATTACHMENT = "EMAIL_ATTACHMENT";
    public static final String UNZIPPED_FILE = "UNZIPPED_FILE";

    public static final Map<String,String> fileUsages = new HashedMap();

    static {
        fileUsages.put("catalogues", "file_reference_id");
        fileUsages.put("dossiers", "dossiers");
    }
}
