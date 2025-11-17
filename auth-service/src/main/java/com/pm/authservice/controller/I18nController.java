package com.pm.authservice.controller;

import com.pm.authservice.dto.UpdateTranslationDTO;
import com.pm.authservice.exception.BusinessException;
import com.pm.authservice.service.I18nService;
import com.pm.authservice.util.AuthorityConstants;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/i18n")
@Slf4j
public class I18nController {

    protected static final String APP_RESOURCE_NAME = "application_ui_labels";
    protected static final String LANG_ISO_DEFAULT = "en";

    @Value("${i18n.resources.DB.enabled:false}")
    private Boolean i18nDbEnabled;

    private final I18nService i18nService;

    public I18nController(@Lazy I18nService i18nService) {
        this.i18nService = i18nService;
    }

    /**
     * Gets as parameter the languageIsoCode and returns the Json of all messages translations in this language.
     *
     * @param languageIsoCode
     * @return
     */
    @GetMapping(value = "/ui-labels", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<Map<String, String>> getUiLabels(@RequestParam(name = "languageIsoCode", required = false) String languageIsoCode)
            throws IOException, BusinessException {

        ResponseEntity<Map<String, String>> output = null;
        Map<String, String> outputMap = null;

        log.info(" LanguageIsoCode: {}, i18nDbEnabled: {} ", languageIsoCode, i18nDbEnabled);
        String langIso = (languageIsoCode != null && !languageIsoCode.isBlank()) ? languageIsoCode : LANG_ISO_DEFAULT;

        if (Boolean.TRUE.equals(i18nDbEnabled)) {
            outputMap = loadDbResourcesByLanguageIsoCode(langIso);
        } else {
            outputMap = loadApplicationResourcesByLanguageIsoCode(langIso);
        }
        output = new ResponseEntity<>(outputMap, HttpStatus.OK);

        return output;
    }

    @ConditionalOnExpression("${i18n.resources.DB.enabled}==true")
    @RolesAllowed({ AuthorityConstants.ROLE_SYSTEM_ADMIN })
    @PutMapping
    public ResponseEntity<Void> editLabels(@RequestBody @Valid List<UpdateTranslationDTO> updateRequest){
        i18nService.editLabelsAndSendNotification(updateRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * @param language
     * @return
     */
    private Map<String, String> loadApplicationResourcesByLanguageIsoCode(final String langIso) throws IOException, BusinessException {
        Map<String, String> outputMap = i18nService.getResourcePropertiesByModuleAndLangIsoCode(APP_RESOURCE_NAME, langIso.toLowerCase(Locale.ENGLISH));
        log.info(" Loaded Keys: {} ", outputMap.size());
        return outputMap;
    }

    /**
     * @param language
     * @return
     */
    private Map<String, String> loadDbResourcesByLanguageIsoCode(final String langIso) {
        return i18nService.getTranslationsByModuleAndLangIsoCode(APP_RESOURCE_NAME, langIso.toLowerCase(Locale.ENGLISH));
    }
}
