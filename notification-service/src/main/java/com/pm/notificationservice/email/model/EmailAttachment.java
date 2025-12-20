package com.pm.notificationservice.email.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "email_attachments")
public class EmailAttachment {

    @Id
    @SequenceGenerator(name = "EMAIL_ATTACHMENTS_ID_GENERATOR", sequenceName = "email_attachments_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_ATTACHMENTS_ID_GENERATOR")
    private Integer id;


    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_reference_id")
    private Long fileReferenceId;

    @Column(name = "emails_id")
    private Integer emailsId;

    //bi-directional many-to-one association to Email
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emails_id",insertable=false, updatable=false)
    private Email email;

    private transient byte[] data;

    public EmailAttachment() {
    }

    public EmailAttachment(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAttachment that = (EmailAttachment) o;
        return Objects.equals(fileName, that.fileName) &&
                Objects.equals(fileReferenceId, that.fileReferenceId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(fileName, fileReferenceId);
    }
}
