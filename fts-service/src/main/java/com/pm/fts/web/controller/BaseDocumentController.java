package com.pm.fts.web.controller;

import com.pm.fts.exception.FtsEsException;
import com.pm.fts.service.BaseDocumentService;
import com.pm.fts.web.dto.DeleteDocumentsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class BaseDocumentController {

    @Autowired
    @Qualifier("baseDocumentService")
    private BaseDocumentService baseService;

    @DeleteMapping
    public ResponseEntity<Void> deleteActiveItemIndex() throws FtsEsException {
        baseService.deleteDocumentIndex();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/byIds")
    public ResponseEntity<Void> deleteByIds(@RequestBody DeleteDocumentsRequest request){
        baseService.deleteByIds(request.getDocumentIds());
        return ResponseEntity.ok().build();
    }
}
