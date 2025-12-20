package com.pm.notificationservice.email.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "emails")
public class Email {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "EMAILS_ID_GENERATOR"
    )
    @SequenceGenerator(
            name = "EMAILS_ID_GENERATOR",
            sequenceName = "email_seq",
            allocationSize = 1
    )
    private Integer id;


    @Column(name = "date_created")
    private LocalDateTime dateCreated;


    @Column(name = "date_sent")
    private LocalDateTime dateSent;


    @Column(name = "header_bcc")
    private String headerBcc;

    @Column(name = "header_cc")
    private String headerCc;

    @Column(name = "header_from")
    private String headerFrom;

    @Column(name = "header_reply_to")
    private String headerReplyTo;

    @Column(name = "header_subject")
    private String headerSubject;

    @Column(name = "header_to")
    private String headerTo;

    @Column(name = "status")
    private EmailStatus status;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "message_body")
    private String messageBody;

    @Column(name = "email_types_id")
    private Integer emailTypesId;

    //bi-directional many-to-one association to EmailType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_types_id",insertable=false, updatable=false)
    private EmailType emailType;

    //bi-directional many-to-one association to EmailAttachment
    @OneToMany(mappedBy = "email", cascade = CascadeType.PERSIST)
    private List<EmailAttachment> emailAttachments;


    @Column(name = "details_1")
    private String details1;

    public void addAttachment(EmailAttachment attachment){
        emailAttachments.add(attachment);
        attachment.setEmail(this);
    }

    public void removeAttachment(EmailAttachment attachment){
        emailAttachments.remove(attachment);
        attachment.setEmail(null);
    }

    public Email() {
    }

    public Email(String headerFrom, String headerTo, String headerCc, String headerBcc, String headerSubject, String messageBody,
                 List<EmailAttachment> emailAttachments) {
        this.headerFrom = headerFrom;
        this.headerTo = headerTo;
        this.headerSubject = headerSubject;
        this.messageBody = messageBody;
        this.headerCc = headerCc;
        this.headerBcc = headerBcc;
        this.emailAttachments = emailAttachments;
    }

}
