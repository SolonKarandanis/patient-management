package com.pm.authservice.service.fts;

import com.pm.authservice.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service("userFullTextSearchService")
@Slf4j
public class UserFullTextSearchServiceBean implements UserFullTextSearchService{

    protected static final String RESPONSE_STATUS = "Response Status from ccm-fts: {}";

    @Value("${fts.host.protocol}")
    private String ftsProtocol;

    @Value("${fts.host}")
    private String ftsHost;

    @Value("${fts.host.port}")
    private String ftsPort;

    @Value("${fts.web.context}")
    private String ftsContext;

    @Autowired
    private RestTemplate restTemplate;

    protected String getEndpoint() {
        return (new StringBuilder()).append(ftsProtocol).append("://").append(ftsHost).append(":").append(ftsPort)
                .append("/").append(ftsContext).toString();
    }

    protected HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public UserSearchResponseDTO searchUsers(DocumentSearchRequest payload) throws ResourceAccessException {
        HttpHeaders headers = getDefaultHeaders();
        HttpEntity<DocumentSearchRequest> request = new HttpEntity<>(payload, headers);
        return restTemplate.postForObject(getEndpoint() + "/users/search", request, UserSearchResponseDTO.class);
    }

    @Override
    public Long countUsers(DocumentSearchRequest payload) throws ResourceAccessException {
        HttpHeaders headers = getDefaultHeaders();
        HttpEntity<DocumentSearchRequest> request = new HttpEntity<>(payload, headers);
        return restTemplate.postForObject(getEndpoint() + "/users/search/count", request, Long.class);
    }

    @Override
    public List<UserDocumentSearchResultsDTO> findUsers(DocumentSearchRequest payload) throws ResourceAccessException {
        HttpHeaders headers = getDefaultHeaders();
        HttpEntity<DocumentSearchRequest> request = new HttpEntity<>(payload, headers);
        UserDocumentSearchResultsDTO[] result = restTemplate.postForObject(getEndpoint() + "/items/search/export", request, UserDocumentSearchResultsDTO[].class);
        return List.of(result);
    }

    @Override
    public Boolean indexUsers(List<UserDocumentDTO> documents) throws ResourceAccessException {
        HttpHeaders headers = getDefaultHeaders();
        HttpEntity<List<UserDocumentDTO>> request = new HttpEntity<>(documents, headers);
        ResponseEntity<Void> response = restTemplate.exchange(getEndpoint() + "/users", HttpMethod.POST, request,
                Void.class);
        logResponse(response);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Boolean deleteUsersByIds(List<Integer> itemIds) throws ResourceAccessException {
        HttpEntity<DeleteDocumentsRequest> request = new HttpEntity<>(new DeleteDocumentsRequest(itemIds), getDefaultHeaders());
        ResponseEntity<Void> response = restTemplate.exchange(getEndpoint() + "/users/byIds", HttpMethod.DELETE, request, Void.class);
        logResponse(response);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Boolean deleteUserIndex() throws ResourceAccessException {
        HttpEntity<Integer> request = new HttpEntity<>(getDefaultHeaders());
        ResponseEntity<Void> response = restTemplate.exchange(getEndpoint() + "/users", HttpMethod.DELETE, request, Void.class);
        logResponse(response);
        return response.getStatusCode().is2xxSuccessful();
    }

    protected void logResponse(ResponseEntity<Void> response){
        log.debug(RESPONSE_STATUS, response.getStatusCode());
    }
}
