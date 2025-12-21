package com.pm.notificationservice.email.dto;

import java.util.List;

import com.pm.notificationservice.shared.dto.Paging;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EmailSearchRequestDTO{

    private String dateCreatedFrom;

    private String dateCreatedTo;

    @NotEmpty(message = "{prompt.email.search.sent.date.range} {error.generic.required}")
    private String dateSentFrom;

    @NotEmpty(message = "{prompt.email.search.sent.date.range} {error.generic.required}")
    private String dateSentTo;

    @NotEmpty(message = "{prompt.audit.email.types} {error.generic.required}")
    private List<Integer> emailTypeIds;

    private String subject;

    private String sentTo;

    private String status;

    protected Paging paging;
}
