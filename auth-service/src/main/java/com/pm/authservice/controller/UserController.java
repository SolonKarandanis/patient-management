package com.pm.authservice.controller;

import com.pm.authservice.config.i18n.Translate;
import com.pm.authservice.dto.*;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.service.UserService;
import com.pm.authservice.util.AppConstants;
import com.pm.authservice.util.HttpUtil;
import com.pm.authservice.util.UserCsvExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService usersService;

    public UserController(UserService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/export/csv")
    public void exportUsersToCsv(
            @RequestBody @Valid UsersSearchRequestDTO searchObj,
            HttpServletResponse response,
            Authentication authentication) throws Exception{
        UserDetailsDTO dto = (UserDetailsDTO)authentication.getPrincipal();
        UserEntity user = usersService.findByPublicId(dto.getPublicId());
        Long resultsCount = usersService.countUsers(searchObj,user);
        log.info("UserController --> exportUsersToCsv --> results: {}", resultsCount);
        if (resultsCount >= AppConstants.MAX_RESULTS_CSV_EXPORT) {
            throw new BusinessException("error.max.csv.results");
        }
        response.setContentType(HttpUtil.MEDIA_TYPE_CSV);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users-results.csv\"");
        List<UserDTO> results =usersService.findAllUsersForExport(searchObj,user);
        UserCsvExporter exporter= new UserCsvExporter(results, response);
        exporter.exportData();
    }

    @PostMapping("/search")
    @Translate(path = "list[*].status", targetProperty = "statusLabel")
    public ResponseEntity<SearchResults<UserDTO>> findAllUsers(@RequestBody @Valid UsersSearchRequestDTO searchObj,
                                                               Authentication authentication){
        UserDetailsDTO dto = (UserDetailsDTO)authentication.getPrincipal();
        UserEntity user = usersService.findByPublicId(dto.getPublicId());
        Page<UserEntity> results = usersService.searchUsers(searchObj,user);
        List<UserDTO> dtos=usersService.convertToDTOList(results.getContent(),false);
        return ResponseEntity.ok().body(new SearchResults<UserDTO>(Math.toIntExact(results.getTotalElements()), dtos));
    }

    @GetMapping("/{id}")
    @Translate(path = "status", targetProperty = "statusLabel")
    @Translate(path = "roles[*].name", targetProperty = "nameLabel")
    public ResponseEntity<UserDTO> viewUser(@PathVariable(name= "id", required=true) String publicId)
            throws NotFoundException {
        UserEntity user = usersService.findByPublicId(publicId);
        log.info("UserController --> viewUser --> username: {}", user.getUsername());
        return ResponseEntity.ok(usersService.convertToDTO(user,true));
    }

    @GetMapping(value="/account")
    @Translate(path = "status", targetProperty = "statusLabel")
    @Translate(path = "roles[*].name", targetProperty = "nameLabel")
    public ResponseEntity<UserDTO> getUserByToken(Authentication authentication) throws NotFoundException{
        UserDetailsDTO dto = (UserDetailsDTO)authentication.getPrincipal();
        UserEntity user = usersService.findByPublicId(dto.getPublicId());
        log.info("UserController --> getUserByToken --> username: {}", user.getUsername());
        return ResponseEntity.ok(usersService.convertToDTO(user,true));
    }

    @PreAuthorize("isSystemAdmin() || isUserMe(#publicId)")
    @PutMapping("/{id}")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable(name = "id", required=true) String publicId,
            @RequestBody @Valid UpdateUserDTO user)throws NotFoundException{
        log.info("UserController->updateUser");
        UserEntity userToBeUpdated = usersService.findByPublicId(publicId);
        userToBeUpdated=usersService.updateUser(publicId,user);
        return ResponseEntity.ok(usersService.convertToDTO(userToBeUpdated,true));
    }

    @PreAuthorize("isSystemAdmin()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name= "id") String publicId)
            throws NotFoundException{
        log.info("UserController->deleteUser->publicId: {}" , publicId);
        usersService.deleteUser(publicId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("isSystemAdmin() || isUserMe(#publicId)")
    @PutMapping("/{id}/password-change")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> changeUserPassword(
            @PathVariable(name= "id", required=true) String publicId,
            @RequestBody @Valid ChangePasswordDTO request)
            throws NotFoundException, BusinessException{
        log.info("UserController->changeUserPassword->publicId: {}" , publicId);
        UserEntity user = usersService.findByPublicId(publicId);
        user= usersService.changePassword(user, request);
        return ResponseEntity.ok(usersService.convertToDTO(user,true));
    }

    @PreAuthorize("isSystemAdmin()")
    @PutMapping("/{id}/activate")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> activateUser(
            @PathVariable(name= "id", required=true) String publicId )
            throws NotFoundException, BusinessException{
        log.info("UserController->activateUser->publicId: {}" , publicId);
        UserEntity user = usersService.findByPublicId(publicId);
        user = usersService.activateUser(user);
        return ResponseEntity.ok(usersService.convertToDTO(user,true));
    }

    @PreAuthorize("isSystemAdmin()")
    @PutMapping("/{id}/deactivate")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> deactivateUser(
            @PathVariable(name= "id", required=true) String publicId )
            throws NotFoundException, BusinessException{
        log.info("UserController->deactivateUser->publicId: {}" , publicId);
        UserEntity user = usersService.findByPublicId(publicId);
        user = usersService.deactivateUser(user);
        return ResponseEntity.ok(usersService.convertToDTO(user,true));
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token)throws BusinessException{
        usersService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
