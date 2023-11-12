package edu.brown.cs.student.main.geoJsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class geoJsonNameSearch {
  private FeatureCollection featureCollection;

  public geoJsonNameSearch(FeatureCollection featureCollection) {
    if (featureCollection == null) {
      throw new IllegalArgumentException(
          "The FeatureCollection provided to geoJsonNameSearch constructor was null.");
    }
    this.featureCollection = featureCollection;
  }

  public FeatureCollection searchArea(List<String> paramList) throws IllegalArgumentException {
    if (paramList == null || paramList.isEmpty()) {
      throw new IllegalArgumentException("The parameter list for searchArea was null or empty.");
    }

    FeatureCollection matchedFeatures = new FeatureCollection();
    matchedFeatures.type = "FeatureCollection";
    matchedFeatures.features = new ArrayList<>();

    for (Feature feature : this.featureCollection.features) {
      if (featureMatchesParams(feature.properties, paramList)) {
        matchedFeatures.features.add(feature);
      }
    }

    return matchedFeatures;
  }

  private boolean featureMatchesParams(Properties properties, List<String> paramList) {
    // Convert all parameters to lowercase for case-insensitive matching
    List<String> lowerCaseParams =
        paramList.stream().map(String::toLowerCase).collect(Collectors.toList());

    if (properties.state != null && containsIgnoreCase(properties.state, lowerCaseParams)) {
      return true;
    }
    if (properties.city != null && containsIgnoreCase(properties.city, lowerCaseParams)) {
      return true;
    }
    if (properties.name != null && containsIgnoreCase(properties.name, lowerCaseParams)) {
      return true;
    }
    if (properties.area_description_data != null) {
      // Using streams to check for any match in the description data
      return properties.area_description_data.values().stream()
          .anyMatch(description -> containsIgnoreCase(description, lowerCaseParams));
    }
    // If none of the properties match, return false.
    return false;
  }

  // Helper method to check if the given value contains any of the parameters, ignoring case
  private boolean containsIgnoreCase(String value, List<String> lowerCaseParams) {
    String lowerCaseValue = value.toLowerCase();
    return lowerCaseParams.stream().anyMatch(lowerCaseValue::contains);
  }
}
