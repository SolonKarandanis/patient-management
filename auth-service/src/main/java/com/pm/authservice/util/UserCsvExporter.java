package com.pm.authservice.util;

import com.opencsv.CSVWriter;
import com.pm.authservice.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class UserCsvExporter extends AbstractCsvExporter {
	
	private List<UserDTO> userList;
	

	public UserCsvExporter(List<UserDTO> userList, HttpServletResponse response) {
		super(response);
		this.userList = userList;
	}

	@Override

    protected void createHeaderRow(CSVWriter csvWriter) {
        csvWriter.writeNext(new String[] { "Id", "First Name", "Last Name", "Email" }, false);
    }

	@Override
	protected void writeData(CSVWriter csvWriter) {
        for (UserDTO bean : userList) {
            String[] line = { bean.getPublicId(), bean.getFirstName(), bean.getLastName(), bean.getEmail()};
            csvWriter.writeNext(line, false);
        }
	}

}
