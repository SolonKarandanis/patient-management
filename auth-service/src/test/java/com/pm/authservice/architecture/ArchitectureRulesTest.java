package com.pm.authservice.architecture;

import com.pm.authservice.AuthServiceApplication;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureRulesTest {

    private static JavaClasses analyzedClasses;

    @BeforeAll
    static void importClasses() {
        analyzedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackagesOf(AuthServiceApplication.class);
    }

    @Test
    void domainMustNotDependOnSpring() {
        noClasses().that().resideInAPackage("com.pm.authservice.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "jakarta.transaction.."
                )
                .because("The domain layer must be framework-agnostic (no Spring, no JPA)")
                .check(analyzedClasses);
    }

    @Test
    void domainMustNotDependOnInfrastructure() {
        noClasses().that().resideInAPackage("com.pm.authservice.domain..")
                .should().dependOnClassesThat().resideInAPackage("com.pm.authservice.infrastructure..")
                .because("The domain layer must not depend on infrastructure")
                .check(analyzedClasses);
    }

    @Test
    void repositoriesMustLiveInPersistencePackage() {
        classes().that().areAssignableTo("org.springframework.data.repository.Repository")
                .should().resideInAPackage("com.pm.authservice.infrastructure.persistence.repository..")
                .because("All Spring Data repositories must live in the persistence layer")
                .check(analyzedClasses);
    }
}
