package at.kk.msc.hcov.plugin.pizza;

import at.kk.msc.hcov.plugin.pizza.util.OntologyElementsUtil;
import at.kk.msc.hcov.plugin.pizza.util.StringUtil;
import at.kk.msc.hcov.sdk.verificationtask.IContextProviderPlugin;
import at.kk.msc.hcov.sdk.verificationtask.model.ProvidedContext;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Plugin that provides context for a given extracted Pizza SubOntology (for extraction see {@link RestrictionVerificationPlugin}).
 * The {@link ProvidedContext} contextString contains a random pizza image, the pizza name and the toppings seperated by ".
 */
@Component
public class PizzaMenuContextProviderPlugin implements IContextProviderPlugin {

  private static final Logger LOGGER = LoggerFactory.getLogger(PizzaMenuContextProviderPlugin.class);

  @Override
  public ProvidedContext provideContextFor(UUID uuid, OntModel ontModel, Map<String, Object> configuration) {
    String pizzaImageSource = "https://www.stockvault.net/data/2016/04/19/194159/preview16.jpg";

    String namedPizzaUri = "http://www.co-ode.org/ontologies/pizza/pizza.owl#NamedPizza";
    OntProperty hasToppingProperty = ontModel.getOntProperty("http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping");
    OntClass currentPizzaModel = ontModel.getOntClass(namedPizzaUri).listSubClasses().next();
    String currentPizzaName = StringUtil.camelCaseToHumanReadable(currentPizzaModel.getLocalName());


    String toppings = Stream.concat(
            OntologyElementsUtil.getSomeValueFromRestrictionsOfToppingsAsStrings(ontModel).stream(),
            OntologyElementsUtil.getSomeValueFromRestrictionsOfToppingsAsStrings(ontModel).stream()
        )
        .distinct()
        .collect(Collectors.joining(", "));

    return new ProvidedContext(uuid, pizzaImageSource + "\"" + currentPizzaName + "\"" + toppings);
  }

  @Override
  public boolean supports(String s) {
    return "PIZZA_MENU_CONTEXT_PROVIDER".equalsIgnoreCase(s);
  }
}
