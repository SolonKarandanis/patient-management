package com.pm.authservice.config.authorisation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pm.authservice.user.dto.UserDTO;
import com.pm.authservice.auth.dto.UserDetailsDTO;
import com.pm.authservice.util.AuthorityConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Objects;

public class RedactedEmailUserDTOSerializer extends JsonSerializer<UserDTO> {

    private static final String REDACTED_VALUE = "*****************";

    @Override
    public void serialize(UserDTO userDTO, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("publicId", userDTO.getPublicId());
        jsonGenerator.writeStringField("username", userDTO.getUsername());
        jsonGenerator.writeStringField("lastName", userDTO.getLastName());
        jsonGenerator.writeStringField("firstName", userDTO.getFirstName());
        jsonGenerator.writeStringField("status", userDTO.getStatus());
        jsonGenerator.writeStringField("statusLabel", userDTO.getStatusLabel());

        if (userDTO.getIsEnabled() != null) {
            jsonGenerator.writeBooleanField("isEnabled", userDTO.getIsEnabled());
        }

        if (userDTO.getRoles() != null) {
            jsonGenerator.writeFieldName("roles");
            serializerProvider.defaultSerializeValue(userDTO.getRoles(), jsonGenerator);
        }

        if (userDTO.getOperations() != null) {
            jsonGenerator.writeFieldName("operations");
            serializerProvider.defaultSerializeValue(userDTO.getOperations(), jsonGenerator);
        }

        jsonGenerator.writeFieldName("email");
        if (isAuthorizedToViewEmail(userDTO)) {
            jsonGenerator.writeString(userDTO.getEmail());
        } else {
            jsonGenerator.writeString(REDACTED_VALUE);
        }

        jsonGenerator.writeEndObject();
    }

    private boolean isAuthorizedToViewEmail(UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        boolean isSystemAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(AuthorityConstants.ROLE_SYSTEM_ADMIN));

        if (isSystemAdmin) {
            return true;
        }

        if (authentication.getPrincipal() instanceof UserDetailsDTO currentUserDetails) {
            return Objects.equals(currentUserDetails.getPublicId(), userDTO.getPublicId());
        }

        return false;
    }
}
