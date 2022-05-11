package at.kk.msc.hcov.plugin.pizza.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

public class TestUtils {

  public static OntModel loadPizzaOntology() throws FileNotFoundException {
    OntModel ontModel = ModelFactory.createOntologyModel();

    InputStream inputStream = new FileInputStream("src/test/resources/pizza.owl.xml");
    ontModel.read(inputStream, Lang.RDFXML.getLabel());
    return ontModel;
  }

  public static OntModel loadVenezianaOntology() throws FileNotFoundException {
    OntModel ontModel = ModelFactory.createOntologyModel();

    InputStream inputStream = new FileInputStream("src/test/resources/Veneziana.owl.xml");
    ontModel.read(inputStream, Lang.RDFXML.getLabel());
    return ontModel;
  }

  public static OntModel loadVenezianaOntology_onlyAllValuesFrom() throws FileNotFoundException {
    OntModel ontModel = ModelFactory.createOntologyModel();

    InputStream inputStream = new FileInputStream("src/test/resources/Veneziana-WithoutSomeValuesFrom.owl.xml");
    ontModel.read(inputStream, Lang.RDFXML.getLabel());
    return ontModel;
  }

  public static OntModel loadVenezianaOntology_onlySomeValuesFrom() throws FileNotFoundException {
    OntModel ontModel = ModelFactory.createOntologyModel();

    InputStream inputStream = new FileInputStream("src/test/resources/Veneziana-WithoutAllValuesFrom.owl.xml");
    ontModel.read(inputStream, Lang.RDFXML.getLabel());
    return ontModel;
  }

  public static void printRestrictionsOnPizza(List<OntModel> actual) {
    for (OntModel ontModel : actual) {
      System.out.println("----------------------");
      for (Iterator<OntClass> supers =
           ontModel.getOntClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#NamedPizza").listSubClasses().toList().get(0)
               .listSuperClasses(); supers.hasNext(); ) {
        OntClass currentRestriction = supers.next();
        String pizzaName = currentRestriction.listSubClasses().toList().get(0).getLocalName();
        displayType(pizzaName, currentRestriction);
      }
    }
  }


  private static void displayType(String pizzaName, OntClass sup) {
    /* SOURCE: https://stackoverflow.com/questions/7779927/get-owl-restrictions-on-classes-using-jena */
    if (sup.isRestriction()) {
      displayRestriction(pizzaName, sup.asRestriction());
    }
  }

  private static void displayRestriction(String pizzaName, Restriction sup) {
    /* SOURCE: https://stackoverflow.com/questions/7779927/get-owl-restrictions-on-classes-using-jena */
    if (sup.isAllValuesFromRestriction()) {
      displayRestriction(pizzaName, "all", sup.getOnProperty(), sup.asAllValuesFromRestriction().getAllValuesFrom());
    } else if (sup.isSomeValuesFromRestriction()) {
      displayRestriction(pizzaName, "some", sup.getOnProperty(), sup.asSomeValuesFromRestriction().getSomeValuesFrom());
    }
  }

  private static void displayRestriction(String pizzaName, String qualifier, OntProperty onP, Resource constraint) {
    /* SOURCE: https://stackoverflow.com/questions/7779927/get-owl-restrictions-on-classes-using-jena */
    String out = String.format("%s %s %s",
        qualifier, renderURI(onP), renderConstraint(constraint));
    System.out.println("'" + pizzaName + "' pizza: " + out);
  }

  private static Object renderConstraint(Resource constraint) {
    /* SOURCE: https://stackoverflow.com/questions/7779927/get-owl-restrictions-on-classes-using-jena */
    if (constraint.canAs(UnionClass.class)) {
      UnionClass uc = constraint.as(UnionClass.class);
      String r = "union{ ";
      for (Iterator<? extends OntClass> i = uc.listOperands(); i.hasNext(); ) {
        r = r + " " + renderURI(i.next());
      }
      return r + "}";
    } else {
      return renderURI(constraint);
    }
  }

  private static Object renderURI(Resource onP) {
    /* SOURCE: https://stackoverflow.com/questions/7779927/get-owl-restrictions-on-classes-using-jena */
    String qName = onP.getModel().qnameFor(onP.getURI());
    return qName == null ? onP.getLocalName() : qName;
  }
}
