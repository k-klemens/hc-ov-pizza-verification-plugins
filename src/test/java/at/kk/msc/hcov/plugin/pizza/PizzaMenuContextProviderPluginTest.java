package at.kk.msc.hcov.plugin.pizza;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class PizzaMenuContextProviderPluginTest {

  PizzaMenuContextProviderPlugin target;

  @BeforeEach
  void setUp() {
    target = new PizzaMenuContextProviderPlugin();
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
