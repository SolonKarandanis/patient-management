package com.pm.medicalrecordsservice.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface for managing medical records.
 */
public interface MedicalRecordService {

    /**
     * Stores a file in GridFS and its metadata in a separate collection.
     *
     * @param file      The file to store.
     * @param patientId The ID of the patient associated with the file.
     * @return The ID of the stored metadata.
     * @throws IOException If an I/O error occurs.
     */
    String storeFile(MultipartFile file, String patientId) throws IOException;

    /**
     * Retrieves a file from GridFS.
     *
     * @param fileId The ID of the file to retrieve.
     * @return The GridFS file, or null if not found.
     */
    GridFSFile getFile(String fileId);
}
