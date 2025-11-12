package com.pm.authservice.repository;

import com.pm.authservice.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    @Query(name = Language.GET_LANGUAGES)
    List<Language> getLanguages();
}
