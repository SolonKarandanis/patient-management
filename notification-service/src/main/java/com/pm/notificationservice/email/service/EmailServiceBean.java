package com.pm.notificationservice.email.service;

import com.pm.notificationservice.email.EMailConstants;
import com.pm.notificationservice.email.dto.EmailDTO;
import com.pm.notificationservice.email.dto.EmailSearchRequestDTO;
import com.pm.notificationservice.email.model.*;
import com.pm.notificationservice.email.repository.EmailAttachmentRepository;
import com.pm.notificationservice.email.repository.EmailRepository;
import com.pm.notificationservice.email.repository.EmailTypeRepository;
import com.pm.notificationservice.shared.dto.Paging;
import com.pm.notificationservice.shared.dto.SearchResults;
import com.pm.notificationservice.shared.exception.NotificationServiceException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

@Service("emailService")
public class EmailServiceBean  implements EmailService{

    private static final Logger log = LoggerFactory.getLogger(EmailServiceBean.class);

    private final EmailRepository emailRepository;
    private final EmailTypeRepository emailTypeRepository;
    private final EmailAttachmentRepository emailAttachmentRepository;
    private final JavaMailSender mailSender;
//    private final FileService fileService;

    public EmailServiceBean(
            EmailRepository emailRepository,
            EmailTypeRepository emailTypeRepository,
            EmailAttachmentRepository emailAttachmentRepository,
            JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.emailTypeRepository = emailTypeRepository;
        this.emailAttachmentRepository = emailAttachmentRepository;
        this.mailSender = mailSender;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveAndSendEmail(Email eMess) throws NotificationServiceException {
        eMess = saveEmail(eMess, EmailStatus.SENT);
        try{
            MimeMessage message = toMimeMessage(eMess);
            mailSender.send(message);
        }
        catch (MessagingException | UnsupportedEncodingException ex){
            throw new NotificationServiceException("errors.send.email", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveAndSendEmailSynchronous(Email eMess) throws NotificationServiceException {
        try {
            Properties props = new Properties();
            props.setProperty(EMailConstants.EMAIL_SERVER_HOST_NAME, EMailConstants.EMAIL_SERVER_HOST_NAME);
            props.setProperty(EMailConstants.EMAIL_TRANSPORT_PROTOCOL_NAME, EMailConstants.EMAIL_TRANSPORT_PROTOCOL_VALUE);
            props.setProperty(EMailConstants.EMAIL_SERVICE_AUTHENTICATION, EMailConstants.EMAIL_SERVICE_AUTHENTICATION_VALUE);
            Session session = Session.getDefaultInstance(props, null);
            if (session == null) {
                log.debug("session is null!!!!!!");
                throw new NotificationServiceException("Could not obtain mail session");
            }
            // Construct the message
            MimeMessage msg = new MimeMessage(session);
            // Set "from" address
            InternetAddress fromAddress = new InternetAddress(eMess.getHeaderFrom());
            msg.setFrom(fromAddress);
            // Add "to" emails
            InternetAddress[] addresses = parseAddress(eMess.getHeaderTo());
            log.debug("sendEmailSynchronous: TO= {}" , dumpAddresses(addresses));
            msg.addRecipients(Message.RecipientType.TO,addresses);

            addresses = parseAddress(eMess.getHeaderCc());
            log.debug("sendEmailSynchronous: CC= {}" , dumpAddresses(addresses));
            msg.addRecipients(Message.RecipientType.CC, addresses);

            addresses = parseAddress(eMess.getHeaderBcc());
            log.debug("sendEmailSynchronous: BCC= {}" , dumpAddresses(addresses));
            msg.addRecipients(Message.RecipientType.BCC, addresses);

            addresses = parseAddress(eMess.getHeaderReplyTo());
            log.debug("sendEmailSynchronous: ReplyTo= {}" , dumpAddresses(addresses));
            msg.setReplyTo(addresses);

            // Set email subject
            if (eMess.getHeaderSubject() != null) {
                msg.setSubject(eMess.getHeaderSubject(), "UTF-8");
            } else {
                msg.setSubject("");
            }
            msg.setSentDate(new Date());

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            String text = eMess.getMessageBody();
            messageBodyPart.setContent(text, EMailConstants.EMAIL_PLAIN_TEXT_CONTENT_TYPE);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            /*
             * attachments to be added
             */
            if (eMess.getEmailAttachments() != null) {
                List<EmailAttachment> fileList = eMess.getEmailAttachments();
//                for (EmailAttachment emailAttachment : fileList) {
//                    MimeBodyPart attachmenBodyPart = new MimeBodyPart();
//                    DataSource source = new AttachmentDataSource(emailAttachment.getData(),
//                            FileUtil.getContentTypeByFileName(emailAttachment.getFileName()), emailAttachment.getFileName());
//                    attachmenBodyPart.setDataHandler(new DataHandler(source));
//                    attachmenBodyPart.setFileName(emailAttachment.getFileName());
//                    multipart.addBodyPart(attachmenBodyPart);
//                }
            }
            // Put parts in message
            msg.setContent(multipart);

            // Send the mail
            Transport.send(msg);
            log.info("message-id: {}" , msg.getHeader("message-id", ","));
        }
        catch (SendFailedException e) {
            saveEmail(eMess, EmailStatus.FAILED);
            throw new NotificationServiceException("errors.send.email", e);
        } catch (MessagingException e) {
            saveEmail(eMess, EmailStatus.FAILED);
            throw new NotificationServiceException("errors.send.email", e);

        }
    }

    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public List<EmailType> getEmailTypes() {
        return emailTypeRepository.findAll();
    }

    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public Email getEmailById(Integer id) throws NotificationServiceException {
        Optional<Email> emailMaybe = emailRepository.findById(id);
        if(emailMaybe.isPresent()){
            List<EmailAttachment> attachments = emailAttachmentRepository.getEmailAttachmentsByEmailId(id);
//            for (EmailAttachment emailAttachment : attachments) {
//                byte[] data = fileService.getFileContentById(BigInteger.valueOf(emailAttachment.getFileReferenceId()));
//                emailAttachment.setData(data);
//            }
            return emailMaybe.get();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateEmailStatus(Integer id, EmailStatus status) {
        Date dateSent = null;
        if (EmailStatus.SENT.equals(status)) {
            dateSent = new Date();
        }
        emailRepository.updateEmailStatusAndDateSentById(id,status,dateSent);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void resendEmails(List<Integer> emailIds) throws NotificationServiceException {

    }

    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public EmailType getEmailTypeByKey(String resourceKey) {
        return emailTypeRepository.getEmailTypeByKey(resourceKey);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Email saveEmail(Email email, EmailStatus status) throws NotificationServiceException {
        email.setStatus(status);
        email.setDateCreated(LocalDateTime.now());
        if (EmailStatus.SENT.equals(email.getStatus())) {
            email.setDateSent(LocalDateTime.now());
        }
//        if(!CollectionUtils.isEmpty(email.getEmailAttachments())){
//            for(EmailAttachment att : email.getEmailAttachments()){
//                BigInteger fileId = fileService.createFile(att.getData(), new FileInfo(att.getFileName(), FileUtil.getContentTypeByFileName(att.getFileName()),
//                        FileConstants.EMAIL_ATTACHMENT, att.getData().length));
//                att.setFileReferenceId(fileId.longValue());
//                att.setEmail(email);
//            }
//        }
        try {
            return emailRepository.save(email);
        } catch (Exception e) {
            email.setStatus(EmailStatus.FAILED);
            emailRepository.save(email);
            throw new NotificationServiceException("errors.saving.email", e);
        }

    }

    @Override
    public EmailDTO convertToDTO(Email email, Boolean withMsgBody) {
        if (email == null) {
            return null;
        }
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setId(email.getId());
        emailDTO.setDateCreated(ConvertUtils.convert(email.getDateCreated()));
        emailDTO.setDateSent(ConvertUtils.convert(email.getDateSent()));
        emailDTO.setDetails1(email.getDetails1());
        emailDTO.setHeaderFrom(email.getHeaderFrom());
        emailDTO.setHeaderTo(email.getHeaderTo());
        emailDTO.setHeaderCc(email.getHeaderCc());
        emailDTO.setHeaderBcc(email.getHeaderBcc());
        emailDTO.setHeaderReplyTo(email.getHeaderReplyTo());
        emailDTO.setHeaderSubject(email.getHeaderSubject());
        emailDTO.setStatus(email.getStatus().getValue());
        emailDTO.setEmailTypeId(email.getEmailType().getId());
        emailDTO.setEmailTypeKey(email.getEmailType().getResourceKey());
        if (withMsgBody) {
            emailDTO.setMessageBody(email.getMessageBody());
        }
        return emailDTO;
    }

    @Override
    public List<EmailDTO> convertToDTOList(List<Email> emails, Boolean withMsgBody) {
        if(CollectionUtils.isEmpty(emails)){
            return List.of();
        }
        List<EmailDTO> result = new ArrayList<>();
        for(Email email : emails){
            result.add(convertToDTO(email,withMsgBody));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public SearchResults<Email> findEmails(EmailSearchRequestDTO searchRequest) {
        Predicate predicate = getSearchPredicate(searchRequest);
        PageRequest pageRequest = toPageRequest(searchRequest.getPaging());
        Page<Email> emails = emailRepository.findAll(predicate, pageRequest);
        return new SearchResults<>(emails.getTotalElements(), emails.getContent());
    }

    protected Predicate getSearchPredicate(EmailSearchRequestDTO searchRequest){
        QEmail email = QEmail.email;

        LocalDateTime dateCreatedFrom = (LocalDateTime)ConvertUtils.convert(searchRequest.getDateCreatedFrom(),LocalDateTime.class);
        LocalDateTime dateCreatedTo = (LocalDateTime)ConvertUtils.convert(searchRequest.getDateCreatedTo(),LocalDateTime.class);
        LocalDateTime dateSentFrom = (LocalDateTime)ConvertUtils.convert(searchRequest.getDateSentFrom(),LocalDateTime.class);
        LocalDateTime dateSentTo = (LocalDateTime)ConvertUtils.convert(searchRequest.getDateSentTo(),LocalDateTime.class);
        List<Integer> emailTypeIds = searchRequest.getEmailTypeIds();
        String subject = searchRequest.getSubject();
        String sentTo = searchRequest.getSentTo();
        String status = searchRequest.getStatus();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(email.dateSent.after(dateSentFrom).and(email.dateSent.before(dateSentTo)));
        builder.and(email.emailTypesId.in(emailTypeIds));
        if(Objects.nonNull(dateCreatedFrom)){
            builder.and(email.dateCreated.before(dateCreatedFrom));
        }

        if(Objects.nonNull(dateCreatedTo)){
            builder.and(email.dateCreated.before(dateCreatedTo));
        }

        if(Objects.nonNull(subject)){
            builder.and(email.headerSubject.eq(subject));
        }

        if(Objects.nonNull(sentTo)){
            builder.and(email.headerTo.eq(sentTo));
        }

        if(Objects.nonNull(status)){
            builder.and(email.status.eq(EmailStatus.valueOf(status)));
        }

        return builder;
    }

    protected PageRequest toPageRequest(Paging paging) {
        Sort sortBy = Sort.by(Sort.Direction.ASC,"id");
        if(Objects.nonNull(paging.getSortingDirection()) && Objects.nonNull(paging.getSortingColumn())){
            sortBy = Sort.by(Sort.Direction.valueOf(paging.getSortingDirection()), paging.getSortingColumn());
        }
        return PageRequest.of(paging.getPagingStart(), paging.getPagingSize(), sortBy);
    }

    protected InternetAddress[] parseAddress(String str) throws AddressException {
        InternetAddress[] retVal;
        if (str == null || str.isEmpty()) {
            retVal = null;
        } else {
            retVal = InternetAddress.parse(str);
        }
        return retVal;
    }

    /**
     * This method generates a String useful for debug statements to log the
     * actuall recipients that emails were sent We prefer this method instead of
     * InternetAddress.toString(InternetAddress[]) to more "accurate" logging
     *
     * @param addresses
     * @return A string in comma (,) delimeted format. Last comma is not
     *         removed.
     */
    protected String dumpAddresses(InternetAddress[] addresses) {
        StringBuilder retVal = new StringBuilder(2048);
        if (addresses != null) {
            for (InternetAddress address : addresses) {
                retVal.append(address);
                retVal.append(',');
            }

        }
        return retVal.toString();
    }

    protected MimeMessage toMimeMessage(Email mail)throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("skarandanis@gmail.com", mail.getHeaderFrom());
        messageHelper.setTo(mail.getHeaderTo());
        messageHelper.setSubject(mail.getHeaderSubject());
        messageHelper.setText(mail.getMessageBody(), true);
        return message;
    }
}
