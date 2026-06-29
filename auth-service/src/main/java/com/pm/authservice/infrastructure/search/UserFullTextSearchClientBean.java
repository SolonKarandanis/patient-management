package com.pm.authservice.infrastructure.search;

import com.pm.authservice.infrastructure.search.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service("userFullTextSearchClient")
@Slf4j
public class UserFullTextSearchClientBean implements UserFullTextSearchClient {

    protected static final String RESPONSE_STATUS = "Response Status from ccm-fts: {}";

    @Value("${fts.host.protocol}")
    private String ftsProtocol;

    @Value("${fts.host}")
    private String ftsHost;

    @Value("${fts.host.port}")
    private String ftsPort;

    @Value("${fts.web.context}")
    private String ftsContext;

    private final RestClient restClient;

    public UserFullTextSearchClientBean(RestClient restClient) {
        this.restClient = restClient;
    }

    protected String getEndpoint() {
        return ftsProtocol + "://" + ftsHost + ":" + ftsPort + "/" + ftsContext;
    }

    @Override
    public UserSearchResponseDTO searchUsers(DocumentSearchRequest payload) throws ResourceAccessException {
        return restClient.post()
                .uri(getEndpoint() + "/users/search")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(UserSearchResponseDTO.class);
    }

    @Override
    public Long countUsers(DocumentSearchRequest payload) throws ResourceAccessException {
        return restClient.post()
                .uri(getEndpoint() + "/users/search/count")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(Long.class);
    }

    @Override
    public List<UserDocumentSearchResultsDTO> findUsers(DocumentSearchRequest payload) throws ResourceAccessException {
        UserDocumentSearchResultsDTO[] result = restClient.post()
                .uri(getEndpoint() + "/items/search/export")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(UserDocumentSearchResultsDTO[].class);
        return result != null && result.length > 0 ? List.of(result) : List.of();
    }

    @Override
    public Boolean indexUsers(List<UserDocumentDTO> documents) throws ResourceAccessException {
        ResponseEntity<Void> response = restClient.post()
                .uri(getEndpoint() + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(documents)
                .retrieve()
                .toBodilessEntity();
        logResponse(response);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Boolean deleteUsersByIds(List<Integer> itemIds) throws ResourceAccessException {
        // DELETE with a request body requires method(HttpMethod.DELETE) — delete() has no body()
        ResponseEntity<Void> response = restClient.method(HttpMethod.DELETE)
                .uri(getEndpoint() + "/users/byIds")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DeleteDocumentsRequest(itemIds))
                .retrieve()
                .toBodilessEntity();
        logResponse(response);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Boolean deleteUserIndex() throws ResourceAccessException {
        ResponseEntity<Void> response = restClient.delete()
                .uri(getEndpoint() + "/users")
                .retrieve()
                .toBodilessEntity();
        logResponse(response);
        return response.getStatusCode().is2xxSuccessful();
    }

    protected void logResponse(ResponseEntity<Void> response) {
        log.debug(RESPONSE_STATUS, response.getStatusCode());
    }
}
