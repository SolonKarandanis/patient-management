package com.pm.fts.service.user;

import com.pm.fts.document.user.UserDocument;
import com.pm.fts.repository.user.UserDocumentCustomRepositoryImpl;
import com.pm.fts.repository.user.UserDocumentRepository;
import com.pm.fts.service.BaseDocumentServiceBean;
import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.UserDocumentDTO;
import com.pm.fts.web.dto.UserDocumentSearchResultsDTO;
import com.pm.fts.web.dto.UserSearchResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDocumentServiceBean extends BaseDocumentServiceBean implements UserDocumentService{
    private static final String USER_INDEX = "auth_service_users";

    @Override
    protected String getIndex() {
        return getIndexPrefix() + USER_INDEX;
    }

    protected final UserDocumentRepository userRepo;
    protected final UserDocumentCustomRepositoryImpl userDocumentCustomRepositoryImpl;

    public UserDocumentServiceBean(UserDocumentRepository userRepo, UserDocumentCustomRepositoryImpl userDocumentCustomRepositoryImpl) {
        this.userRepo = userRepo;
        this.userDocumentCustomRepositoryImpl = userDocumentCustomRepositoryImpl;
    }

    @Override
    public void saveAllUsers(List<UserDocument> items) {
        userRepo.saveAll(items);
    }

    @Override
    public UserDocument convertToDocument(UserDocumentDTO dto) {
        UserDocument document = new UserDocument(super.convertToDocument(dto));
        document.setUsername(dto.getUsername());
        document.setFirstName(dto.getFirstName());
        document.setLastName(dto.getLastName());
        return document;
    }

    @Override
    public List<UserDocument> convertToDocuments(List<UserDocumentDTO> dtoList) {
        return List.of();
    }

    @Override
    public UserSearchResponseDTO search(DocumentSearchRequest payload) throws Exception {
        return null;
    }

    @Override
    public Long countItems(DocumentSearchRequest payload) throws Exception {
        return 0L;
    }

    @Override
    public List<UserDocumentSearchResultsDTO> findDocumentItems(DocumentSearchRequest payload) throws Exception {
        return List.of();
    }
}
