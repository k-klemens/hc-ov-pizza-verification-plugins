package at.kk.msc.hcov.plugin.pizza.representation;

import at.kk.msc.hcov.plugin.pizza.util.StringUtil;
import java.util.List;

public class RepresentationRenderer {

  private RepresentationProvider representationProvider;

  public RepresentationRenderer(String mechanism) {
    switch (mechanism) {
      case "WARREN":
        this.representationProvider = new WarrenRenderer();
        break;
      case "RECTOR":
        this.representationProvider = new RectorRenderer();
        break;
    }
  }

  public String renderString(
      String pizzaName, List<String> someValuesFromStrings, List<String> allValuesFromStrings
  ) {
    StringBuilder stringBuilder = new StringBuilder(StringUtil.camelCaseToHumanReadable(pizzaName));
    stringBuilder.append(" pizzas have, amongst other things, ");

    representationProvider.appendSomeValuesFromString(someValuesFromStrings, stringBuilder);
    representationProvider.appendAllValuesFromString(someValuesFromStrings, allValuesFromStrings, stringBuilder);

    stringBuilder.append(".");

    return stringBuilder.toString();
  }

}
