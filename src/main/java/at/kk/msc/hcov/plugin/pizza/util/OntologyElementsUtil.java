package at.kk.msc.hcov.plugin.pizza.util;

import java.util.Collection;
import java.util.List;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.util.iterator.ExtendedIterator;

public class OntologyElementsUtil {
  public static void copySomeValuesFromRestrictions(
      OntClass pizzaClass, OntModel subOntology, OntClass pizzaClassInSubontology, OntProperty hasToppingInSubontology
  ) {
    getSomeValuesFromRestrictionIterator(pizzaClass)
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

  private static ExtendedIterator<SomeValuesFromRestriction> getSomeValuesFromRestrictionIterator(OntClass pizzaClass) {
    return pizzaClass.listSuperClasses()
        .filterKeep(OntClass::isRestriction)
        .mapWith(OntClass::asRestriction)
        .filterKeep(restriction -> "hasTopping".equals(restriction.getOnProperty().getLocalName()))
        .filterKeep(Restriction::isSomeValuesFromRestriction)
        .mapWith(Restriction::asSomeValuesFromRestriction);
  }

  public static void copyAllValuesFromRestrictions(
      OntClass pizzaClass, OntModel subOntology, OntClass pizzaClassInSubontology, OntProperty hasToppingInSubontology
  ) {
    getAllValuesFromRestrictionIterator(pizzaClass)
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

  private static ExtendedIterator<AllValuesFromRestriction> getAllValuesFromRestrictionIterator(OntClass pizzaClass) {
    return pizzaClass.listSuperClasses()
        .filterKeep(OntClass::isRestriction)
        .mapWith(OntClass::asRestriction)
        .filterKeep(restriction -> "hasTopping".equals(restriction.getOnProperty().getLocalName()))
        .filterKeep(Restriction::isAllValuesFromRestriction)
        .mapWith(Restriction::asAllValuesFromRestriction);
  }

  public static List<String> getAllValuesFromRestrictionsOfToppinsAsStrings(OntModel ontModel) {
    return ontModel.listRestrictions()
        .filterKeep(Restriction::isAllValuesFromRestriction)
        .filterKeep(restriction -> "hasTopping".equals(restriction.getOnProperty().getLocalName()))
        .mapWith(Restriction::asAllValuesFromRestriction)
        .mapWith(AllValuesFromRestriction::getAllValuesFrom)
        .mapWith(StringUtil::extractToppingName)
        .toList()
        .stream()
        .flatMap(Collection::stream)
        .distinct()
        .sorted()
        .toList();
  }

  public static List<String> getSomeValueFromRestrictionsOfToppingsAsStrings(OntModel ontModel) {
    return ontModel.listRestrictions()
        .filterKeep(Restriction::isSomeValuesFromRestriction)
        .filterKeep(restriction -> "hasTopping".equals(restriction.getOnProperty().getLocalName()))
        .mapWith(Restriction::asSomeValuesFromRestriction)
        .mapWith(SomeValuesFromRestriction::getSomeValuesFrom)
        .mapWith(StringUtil::extractToppingName)
        .toList()
        .stream()
        .flatMap(Collection::stream)
        .distinct()
        .sorted()
        .toList();
  }
}
