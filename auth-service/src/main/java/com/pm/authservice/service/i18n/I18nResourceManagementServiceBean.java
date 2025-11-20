package com.pm.authservice.service.i18n;

import com.pm.authservice.dto.I18nResourceManagementRequestDTO;
import com.pm.authservice.dto.I18nResourceManagementResponseDTO;
import com.pm.authservice.dto.SearchResults;
import com.pm.authservice.repository.I18nLabelRepository;
import com.pm.authservice.service.GenericServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service("i18nResourceManagementService")
@Transactional(readOnly = true)
public class I18nResourceManagementServiceBean extends GenericServiceBean implements I18nResourceManagementService{

    private final I18nLabelRepository i18nLabelRepository;

    public I18nResourceManagementServiceBean(I18nLabelRepository i18nLabelRepository) {
        this.i18nLabelRepository = i18nLabelRepository;
    }


    @Override
    public SearchResults<I18nResourceManagementResponseDTO> searchI18nResources(
            I18nResourceManagementRequestDTO searchRequest, PageRequest pageRequest) {
        Integer langId = searchRequest.getLanguageId() == null || searchRequest.getLanguageId().isEmpty() ? null : Integer.parseInt(searchRequest.getLanguageId());
        Integer modId = searchRequest.getModuleId() == null || searchRequest.getModuleId().isEmpty() ? null : Integer.parseInt(searchRequest.getModuleId());

        pageRequest = transformPageSorting(pageRequest, Collections.emptyMap(), Collections.emptySet());

        Page<Long> resultsIds = i18nLabelRepository.searchI18nResourcesDistinctLabelIds(langId, modId, searchRequest.getTerm(), pageRequest);

        if(resultsIds.isEmpty()) {
            return new SearchResults<>(0, Collections.emptyList());
        }

        List<Object[]> results = i18nLabelRepository.getResourceDataWithPaginationByLabelIds(resultsIds.getContent());
        List<I18nResourceManagementResponseDTO> respDto = convertToResponseDTOList(results);
        long totalElems = resultsIds.getTotalElements();

        return new SearchResults<>((int) totalElems, respDto);
    }

    private List<I18nResourceManagementResponseDTO> convertToResponseDTOList(List<Object[]> rows) {
        Map<String, I18nResourceManagementResponseDTO> resMap = new LinkedHashMap<String, I18nResourceManagementResponseDTO>();
        for (Object[] row : rows) {
            Integer id = (Integer) row[0];
            String key = (String) row[1];
            String mod = (String) row[2];
            Integer langId = (Integer) row[3];
            String value = (String) row[4];

            I18nResourceManagementResponseDTO dto = resMap.get(key);

            if(dto == null) {
                dto = new I18nResourceManagementResponseDTO();
                dto.setId(id);
                dto.setKey(key);
                dto.setMod(mod);
                Map<Integer, String> translations = new HashMap<>();
                translations.put(langId, value);
                dto.setTranslations(translations);
                resMap.put(key, dto);
            }
            else {
                if(langId != null) {
                    dto.getTranslations().put(langId, value);
                }
            }
        }
        return new ArrayList<>(resMap.values());
    }
}
