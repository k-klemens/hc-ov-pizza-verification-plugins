package at.kk.msc.hcov.plugin.pizza;

import static org.assertj.core.api.Assertions.assertThat;

import at.kk.msc.hcov.plugin.pizza.util.TestUtils;
import at.kk.msc.hcov.sdk.verificationtask.model.ProvidedContext;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.jena.ontology.OntModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class PizzaMenuContextProviderPluginTest {

  PizzaMenuContextProviderPlugin target;

  @BeforeEach
  void setUp() {
    target = new PizzaMenuContextProviderPlugin();
  }

  @Test
  public void testProvideContextFor_extractsContextPropertly() throws FileNotFoundException {
    // given
    UUID givenUUID = UUID.fromString("dade8122-78a1-4632-ad10-b3b76343d462");
    OntModel givenOntModel = TestUtils.loadVenezianaOntology();
    Map<String, Object> givenConfiguration = new HashMap<>();

    // when
    ProvidedContext actual = target.provideContextFor(givenUUID, givenOntModel, givenConfiguration);

    // then
    assertThat(actual).isNotNull();
    assertThat(actual.getExtractedElementsId()).isEqualTo(UUID.fromString("dade8122-78a1-4632-ad10-b3b76343d462"));
    assertThat(actual.getContextString())
        .isEqualTo(
            "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg\"Veneziana\"Caper, Mozzarella, Olive, Onion, Pine Kernels, Sultana, Tomato"
        );
  }

  @ParameterizedTest
  @ValueSource(strings = {"PIZZA_MENU_CONTEXT_PROVIDER", "pizza_menu_context_provider", "pIzZa_mEnU_CoNtExT_PrOvIdEr"})
  public void testSupports_givenSupportedStrings(String givenString) {
    // when - then
    assertThat(target.supports(givenString)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"PIZZA_MENU_CONTEXT_PROVIDERS", "pizza_menu_context_providers"})
  @NullAndEmptySource
  public void testSupports_givenUnsupportedStrings(String givenString) {
    // when - then
    assertThat(target.supports(givenString)).isFalse();
  }
}
