package com.pm.fts.web.controller;

import com.pm.fts.document.user.UserDocument;
import com.pm.fts.service.user.UserDocumentService;
import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.UserDocumentDTO;
import com.pm.fts.web.dto.UserDocumentSearchResultsDTO;
import com.pm.fts.web.dto.UserSearchResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserDocumentController extends BaseDocumentController{

    @Autowired
    private UserDocumentService userDocumentService;

    @PostMapping
    public ResponseEntity<Void> indexActiveItems(@RequestBody List<UserDocumentDTO> activeItems) {
        List<UserDocument> documents = userDocumentService.convertToDocuments(activeItems);
        userDocumentService.saveAllUsers(documents);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<UserSearchResponseDTO> search(@RequestBody @Valid final DocumentSearchRequest payload) throws Exception {
        UserSearchResponseDTO result = userDocumentService.search(payload);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/search/export")
    public ResponseEntity<List<UserDocumentSearchResultsDTO>> findActiveItems(@RequestBody @Valid final DocumentSearchRequest payload) throws Exception {
        return new ResponseEntity<>(userDocumentService.findDocumentUsers(payload), HttpStatus.OK);

    }

    @PostMapping("/search/count")
    public ResponseEntity<Long> countItems(@RequestBody @Valid final DocumentSearchRequest payload) throws Exception {
        Long result = userDocumentService.countItems(payload);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
