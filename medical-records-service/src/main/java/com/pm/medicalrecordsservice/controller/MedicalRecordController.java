package com.pm.medicalrecordsservice.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.pm.medicalrecordsservice.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/records")
public class MedicalRecordController
{

    private final MedicalRecordService medicalRecordService;
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService, GridFsTemplate gridFsTemplate) {
        this.medicalRecordService = medicalRecordService;
        this.gridFsTemplate = gridFsTemplate;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestBody MedicalRecordUploadRequest request) {
        try {
            Point location = request.toPoint();
            String fileId = medicalRecordService.storeFile(file, request.getPatientId(), location);
            return ResponseEntity.ok("File uploaded successfully with ID: " + fileId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileId) {
        GridFSFile gridFSFile = medicalRecordService.getFile(fileId);
        if (gridFSFile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        try {
            InputStreamResource resource = new InputStreamResource(gridFsTemplate.getResource(gridFSFile).getInputStream());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + gridFSFile.getFilename() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, gridFSFile.getMetadata().getString("contentType"));

            return ResponseEntity.ok()
                                 .headers(headers)
                                 .contentLength(gridFSFile.getLength())
                                 .contentType(MediaType.parseMediaType(gridFSFile.getMetadata().getString("contentType")))
                                 .body(resource);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing the file", e);
        }
    }
}
