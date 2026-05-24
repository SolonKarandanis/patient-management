package com.pm.authservice.infrastructure.application;

import com.pm.authservice.infrastructure.web.dto.I18nResourceManagementRequestDTO;
import com.pm.authservice.infrastructure.web.dto.I18nResourceManagementResponseDTO;
import com.pm.authservice.infrastructure.web.dto.SearchResults;
import com.pm.authservice.infrastructure.persistence.repository.I18nLabelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service("i18nResourceManagementService")
@Transactional(readOnly = true)
public class I18nResourceManagementServiceBean implements I18nResourceManagementService{

    private final I18nLabelRepository i18nLabelRepository;

    public I18nResourceManagementServiceBean(I18nLabelRepository i18nLabelRepository) {
        this.i18nLabelRepository = i18nLabelRepository;
    }

    private PageRequest normalizePageRequestSort(PageRequest pageRequest) {
        if (pageRequest == null) return null;
        List<Sort.Order> fixedOrders = pageRequest.getSort().get()
            .map(order -> order.withProperty("id"))
            .toList();
        if (fixedOrders.isEmpty()) {
            return pageRequest;
        }
        return pageRequest.withSort(Sort.by(fixedOrders));
    }

    @Override
    public SearchResults<I18nResourceManagementResponseDTO> searchI18nResources(
            I18nResourceManagementRequestDTO searchRequest, PageRequest pageRequest) {
        Integer langId = searchRequest.getLanguageId() == null || searchRequest.getLanguageId().isEmpty() ? null : Integer.parseInt(searchRequest.getLanguageId());
        Integer modId = searchRequest.getModuleId() == null || searchRequest.getModuleId().isEmpty() ? null : Integer.parseInt(searchRequest.getModuleId());

        pageRequest = normalizePageRequestSort(pageRequest);

        Page<Long> resultsIds = i18nLabelRepository.searchI18nResourcesDistinctLabelIds(langId, modId, searchRequest.getTerm(), pageRequest);

        if(resultsIds.isEmpty()) {
            return new SearchResults<>(0, Collections.emptyList());
        }

        List<Object[]> results = i18nLabelRepository.findResourceDataWithPaginationByLabelIds(resultsIds.getContent());
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
