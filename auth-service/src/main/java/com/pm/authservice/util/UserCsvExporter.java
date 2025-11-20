package com.pm.authservice.util;

import com.opencsv.CSVWriter;
import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.user.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.stream.Collectors;

public class UserCsvExporter extends AbstractCsvExporter {
	
	private final List<UserDTO> userList;
	

	public UserCsvExporter(List<UserDTO> userList, HttpServletResponse response) {
		super(response);
		this.userList = userList;
	}

	@Override

    protected void createHeaderRow(CSVWriter csvWriter) {
        csvWriter.writeNext(new String[] { "Id", "First Name", "Last Name", "Email","Roles" }, false);
    }

	@Override
	protected void writeData(CSVWriter csvWriter) {
        for (UserDTO bean : userList) {
			String commaSeperatedRoles = bean.getRoles().stream()
					.map(RoleDTO::getNameLabel)
					.collect(Collectors.joining(","));
            String[] line = { bean.getPublicId(), bean.getFirstName(), bean.getLastName(), bean.getEmail(),commaSeperatedRoles};
            csvWriter.writeNext(line, false);
        }
	}

}
