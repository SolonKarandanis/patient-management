package com.pm.authservice.infrastructure.web.controller;

import com.pm.authservice.infrastructure.config.i18n.Translate;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import com.pm.authservice.infrastructure.search.SearchService;
import com.pm.authservice.infrastructure.util.AppConstants;
import com.pm.authservice.infrastructure.util.HttpUtil;
import com.pm.authservice.infrastructure.web.dto.ChangePasswordDTO;
import com.pm.authservice.infrastructure.web.dto.SearchResults;
import com.pm.authservice.infrastructure.web.dto.UpdateUserDTO;
import com.pm.authservice.infrastructure.web.dto.UserDTO;
import com.pm.authservice.infrastructure.web.dto.UserDetailsDTO;
import com.pm.authservice.infrastructure.web.dto.UsersSearchRequestDTO;
import com.pm.authservice.infrastructure.web.exception.AuthException;
import com.pm.authservice.infrastructure.web.exception.BusinessException;
import com.pm.authservice.infrastructure.web.exception.NotFoundException;
import com.pm.authservice.infrastructure.web.export.UserCsvExporter;
import com.pm.authservice.infrastructure.application.UserLifecycleService;
import com.pm.authservice.infrastructure.application.UserQueryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserJpaRepository userRepository;
    private final UserLifecycleService lifecycleService;
    private final SearchService searchService;
    private final UserQueryService userQueryService;

    public UserController(
            UserJpaRepository userRepository,
            UserLifecycleService lifecycleService,
            SearchService searchService,
            UserQueryService userQueryService
    ) {
        this.userRepository = userRepository;
        this.lifecycleService = lifecycleService;
        this.searchService = searchService;
        this.userQueryService = userQueryService;
    }

    private UserJpaEntity findUserByPublicId(String publicId) {
        return userRepository.findByDomainId(UUID.fromString(publicId))
                .orElseThrow(() -> new NotFoundException("error.user.not.found"));
    }

    private UserJpaEntity resolveCurrentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("error.user.not.found"));
        }
        UserDetailsDTO dto = (UserDetailsDTO) authentication.getPrincipal();
        assert dto != null;
        return findUserByPublicId(dto.getPublicId());
    }

    @PostMapping("/export/csv")
    public void exportUsersToCsv(
            @RequestBody @Valid UsersSearchRequestDTO searchObj,
            HttpServletResponse response,
            Authentication authentication) throws Exception {
        UserJpaEntity user = resolveCurrentUser(authentication);
        Long resultsCount = searchService.countUsers(searchObj, user);
        log.info("UserController --> exportUsersToCsv --> results: {}", resultsCount);
        if (resultsCount >= AppConstants.MAX_RESULTS_CSV_EXPORT) {
            throw new BusinessException("error.max.csv.results");
        }
        response.setContentType(HttpUtil.MEDIA_TYPE_CSV);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users-results.csv\"");
        List<UserDTO> results = searchService.findUsersForExport(searchObj, user);
        UserCsvExporter exporter = new UserCsvExporter(results, response);
        exporter.exportData();
    }

    @PostMapping("/search")
    @Translate(path = "list[*].status", targetProperty = "statusLabel")
    public ResponseEntity<SearchResults<UserDTO>> findAllUsers(@RequestBody @Valid UsersSearchRequestDTO searchObj,
                                                               Authentication authentication) throws AuthException {
        log.info("[NEW SEARCH] -- UserController, method findAllUsers");
        UserJpaEntity user = resolveCurrentUser(authentication);
        log.info("In UserController with user: {}", user.getUsername());
        SearchResults<UserDTO> results = searchService.advancedSearchUsers(searchObj, user);
        return ResponseEntity.ok().body(results);
    }

    @GetMapping("/search")
    @Translate(path = "list[*].status", targetProperty = "statusLabel")
    public ResponseEntity<SearchResults<UserDTO>> quickSearchUSers(
            @RequestParam String value, @RequestParam Integer page,
            @RequestParam Integer size, @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder, Authentication authentication) throws AuthException {
        log.info("[NEW SEARCH] -- UserController, method findAllUsers");
        UserJpaEntity user = resolveCurrentUser(authentication);
        log.info("In UserController with user: {}", user.getUsername());
        SearchResults<UserDTO> results = searchService.quickSearchUsers(value, user, page, size, sortField, sortOrder);
        return ResponseEntity.ok().body(results);
    }

    @GetMapping("/{id}")
    @Translate(path = "status", targetProperty = "statusLabel")
    @Translate(path = "roles[*].name", targetProperty = "nameLabel")
    public ResponseEntity<UserDTO> viewUser(@PathVariable(name = "id", required = true) String publicId) {
        log.info("UserController --> viewUser --> publicId: {}", publicId);
        return ResponseEntity.ok(userQueryService.viewUser(publicId));
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<String>> getUserPermissions(@PathVariable(name = "id", required = true) String publicId) {
        return ResponseEntity.ok(userQueryService.getUserPermissions(publicId));
    }

    @GetMapping(value = "/account")
    @Translate(path = "status", targetProperty = "statusLabel")
    @Translate(path = "roles[*].name", targetProperty = "nameLabel")
    public ResponseEntity<UserDTO> getUserByToken(Authentication authentication) {
        UserJpaEntity user = resolveCurrentUser(authentication);
        log.info("UserController --> getUserByToken --> publicId: {}", user.getDomainId());
        return ResponseEntity.ok(userQueryService.viewUser(user.getDomainId().toString()));
    }

    @PreAuthorize("isSystemAdmin() || isUserMe(#publicId)")
    @PutMapping("/{id}")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable(name = "id", required = true) String publicId,
            @RequestBody @Valid UpdateUserDTO user) throws NotFoundException {
        log.info("UserController->updateUser");
        return ResponseEntity.ok(lifecycleService.updateUser(publicId, user));
    }

    @PreAuthorize("isSystemAdmin()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") String publicId)
            throws NotFoundException {
        log.info("UserController->deleteUser->publicId: {}", publicId);
        lifecycleService.deleteUser(publicId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("isSystemAdmin() || isUserMe(#publicId)")
    @PutMapping("/{id}/password-change")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> changeUserPassword(
            @PathVariable(name = "id", required = true) String publicId,
            @RequestBody @Valid ChangePasswordDTO request)
            throws NotFoundException, BusinessException {
        log.info("UserController->changeUserPassword->publicId: {}", publicId);
        return ResponseEntity.ok(lifecycleService.changePassword(publicId, request));
    }

    @PreAuthorize("isSystemAdmin()")
    @PutMapping("/{id}/activate")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> activateUser(
            @PathVariable(name = "id", required = true) String publicId)
            throws NotFoundException, BusinessException {
        log.info("UserController->activateUser->publicId: {}", publicId);
        return ResponseEntity.ok(lifecycleService.activateUser(publicId));
    }

    @PreAuthorize("isSystemAdmin()")
    @PutMapping("/{id}/deactivate")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> deactivateUser(
            @PathVariable(name = "id", required = true) String publicId)
            throws NotFoundException, BusinessException {
        log.info("UserController->deactivateUser->publicId: {}", publicId);
        return ResponseEntity.ok(lifecycleService.deactivateUser(publicId));
    }
}
