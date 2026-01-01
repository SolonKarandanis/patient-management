package com.pm.medicalrecordsservice.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.pm.medicalrecordsservice.model.GeoLocation;
import com.pm.medicalrecordsservice.model.MedicalRecordMetadata;
import com.pm.medicalrecordsservice.repository.MedicalRecordMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service("medicalRecordService")
public class MedicalRecordServiceBean implements MedicalRecordService {

    private final GridFsTemplate gridFsTemplate;
    private final MedicalRecordMetadataRepository metadataRepository;

    @Autowired
    public MedicalRecordServiceBean(
            GridFsTemplate gridFsTemplate,
            MedicalRecordMetadataRepository metadataRepository
    ) {
        this.gridFsTemplate = gridFsTemplate;
        this.metadataRepository = metadataRepository;
    }

    @Override
    public String storeFile(MultipartFile file, String patientId, GeoLocation location) throws IOException {
        InputStream inputStream = file.getInputStream();
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        // Store the file in GridFS
        Object fileId = gridFsTemplate.store(inputStream, filename, contentType);

        // Create and save the metadata
        MedicalRecordMetadata metadata = new MedicalRecordMetadata();
        metadata.setPatientId(patientId);
        metadata.setFilename(filename);
        metadata.setContentType(contentType);
        metadata.setSize(file.getSize());
        metadata.setUploadDate(LocalDateTime.now());
        metadata.setFileId(fileId.toString());
        metadata.setLocation(location);

        MedicalRecordMetadata savedMetadata = metadataRepository.save(metadata);
        return savedMetadata.getId();
    }

    @Override
    public GridFSFile getFile(String fileId) {
        return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
    }
}
