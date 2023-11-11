package edu.brown.cs.student.main.geoJsonParser;

import java.util.Map;

public class Properties {
  public String state;
  public String city;
  public String name;
  public Map<String, String> area_description_data;
  public String holc_grade;
  public String holc_id;
  public int neighborhood_id;

  public String getCity() {
    return this.city;
  }

  public String getName() {
    return this.name;
  }

  public String getState() {
    return this.state;
  }

  public String getAreaDescriptionData() {
    return this.area_description_data.toString();
  }

  public String getHolcGrade() {
    return this.holc_grade;
  }

  public String getHolcId() {
    return this.holc_id;
  }

  public int getNeighborhoodId() {
    return this.neighborhood_id;
  }

}