package at.kk.msc.hcov.plugin.pizza.representation;

import java.util.List;

public interface RepresentationProvider {

  void appendAllValuesFromString(List<String> someValuesFromStrings, List<String> allValuesFromStrings, StringBuilder stringBuilder);

  void appendSomeValuesFromString(List<String> someValuesFromStrings, StringBuilder stringBuilder);
  
}
