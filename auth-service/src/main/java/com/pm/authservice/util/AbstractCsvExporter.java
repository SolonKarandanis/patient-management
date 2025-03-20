package com.pm.authservice.util;

import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public abstract class AbstractCsvExporter {
    private static Logger log = LoggerFactory.getLogger(AbstractCsvExporter.class);
	
    private HttpServletResponse response;
    protected abstract void createHeaderRow(CSVWriter csvWriter);
    protected abstract void writeData(CSVWriter csvWriter);


    protected AbstractCsvExporter(HttpServletResponse response) {
        this.response = response;
    }

    public void exportData() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
                CSVWriter csvWriter = new CSVWriter(writer)) {
            createHeaderRow(csvWriter);
            writeData(csvWriter);
            writer.flush();
        }
    }

}
