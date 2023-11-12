package edu.brown.cs.student.main.geoJsonParser;

public class Feature {

  public String type;
  public Geometry geometry;
  public Properties properties;

  public Geometry getGeometry() {
    return this.geometry;
  }

  public Properties getProperties() {
    return this.properties;
  }
}
