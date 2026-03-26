package com.pm.fts.service;

import com.pm.fts.document.AbstractDocument;
import com.pm.fts.exception.FtsEsException;
import com.pm.fts.web.dto.AbstractDocumentDTO;

import java.util.List;

public interface BaseDocumentService {
    AbstractDocument  convertToDocument(AbstractDocumentDTO dto);

    void deleteByIds(List<Integer> itemIds);

    Boolean deleteDocumentIndex() throws FtsEsException;
}
