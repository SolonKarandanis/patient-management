package com.pm.authservice.controller;

import com.pm.authservice.config.authorisation.NoAuthentication;
import com.pm.authservice.config.i18n.Translate;
import com.pm.authservice.dto.*;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.model.User;
import com.pm.authservice.service.UserService;
import com.pm.authservice.util.AppConstants;
import com.pm.authservice.util.HttpUtil;
import com.pm.authservice.util.UserCsvExporter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService usersService;

    public UserController(UserService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/export/csv")
    public void exportUsersToCsv(
            @RequestBody @Valid UsersSearchRequestDTO searchObj,
            HttpServletResponse response) throws Exception{
        Long resultsCount = usersService.countUsers(searchObj);
        log.info("UserController --> exportUsersToCsv --> results: {}", resultsCount);
        if (resultsCount >= AppConstants.MAX_RESULTS_CSV_EXPORT) {
            throw new BusinessException("error.max.csv.results");
        }
        response.setContentType(HttpUtil.MEDIA_TYPE_CSV);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users-results.csv\"");
        List<UserDTO> results =usersService.findAllUsersForExport(searchObj);
        UserCsvExporter exporter= new UserCsvExporter(results, response);
        exporter.exportData();
    }

    @PostMapping("/search")
    @Translate(path = "list[*].status", targetProperty = "statusLabel")
    public ResponseEntity<SearchResults<UserDTO>> findAllUsers(@RequestBody @Valid UsersSearchRequestDTO searchObj){
        Page<User> results = usersService.searchUsers(searchObj);
        List<UserDTO> dtos=usersService.convertToDTOList(results.getContent());
        return ResponseEntity.ok().body(new SearchResults<UserDTO>(Math.toIntExact(results.getTotalElements()), dtos));
    }

    @GetMapping("/{id}")
    @Translate(path = "status", targetProperty = "statusLabel")
    @Translate(path = "roles[*].name", targetProperty = "nameLabel")
    public ResponseEntity<UserDTO> viewUser(@PathVariable(name= "id", required=true) String publicId)
            throws NotFoundException {
        User user = usersService.findByPublicId(publicId);
        log.info("UserController --> viewUser --> username: {}", user.getUsername());
        return ResponseEntity.ok(usersService.convertToDTO(user));
    }

    @GetMapping(value="/account")
    @Translate(path = "status", targetProperty = "statusLabel")
    @Translate(path = "roles[*].name", targetProperty = "nameLabel")
    public ResponseEntity<UserDTO> getUserByToken(Authentication authentication) throws NotFoundException{
        UserDetailsDTO dto = (UserDetailsDTO)authentication.getPrincipal();
        User user = usersService.findByPublicId(dto.getPublicId());
        log.info("UserController --> getUserByToken --> username: {}", user.getUsername());
        return ResponseEntity.ok(usersService.convertToDTO(user));
    }

    @NoAuthentication
    @PostMapping
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid CreateUserDTO user, final HttpServletRequest request)
            throws BusinessException{
        log.info("UserController->registerUser->RequestBody: {}" , user);
        User userSaved=usersService.registerUser(user, applicationUrl(request));
        return ResponseEntity.ok(usersService.convertToDTO(userSaved));
    }

    @PutMapping("/{id}")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable(name = "id", required=true) String publicId,
            @RequestBody @Valid UpdateUserDTO user)throws NotFoundException{
        log.info("UserController->updateUser->RequestBody: {}" , user);
        User userSaved=usersService.updateUser(publicId,user);
        return ResponseEntity.ok(usersService.convertToDTO(userSaved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name= "id",required=true) @Min(1) String publicId)
            throws NotFoundException{
        log.info("UserController->deleteUser->publicId: {}" , publicId);
        usersService.deleteUser(publicId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/activate")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> activateUser(
            @PathVariable(name= "id", required=true) String publicId )
            throws NotFoundException, BusinessException{
        log.info("UserController->activateUser->publicId: {}" , publicId);
        User user = usersService.findByPublicId(publicId);
        user = usersService.activateUser(user);
        return ResponseEntity.ok(usersService.convertToDTO(user));
    }

    @PutMapping("/{id}/deactivate")
    @Translate(path = "status", targetProperty = "statusLabel")
    public ResponseEntity<UserDTO> deactivateUser(
            @PathVariable(name= "id", required=true) String publicId )
            throws NotFoundException, BusinessException{
        log.info("UserController->deactivateUser->publicId: {}" , publicId);
        User user = usersService.findByPublicId(publicId);
        user = usersService.deactivateUser(user);
        return ResponseEntity.ok(usersService.convertToDTO(user));
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token)throws BusinessException{
        usersService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    protected String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }

}
