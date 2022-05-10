package at.kk.msc.hcov.plugin.pizza.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;

public class RestrictionVerificationPluginUtil {
  public static void copySomeValuesFromRestrictions(
      OntClass pizzaClass, OntModel subOntology, OntClass pizzaClassInSubontology, OntProperty hasToppingInSubontology
  ) {
    pizzaClass.listSuperClasses()
        .filterKeep(OntClass::isRestriction)
        .mapWith(OntClass::asRestriction)
        .filterKeep(restriction -> "hasTopping".equals(restriction.getOnProperty().getLocalName()))
        .filterKeep(Restriction::isSomeValuesFromRestriction)
        .mapWith(Restriction::asSomeValuesFromRestriction)
        .forEach(
            restriction -> {
              SomeValuesFromRestriction someValuesFromRestrictionInSubontology =
                  subOntology.createSomeValuesFromRestriction(null, hasToppingInSubontology, pizzaClassInSubontology);
              OntClass createdSomeValuesFromResourceInSubontology =
                  subOntology.createClass(restriction.getSomeValuesFrom().getURI());
              someValuesFromRestrictionInSubontology.setSomeValuesFrom(createdSomeValuesFromResourceInSubontology);
              pizzaClassInSubontology.addSuperClass(someValuesFromRestrictionInSubontology);
            }
        );
  }

  public static void copyAllValuesFromRestrictions(
      OntClass pizzaClass, OntModel subOntology, OntClass pizzaClassInSubontology, OntProperty hasToppingInSubontology
  ) {
    pizzaClass.listSuperClasses()
        .filterKeep(OntClass::isRestriction)
        .mapWith(OntClass::asRestriction)
        .filterKeep(restriction -> "hasTopping".equals(restriction.getOnProperty().getLocalName()))
        .filterKeep(Restriction::isAllValuesFromRestriction)
        .mapWith(Restriction::asAllValuesFromRestriction)
        .forEach(
            restriction -> {
              if (restriction.getAllValuesFrom().canAs(UnionClass.class)) {
                UnionClass unionClass = restriction.getAllValuesFrom().as(UnionClass.class);

                List<OntClass> unionClassResourcesInSubontology = unionClass.listOperands().mapWith(
                    ontClass -> subOntology.createClass(ontClass.getURI())
                ).toList();

                RDFList unionMembers = subOntology.createList(unionClassResourcesInSubontology.listIterator());
                UnionClass unionClassInSubontology = subOntology.createUnionClass(null, unionMembers);

                AllValuesFromRestriction allValuesFromRestrictionInSubontology =
                    subOntology.createAllValuesFromRestriction(null, hasToppingInSubontology, pizzaClassInSubontology);
                allValuesFromRestrictionInSubontology.setAllValuesFrom(unionClassInSubontology);
                pizzaClassInSubontology.addSuperClass(allValuesFromRestrictionInSubontology);
              } else {
                AllValuesFromRestriction allValuesFromRestrictionInSubontology =
                    subOntology.createAllValuesFromRestriction(null, hasToppingInSubontology, pizzaClassInSubontology);
                OntClass createdAllValuesFromResourceInSubontology =
                    subOntology.createClass(restriction.getAllValuesFrom().getURI());
                allValuesFromRestrictionInSubontology.setAllValuesFrom(createdAllValuesFromResourceInSubontology);
                pizzaClassInSubontology.addSuperClass(allValuesFromRestrictionInSubontology);
              }
            }
        );
  }

  public static List<String> extractToppingName(Resource restrictedResource) {
    if (restrictedResource.canAs(UnionClass.class)) {
      UnionClass unionClass = restrictedResource.as(UnionClass.class);
      return unionClass.listOperands().mapWith(Resource::getLocalName).mapWith(
          RestrictionVerificationPluginUtil::toHumanReadableTopping
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

  public static String toRectorString(
      String pizzaName, List<String> someValuesFromStrings, List<String> allValuesFromStrings
  ) {
    StringBuilder stringBuilder = new StringBuilder(camelCaseToHumanReadable(pizzaName));
    stringBuilder.append(" pizzas have, amongst other things, ");

    String someValuesFromString = someValuesFromStrings.stream()
        .map(str -> "some ".concat(str).concat(" topping"))
        .collect(Collectors.joining(", and "));
    if (someValuesFromStrings.size() > 0) {
      stringBuilder.append(someValuesFromString);
    }

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
    stringBuilder.append(".");

    return stringBuilder.toString();
  }
}
