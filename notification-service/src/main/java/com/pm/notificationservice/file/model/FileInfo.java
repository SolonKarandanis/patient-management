package com.pm.notificationservice.file.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "file_info")
@Getter
@Setter
public class FileInfo {

    public static final String SIGNATURE = "SIGNATURE";

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @SequenceGenerator(
            name = "FILIE_INFO_ID_GENERATOR",
            sequenceName = "file_info_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "FILIE_INFO_ID_GENERATOR"
    )
    private Long id = -1L;

    @Column(name = "FILE_REF_ID", nullable = false)
    private Long fileRefId;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FILE_USAGE", nullable = false)
    private String fileUsage;

    @Column(name = "FILE_MIME_TYPE", nullable = false)
    private String mimeType;

    @Column(name = "FILE_SIZE", nullable = false)
    private Integer fileSize;

    public FileInfo() {
    }

    public FileInfo(String fileName, String mimeType, String fileUsage, Integer fileSize) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileUsage = fileUsage;
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.lineSeparator();

        result.append(this.getClass().getName()).append(" Object {").append(newLine);
        result.append("ID: ").append(this.id);
        result.append(newLine);
        result.append("}");

        return result.toString();
    }
}
