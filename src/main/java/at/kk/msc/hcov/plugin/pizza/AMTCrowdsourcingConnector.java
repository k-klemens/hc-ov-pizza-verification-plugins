package at.kk.msc.hcov.plugin.pizza;

import at.kk.msc.hcov.sdk.crowdsourcing.platform.ICrowdsourcingConnectorPlugin;
import at.kk.msc.hcov.sdk.crowdsourcing.platform.model.HitStatus;
import at.kk.msc.hcov.sdk.crowdsourcing.platform.model.RawResult;
import at.kk.msc.hcov.sdk.plugin.PluginConfigurationNotSetException;
import at.kk.msc.hcov.sdk.verificationtask.model.VerificationTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AMTCrowdsourcingConnector implements ICrowdsourcingConnectorPlugin {

  private static final Logger LOGGER = LoggerFactory.getLogger(AMTCrowdsourcingConnector.class);
  private Map<String, Object> configuration;

  @Override
  public Map<UUID, String> publishTasks(List<VerificationTask> list) throws PluginConfigurationNotSetException {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  @Override
  public Map<String, HitStatus> getStatusForHits(List<String> list) throws PluginConfigurationNotSetException {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  @Override
  public Map<String, List<RawResult>> getResultsForHits(List<String> list) throws PluginConfigurationNotSetException {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  @Override
  public void validateConfigurationSetOrThrow() throws PluginConfigurationNotSetException {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  @Override
  public void setConfiguration(Map<String, Object> map) {
    configuration = map;
  }

  @Override
  public Map<String, Object> getConfiguration() {
    return configuration;
  }

  @Override
  public boolean supports(String s) {
    throw new UnsupportedOperationException("Not yet implemented!");
  }
}
