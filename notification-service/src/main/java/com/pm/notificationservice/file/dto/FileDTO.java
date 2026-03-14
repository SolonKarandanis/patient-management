package com.pm.notificationservice.file.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileDTO {
    private String fileId;
    private String fullPath;
    private Long dateLastModified;
    private Long fileSize;

    public FileDTO(String fileId, String fullPath, Long dateLastModified, Long fileSize) {
        this.fileId = fileId;
        this.fullPath = fullPath;
        this.dateLastModified = dateLastModified;
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return fileId + "," + fullPath + "," + dateLastModified + "," + fileSize;
    }

}
