package edu.brown.cs.student.main.geoJsonParser;

import edu.brown.cs.student.main.geoJsonParser.Feature;
import java.util.List;

public class FeatureCollection {

  public String type;
  public List<Feature> features;


  public List<Feature> getFeatures() {
    return this.features;
  }
}
