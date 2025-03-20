package com.pm.authservice.controller;

import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.dto.UsersSearchRequestDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.service.UserService;
import com.pm.authservice.util.AppConstants;
import com.pm.authservice.util.HttpUtil;
import com.pm.authservice.util.UserCsvExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        log.info("UsersController --> exportUsersToCsv --> results: {}", resultsCount);
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
}
