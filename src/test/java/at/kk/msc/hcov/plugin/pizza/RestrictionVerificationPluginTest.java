package at.kk.msc.hcov.plugin.pizza;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import at.kk.msc.hcov.plugin.pizza.util.TestUtils;
import at.kk.msc.hcov.sdk.plugin.PluginConfigurationNotSetException;
import at.kk.msc.hcov.sdk.verificationtask.model.ProvidedContext;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.jena.ontology.OntModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

  @Test
  public void testGetTemplateVariableValueResolver_requiredConfigurationNotSet_expectException() {
    // given
    Map<String, Object> givenConfiguration = new HashMap<>();
    givenConfiguration.put("TEST_SOME_OTHER_KEY", 3);
    target.setConfiguration(givenConfiguration);

    // when - then
    assertThatThrownBy(() -> target.getTemplateVariableValueResolver())
        .isInstanceOf(PluginConfigurationNotSetException.class)
        .hasMessageContaining("REPRESENTATION_MECHANISM");
  }

  @Test
  public void testGetTemplateVariableValueResolver_rectorRepresentationWorksCorrectly()
      throws FileNotFoundException, PluginConfigurationNotSetException {
    // given
    Map<String, Object> givenConfiguration = new HashMap<>();
    givenConfiguration.put("REPRESENTATION_MECHANISM", "RECTOR");
    target.setConfiguration(givenConfiguration);

    OntModel givenVenezianaModel = TestUtils.loadVenezianaOntology();
    ProvidedContext providedContext = new ProvidedContext(
        UUID.randomUUID(),
        "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg\"Veneziana\"Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato"
    );

    // when
    Map<String, Object> actual = target.getTemplateVariableValueResolver().apply(givenVenezianaModel, providedContext);

    // then
    assertThat(actual)
        .hasSize(4)
        .containsEntry("imageURI", "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg")
        .containsEntry("pizzaName", "Veneziana")
        .containsEntry("ingredientList", "Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato")
        .containsEntry("axiom",
            "Veneziana pizzas have, amongst other things, some Caper topping, and some Mozzarella topping, and some Olive topping, " +
                "and some Onion topping, and some Pine Kernels topping, and some Sultana topping, and some Tomato topping, and also only " +
                "Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, and/or Tomato toppings.");
  }

  @Test
  public void testGetTemplateVariableValueResolver_warrenRepresentationworksCorrectly()
      throws FileNotFoundException, PluginConfigurationNotSetException {
    // given
    Map<String, Object> givenConfiguration = new HashMap<>();
    givenConfiguration.put("REPRESENTATION_MECHANISM", "WARREN");
    target.setConfiguration(givenConfiguration);

    OntModel givenVenezianaModel = TestUtils.loadVenezianaOntology();
    ProvidedContext providedContext = new ProvidedContext(
        UUID.randomUUID(),
        "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg\"Veneziana\"Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato"
    );

    // when
    Map<String, Object> actual = target.getTemplateVariableValueResolver().apply(givenVenezianaModel, providedContext);

    // then
    assertThat(actual)
        .hasSize(4)
        .containsEntry("imageURI", "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg")
        .containsEntry("pizzaName", "Veneziana")
        .containsEntry("ingredientList", "Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato")
        .containsEntry("axiom",
            "Veneziana pizzas have, amongst other things, at least one Caper topping, and at least one Mozzarella topping, and at least one Olive topping, " +
                "and at least one Onion topping, and at least one Pine Kernels topping, and at least one Sultana topping, and at least one Tomato topping, " +
                "and also no other than Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, and/or Tomato toppings.");
  }

  @Test
  public void testGetTemplateVariableValueResolver_onlySomeValuesFrom_warrenRepresentationworksCorrectly()
      throws FileNotFoundException, PluginConfigurationNotSetException {
    // given
    Map<String, Object> givenConfiguration = new HashMap<>();
    givenConfiguration.put("REPRESENTATION_MECHANISM", "WARREN");
    target.setConfiguration(givenConfiguration);

    OntModel givenVenezianaModel = TestUtils.loadVenezianaOntology_onlySomeValuesFrom();
    ProvidedContext providedContext = new ProvidedContext(
        UUID.randomUUID(),
        "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg\"Veneziana\"Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato"
    );

    // when
    Map<String, Object> actual = target.getTemplateVariableValueResolver().apply(givenVenezianaModel, providedContext);

    // then
    assertThat(actual)
        .hasSize(4)
        .containsEntry("imageURI", "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg")
        .containsEntry("pizzaName", "Veneziana")
        .containsEntry("ingredientList", "Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato")
        .containsEntry("axiom",
            "Veneziana pizzas have, amongst other things, at least one Caper topping, and at least one Mozzarella topping, and at least one Olive topping, " +
                "and at least one Onion topping, and at least one Pine Kernels topping, and at least one Sultana topping, and at least one Tomato topping.");
  }

  @Test
  public void testGetTemplateVariableValueResolver_onlyAllValuesFrom_warrenRepresentationworksCorrectly()
      throws FileNotFoundException, PluginConfigurationNotSetException {
    // given
    Map<String, Object> givenConfiguration = new HashMap<>();
    givenConfiguration.put("REPRESENTATION_MECHANISM", "WARREN");
    target.setConfiguration(givenConfiguration);

    OntModel givenVenezianaModel = TestUtils.loadVenezianaOntology_onlyAllValuesFrom();
    ProvidedContext providedContext = new ProvidedContext(
        UUID.randomUUID(),
        "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg\"Veneziana\"Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato"
    );

    // when
    Map<String, Object> actual = target.getTemplateVariableValueResolver().apply(givenVenezianaModel, providedContext);

    // then
    assertThat(actual)
        .hasSize(4)
        .containsEntry("imageURI", "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg")
        .containsEntry("pizzaName", "Veneziana")
        .containsEntry("ingredientList", "Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato")
        .containsEntry("axiom",
            "Veneziana pizzas have, amongst other things, no other than Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, and/or Tomato toppings.");
  }

  @Test
  public void testGetTemplateVariableValueResolver_givenRepresentationNotKnown_expectException()
      throws FileNotFoundException, PluginConfigurationNotSetException {
    // given
    Map<String, Object> givenConfiguration = new HashMap<>();
    givenConfiguration.put("REPRESENTATION_MECHANISM", "NEW_REPRESENTATION");
    target.setConfiguration(givenConfiguration);


    // when - then
    assertThatThrownBy(() -> target.getTemplateVariableValueResolver())
        .isInstanceOf(PluginConfigurationNotSetException.class)
        .hasMessageContaining("Given representation 'NEW_REPRESENTATION' mechanism not known!");
  }

  @ParameterizedTest
  @ValueSource(strings = {"RESTRICTION_TASK_CREATOR", "restriction_task_creator", "rEsTrIcTiOn_tAsK_CrEaToR"})
  public void testSupports_givenSupportedStrings(String givenString) {
    // when - then
    assertThat(target.supports(givenString)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"RESTRICTION_TASK_CREATORS", "restriction_task_creatorx"})
  @NullAndEmptySource
  public void testSupports_givenUnsupportedStrings(String givenString) {
    // when - then
    assertThat(target.supports(givenString)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = "MY_REPRESENTATION")
  @NullAndEmptySource
  public void testGetTemplate_givenRepresentationNotKnown(String givenRepresentation) {
    // given
    Map<String, Object> givenConfiguration = new HashMap<>();
    givenConfiguration.put("REPRESENTATION_MECHANISM", givenRepresentation);
    target.setConfiguration(givenConfiguration);


    // when - then
    assertThatThrownBy(() -> target.getTemplate())
        .isInstanceOf(PluginConfigurationNotSetException.class)
        .hasMessageContaining("Given representation '" + givenRepresentation + "' mechanism not known!");
  }

  @ParameterizedTest
  @ValueSource(strings = {"RECTOR", "WARREN"})
  public void testGetTemplate_templateIsNotEmpty(String givenRepresentation) throws PluginConfigurationNotSetException {
    // given
    Map<String, Object> givenConfiguration = new HashMap<>();
    givenConfiguration.put("REPRESENTATION_MECHANISM", givenRepresentation);
    target.setConfiguration(givenConfiguration);


    // when
    String actual = target.getTemplate();

    // then
    assertThat(actual)
        .isNotBlank()
        .contains("<span th:text='${axiom}'/>")
        .contains("<img style='max-width: 100%' width='150' th:src='${imageURI}'/>")
        .contains("<h3 th:text='${pizzaName}'/>")
        .contains("<span th:text='${ingredientList}'/>")
        .containsIgnoringCase(givenRepresentation);
  }

}
