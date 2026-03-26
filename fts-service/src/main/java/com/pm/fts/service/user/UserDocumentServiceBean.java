package com.pm.fts.service.user;

import com.pm.fts.document.user.UserDocument;
import com.pm.fts.repository.user.UserDocumentCustomRepositoryImpl;
import com.pm.fts.repository.user.UserDocumentRepository;
import com.pm.fts.service.BaseDocumentServiceBean;
import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.UserDocumentDTO;
import com.pm.fts.web.dto.UserDocumentSearchResultsDTO;
import com.pm.fts.web.dto.UserSearchResponseDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        document.setEmail(dto.getEmail());
        document.setStatus(dto.getStatus());
        document.setIsEnabled(dto.getIsEnabled());
        document.setIsVerified(dto.getIsVerified());
        document.setRoleIds(dto.getRoleIds());
        document.setRolesNames(dto.getRolesNames());
        return document;
    }

    @Override
    public List<UserDocument> convertToDocuments(List<UserDocumentDTO> dtoList) {
        List<UserDocument> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(dtoList)) {
            return result;
        }
        for (UserDocumentDTO dto : dtoList) {
            result.add(convertToDocument(dto));
        }
        return result;
    }

    @Override
    public UserSearchResponseDTO search(@Valid DocumentSearchRequest payload) throws Exception {
        if (Objects.equals(DocumentSearchRequest.Type.QUICK, payload.getType())) {
            return userDocumentCustomRepositoryImpl.quickSearch(payload);
        } else {
            return userDocumentCustomRepositoryImpl.advancedSearch(payload);
        }
    }

    @Override
    public Long countItems(DocumentSearchRequest payload) throws Exception {
        return userDocumentCustomRepositoryImpl.countItems(payload);
    }

    @Override
    public List<UserDocumentSearchResultsDTO> findDocumentUsers(DocumentSearchRequest payload) throws Exception {
        return userDocumentCustomRepositoryImpl.findDocumentItems(payload).getContent();
    }
}
