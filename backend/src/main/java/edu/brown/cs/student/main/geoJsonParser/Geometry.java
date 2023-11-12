package edu.brown.cs.student.main.geoJsonParser;

import java.util.List;

public class Geometry {

  public String type;
  public List<List<List<List<Double>>>> coordinates;

  public String getType() {
    return this.type;
  }

  public List<List<List<List<Double>>>> getCoordinates() {
    return this.coordinates;
  }
}
