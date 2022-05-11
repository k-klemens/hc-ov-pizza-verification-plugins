package at.kk.msc.hcov.plugin.pizza.representation;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class RectorRenderer implements RepresentationProvider {


  public void appendAllValuesFromString(
      List<String> someValuesFromStrings, List<String> allValuesFromStrings, StringBuilder stringBuilder
  ) {
    if (someValuesFromStrings.size() > 0 && allValuesFromStrings.size() > 0) {
      stringBuilder.append(", and also only ");
    } else if (allValuesFromStrings.size() > 0) {
      stringBuilder.append("only ");
    }

    if (allValuesFromStrings.size() == 1) {
      stringBuilder
          .append(allValuesFromStrings.get(0))
          .append(" topping");
    } else if (allValuesFromStrings.size() > 1) {
      List<String> allValuesFromStringExceptLast = allValuesFromStrings.subList(0, allValuesFromStrings.size() - 1);
      String lastAllValuesFromString = allValuesFromStrings.get(allValuesFromStrings.size() - 1);
      stringBuilder.append(StringUtils.join(allValuesFromStringExceptLast, ", "))
          .append(", and/or ")
          .append(lastAllValuesFromString)
          .append(" toppings");
    }
  }

  public void appendSomeValuesFromString(List<String> someValuesFromStrings, StringBuilder stringBuilder) {
    String someValuesFromString = someValuesFromStrings.stream()
        .map(str -> "some ".concat(str).concat(" topping"))
        .collect(Collectors.joining(", and "));
    if (someValuesFromStrings.size() > 0) {
      stringBuilder.append(someValuesFromString);
    }
  }
}
