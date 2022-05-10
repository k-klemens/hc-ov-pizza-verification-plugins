package at.kk.msc.hcov.plugin.pizza;

import at.kk.msc.hcov.sdk.plugin.PluginConfigurationNotSetException;
import at.kk.msc.hcov.sdk.verificationtask.IVerificationTaskPlugin;
import at.kk.msc.hcov.sdk.verificationtask.model.ProvidedContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class RestrictionVerificationPlugin implements IVerificationTaskPlugin {

  private Map<String, Object> configuration;

  @Override
  public Function<OntModel, List<OntModel>> getElementExtractor() throws PluginConfigurationNotSetException {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  @Override
  public BiFunction<OntModel, ProvidedContext, Map<String, Object>> getTemplateVariableValueResolver()
      throws PluginConfigurationNotSetException {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  @Override
  public String getTemplate() throws PluginConfigurationNotSetException {
    throw new UnsupportedOperationException("Not yet implemented!");
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
  public boolean supports(String s) {
    throw new UnsupportedOperationException("Not yet implemented!");
  }
}
