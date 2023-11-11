package edu.brown.cs.student.main.server.datasource.redline_no_caching;

import java.util.ArrayList;

public interface GeoJsonDataSource {

  JsonData searchFeaturesInBoundingBox(double lowerX, double lowerY, double upperX, double upperY);


  }
