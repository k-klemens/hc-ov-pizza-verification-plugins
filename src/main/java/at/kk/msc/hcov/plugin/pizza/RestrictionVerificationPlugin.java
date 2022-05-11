package at.kk.msc.hcov.plugin.pizza;

import at.kk.msc.hcov.plugin.pizza.representation.RepresentationRenderer;
import at.kk.msc.hcov.plugin.pizza.util.OntologyElementsUtil;
import at.kk.msc.hcov.plugin.pizza.util.ResourceFileLoader;
import at.kk.msc.hcov.plugin.pizza.util.StringUtil;
import at.kk.msc.hcov.sdk.plugin.PluginConfigurationNotSetException;
import at.kk.msc.hcov.sdk.verificationtask.IVerificationTaskPlugin;
import at.kk.msc.hcov.sdk.verificationtask.model.ProvidedContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.springframework.stereotype.Component;

@Component
public class RestrictionVerificationPlugin implements IVerificationTaskPlugin {

  private Map<String, Object> configuration;

  @Override
  public Function<OntModel, List<OntModel>> getElementExtractor() throws PluginConfigurationNotSetException {
    return ontModel -> {
      OntClass namedPizza = ontModel.getOntClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#NamedPizza");
      List<OntClass> pizzaClasses = namedPizza.listSubClasses().toList();

      List<OntModel> returnModels = new ArrayList<>();
      Property hasToppingsProperty = ontModel.getProperty("http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping");

      for (OntClass pizzaClass : pizzaClasses) {
        OntModel subOntology = ModelFactory.createOntologyModel();
        OntClass namedPizzaInSubontology = subOntology.createClass(namedPizza.getURI());
        OntClass pizzaClassInSubontology = subOntology.createClass(pizzaClass.getURI());
        pizzaClassInSubontology.addSuperClass(namedPizzaInSubontology);
        OntProperty hasToppingInSubontology = subOntology.createOntProperty(hasToppingsProperty.getURI());

        OntologyElementsUtil.copySomeValuesFromRestrictions(pizzaClass, subOntology, pizzaClassInSubontology,
            hasToppingInSubontology);
        OntologyElementsUtil.copyAllValuesFromRestrictions(pizzaClass, subOntology, pizzaClassInSubontology,
            hasToppingInSubontology);

        returnModels.add(subOntology);
      }
      return returnModels;
    };
  }

  @Override
  public BiFunction<OntModel, ProvidedContext, Map<String, Object>> getTemplateVariableValueResolver()
      throws PluginConfigurationNotSetException {
    validateConfigurationSetOrThrow();
    return (ontModel, providedContext) -> {
      String namedPizzaUri = "http://www.co-ode.org/ontologies/pizza/pizza.owl#NamedPizza";
      OntClass currentPizzaModel = ontModel.getOntClass(namedPizzaUri).listSubClasses().next();
      String currentPizzaName = StringUtil.camelCaseToHumanReadable(currentPizzaModel.getLocalName());

      List<String> allValuesFromStrings = OntologyElementsUtil.getAllValuesFromRestrictionsOfToppinsAsStrings(ontModel);
      List<String> someValuesFromStrings = OntologyElementsUtil.getSomeValueFromRestrictionsOfToppingsAsStrings(ontModel);

      Map<String, Object> returnMap = new HashMap<>();
      addProvidedContext(providedContext, returnMap);

      switch (configuration.get("REPRESENTATION_MECHANISM").toString()) {
        case "RECTOR" -> returnMap.put(
            "axiom",
            new RepresentationRenderer("RECTOR").renderString(currentPizzaName, someValuesFromStrings, allValuesFromStrings)
        );
        case "WARREN" -> returnMap.put(
            "axiom",
            new RepresentationRenderer("WARREN").renderString(currentPizzaName, someValuesFromStrings, allValuesFromStrings)
        );
      }

      return returnMap;
    };
  }

  @Override
  public String getTemplate() throws PluginConfigurationNotSetException {
    validateConfigurationSetOrThrow();
    return switch (configuration.get("REPRESENTATION_MECHANISM").toString()) {
      case "RECTOR" -> new ResourceFileLoader().loadFileAsString("/template/rector.html").replace("\n", "").replace("\r", "");
      case "WARREN" -> new ResourceFileLoader().loadFileAsString("/template/warren.html").replace("\n", "").replace("\r", "");
      default -> "";
    };
  }

  @Override
  public void setConfiguration(Map<String, Object> map) {
    this.configuration = map;
  }

  @Override
  public Map<String, Object> getConfiguration() {
    return this.configuration;
  }

  @Override
  public void validateConfigurationSetOrThrow() throws PluginConfigurationNotSetException {
    IVerificationTaskPlugin.super.validateConfigurationSetOrThrow();
    if (!getConfiguration().containsKey("REPRESENTATION_MECHANISM")) {
      throw new PluginConfigurationNotSetException("Plugin configuration: REPRESENTATION_MECHANISM needs to be set!");
    } else if (getConfiguration().get("REPRESENTATION_MECHANISM") == null) {
      throw new PluginConfigurationNotSetException(
          "Given representation 'null' mechanism not known!"
      );
    } else if (
        !getConfiguration().get("REPRESENTATION_MECHANISM").equals("WARREN") &&
            !getConfiguration().get("REPRESENTATION_MECHANISM").equals("RECTOR")
    ) {
      throw new PluginConfigurationNotSetException(
          "Given representation '" + getConfiguration().get("REPRESENTATION_MECHANISM") + "' mechanism not known!"
      );
    }
  }

  @Override
  public boolean supports(String s) {
    return "RESTRICTION_TASK_CREATOR".equalsIgnoreCase(s);
  }

  private void addProvidedContext(ProvidedContext providedContext, Map<String, Object> returnMap) {
    if (
        providedContext != null
            && providedContext.getContextString() != null
            && providedContext.getContextString().split("\"").length == 3
    ) {
      String[] providedContextParts = providedContext.getContextString().split("\"");
      returnMap.put("imageURI", providedContextParts[0]);
      returnMap.put("pizzaName", providedContextParts[1]);
      returnMap.put("ingredientList", providedContextParts[2]);
    }
  }
}
