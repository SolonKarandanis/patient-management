package com.pm.notificationservice.email;

import jakarta.activation.DataSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AttachmentDataSource implements DataSource {
    protected String name;
    protected byte[] bb;
    protected String contentType;

    public AttachmentDataSource(byte[] bb, String contentType, String name) {
        super();
        this.bb = bb;
        this.contentType = contentType;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bb);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
