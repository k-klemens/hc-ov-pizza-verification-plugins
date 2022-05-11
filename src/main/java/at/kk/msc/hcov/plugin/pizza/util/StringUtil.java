package at.kk.msc.hcov.plugin.pizza.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.Resource;

public class StringUtil {
  public static List<String> extractToppingName(Resource restrictedResource) {
    if (restrictedResource.canAs(UnionClass.class)) {
      UnionClass unionClass = restrictedResource.as(UnionClass.class);
      return unionClass.listOperands().mapWith(Resource::getLocalName).mapWith(
          StringUtil::toHumanReadableTopping
      ).toList();

    } else {
      List<String> returnList = new ArrayList<>();
      returnList.add(
          toHumanReadableTopping(restrictedResource.getLocalName())
      );
      return returnList;
    }
  }

  private static String toHumanReadableTopping(String localName) {
    String replacedStr = localName.replace("Topping", "");
    return camelCaseToHumanReadable(replacedStr);
  }

  public static String camelCaseToHumanReadable(String camelCaseString) {
    return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(camelCaseString), " ");
  }
}
