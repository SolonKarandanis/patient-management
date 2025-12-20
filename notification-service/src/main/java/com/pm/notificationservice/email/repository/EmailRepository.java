package com.pm.notificationservice.email.repository;


import com.pm.notificationservice.email.model.Email;
import com.pm.notificationservice.email.model.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface EmailRepository extends JpaRepository<Email, Integer> ,
        QuerydslPredicateExecutor<Email> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Email email set email.status = :status, email.dateSent = :dateSent where email.id = :emailId")
    public void updateEmailStatusAndDateSentById(Integer emailId, EmailStatus status, Date dateSent);
}
