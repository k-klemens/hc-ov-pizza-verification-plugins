package at.kk.msc.hcov.plugin.pizza;

import static org.assertj.core.api.Assertions.assertThat;

import at.kk.msc.hcov.plugin.pizza.util.TestUtils;
import at.kk.msc.hcov.sdk.plugin.PluginConfigurationNotSetException;
import java.io.FileNotFoundException;
import java.util.List;
import org.apache.jena.ontology.OntModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RestrictionVerificationPluginTest {

  RestrictionVerificationPlugin target;

  @BeforeEach
  void setUp() {
    target = new RestrictionVerificationPlugin();
  }

  @Test
  public void testGetElementExtractor_extractorWorksCorrectly() throws FileNotFoundException, PluginConfigurationNotSetException {
    // given
    OntModel givenPizzaOntology = TestUtils.loadPizzaOntology();

    // when
    List<OntModel> actual = target.getElementExtractor().apply(givenPizzaOntology);


    // then
    String namedPizzaUri = "http://www.co-ode.org/ontologies/pizza/pizza.owl#NamedPizza";
    assertThat(actual)
        .hasSize(22)
        .allMatch(
            // assert each ont model has exactly one pizza
            ontModel -> ontModel.getOntClass(namedPizzaUri).listSubClasses().toList().size() == 1
        )
        .flatMap(
            ontModel -> ontModel.listRestrictions().toList()
        )
        .allMatch(
            // assert each restriction is either allValuesFrom or someValuesFrom
            restriction -> restriction.isAllValuesFromRestriction() || restriction.isSomeValuesFromRestriction()
        );

    //printing
    TestUtils.printRestrictionsOnPizza(actual);
  }


}
