package com.pm.notificationservice.email.repository;


import com.pm.notificationservice.email.model.EmailAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailAttachmentRepository extends JpaRepository<EmailAttachment, Integer> {

    @Query("SELECT ea FROM EmailAttachment ea "
            + "WHERE ea.emailsId= :emailsId ")
    public List<EmailAttachment> getEmailAttachmentsByEmailId(@Param("emailsId") Integer emailsId);
}
