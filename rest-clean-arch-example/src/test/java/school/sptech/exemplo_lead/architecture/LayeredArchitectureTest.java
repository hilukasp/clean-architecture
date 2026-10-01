package school.sptech.exemplo_lead.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Turns the layering convention (domain / application / infrastructure) and the
 * input-port boundary (controllers depend on UseCase interfaces, never on the
 * concrete Interactor) into rules the build enforces, instead of relying on
 * code review alone.
 */
@AnalyzeClasses(packages = "school.sptech.exemplo_lead", importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule layersRespectDependencyDirection = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule webLayerOnlyDependsOnUseCasePorts = noClasses()
            .that().resideInAPackage("..infrastructure.web..")
            .should().dependOnClassesThat().haveNameMatching(".*Interactor");
}
