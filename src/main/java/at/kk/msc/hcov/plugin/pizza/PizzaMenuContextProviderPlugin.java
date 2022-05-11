package at.kk.msc.hcov.plugin.pizza;

import at.kk.msc.hcov.sdk.verificationtask.IContextProviderPlugin;
import at.kk.msc.hcov.sdk.verificationtask.model.ProvidedContext;
import java.util.Map;
import java.util.UUID;
import org.apache.jena.ontology.OntModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PizzaMenuContextProviderPlugin implements IContextProviderPlugin {

  private static final Logger LOGGER = LoggerFactory.getLogger(PizzaMenuContextProviderPlugin.class);

  @Override
  public ProvidedContext provideContextFor(UUID uuid, OntModel ontModel, Map<String, Object> map) {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  @Override
  public boolean supports(String s) {
    return "PIZZA_MENU_CONTEXT_PROVIDER".equalsIgnoreCase(s);
  }
}
