package com.pm.authservice.dto;

import com.pm.authservice.model.I18nTranslation;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class I18nTranslationImportDTO {

    private List<I18nTranslation> insertionsList;
    private List<I18nTranslation> updatesList;
    private List<Integer> deletionsList;

    public Set<String> getInsertLabelKeys() {
        return Optional.ofNullable(insertionsList)
                .map(ofInsertList -> ofInsertList.stream().map(I18nTranslation::getI18nLabelResourceKey).collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElseGet(LinkedHashSet::new);
    }

    public boolean hasUpdates() {
        return Stream.of(insertionsList, updatesList, deletionsList).anyMatch(u -> u != null && !u.isEmpty());
    }
}
