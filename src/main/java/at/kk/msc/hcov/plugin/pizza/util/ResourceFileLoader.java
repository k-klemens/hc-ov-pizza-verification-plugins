package at.kk.msc.hcov.plugin.pizza.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public class ResourceFileLoader {

  public String loadFileAsString(String pathInResources) {
    try {
      return IOUtils.toString(this.getClass().getResourceAsStream(pathInResources), StandardCharsets.UTF_8);
    } catch (IOException e) {
      return "";
    }
  }

}
