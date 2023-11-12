package edu.brown.cs.student.main.server.datasource.redline_no_caching;

public interface GeoJsonDataSource {

  JsonData searchFeaturesInBoundingBox(double lowerX, double lowerY, double upperX, double upperY);
}
