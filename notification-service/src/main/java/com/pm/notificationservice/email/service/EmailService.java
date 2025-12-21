package com.pm.notificationservice.email.service;



import com.pm.notificationservice.email.dto.EmailDTO;
import com.pm.notificationservice.email.dto.EmailSearchRequestDTO;
import com.pm.notificationservice.shared.dto.SearchResults;
import com.pm.notificationservice.email.model.Email;
import com.pm.notificationservice.email.model.EmailStatus;
import com.pm.notificationservice.email.model.EmailType;
import com.pm.notificationservice.shared.exception.NotificationServiceException;

import java.util.List;

public interface EmailService {
    /**
     * Utilize the JMX Queue to send an email
     * Send emails
     */
    public void saveAndSendEmail(Email eMess) throws NotificationServiceException;
    /**
     * Sends e-mail as instance of class <code>EMailMessage</code>
     * synchronously.
     * VERY IMPORTANT: SHOULD ONLY BE CALLED FROM AN MDB OR ANOTHER
     * <B>ASYNCHRONOUS</B> MECHANISM!
     *
     * @param eMess
     *            <code>EMailMessage</code>
     *            The message to send. Contains all the necessary
     *            fields.
     * @throws EDException
     *             If there is a problem with the back-end.
     */
    public void saveAndSendEmailSynchronous(Email eMess) throws NotificationServiceException;
    /**
     * Return all email types used in the application.
     *
     * @return
     */
    List<EmailType> getEmailTypes();
     /**
     * Returns email object along with the email body.
     *
     * @param id
     *            - message id.
     * @return email object along with the email body
     * @throws EDException
     *             If there is a problem with the back-end.
     */
    Email getEmailById(Integer id) throws NotificationServiceException;
    /**
     * Updates email status.
     *
     * @param id
     * @param status
     */
    void updateEmailStatus(Integer id, EmailStatus status);
    /**
     * Resends the emails stored in the database.
     *
     * @param emailIds
     *            - list of email ids
     * @throws EDException
     *             If there is a problem with the back-end.
     */
    void resendEmails(List<Integer> emailIds) throws NotificationServiceException;
    /**
     * Return email type object by resource key.
     *
     * @param resourceKey
     * @return
     */
    EmailType getEmailTypeByKey(String resourceKey);
    /**
     * Saves the email in the database.
     */
    Email saveEmail(Email email, EmailStatus status) throws NotificationServiceException;

    public EmailDTO convertToDTO(Email email, Boolean withMsgBody);

    public List<EmailDTO> convertToDTOList(List<Email> emails, Boolean withMsgBody);

    SearchResults<Email> findEmails(EmailSearchRequestDTO searchRequest);

}
