package com.pm.authservice.util;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


public class HttpUtil {

    public static final String MEDIA_TYPE_XLS = "application/vnd.ms-excel";
    public static final String MEDIA_TYPE_CSV = "text/csv";
    public static final String MEDIA_TYPE_OPENXML_DOC = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MEDIA_TYPE_OPENXML_SHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static ResponseEntity<byte[]> getByteArrayResponseFromFile(String fileName, String fileType, byte[] fileData) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, toContentDisposition(fileName))
                .contentType(MediaType.parseMediaType(fileType)).body(fileData);
    }

    public static ResponseEntity<byte[]> getByteArrayResponseFromFile(String fileName, MediaType mediaType,byte[] fileData) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, toContentDisposition(fileName))
                .contentType(mediaType).body(fileData);
    }

    public static HttpHeaders getHeadersForFile(String fileName, String contentType) {
        HttpHeaders headers = new HttpHeaders();
        String contentDisposition = toContentDisposition(fileName);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        headers.set(HttpHeaders.CONTENT_TYPE, contentType);
        return headers;
    }

    public static String toContentDisposition(String fileName) {
        return "attachment; filename=\"" + fileName + "\"";
    }
}
