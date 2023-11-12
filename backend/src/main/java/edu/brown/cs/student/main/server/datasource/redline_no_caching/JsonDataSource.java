package edu.brown.cs.student.main.server.datasource.redline_no_caching;

import edu.brown.cs.student.main.geoJsonParser.Feature;
import edu.brown.cs.student.main.geoJsonParser.FeatureCollection;
import edu.brown.cs.student.main.geoJsonParser.Geometry;
import edu.brown.cs.student.main.geoJsonParser.Properties;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class JsonDataSource implements GeoJsonDataSource {

  private boolean isInBoundingBox(
      List<Double> coordinate, double lowerX, double lowerY, double upperX, double upperY) {
    double x = coordinate.get(0);
    double y = coordinate.get(1);
    return x >= lowerX && x <= upperX && y >= lowerY && y <= upperY;
  }

  public JsonData searchFeaturesInBoundingBox(
      double lowerX, double lowerY, double upperX, double upperY) {
    FeatureCollection featureCollection = DataStorage.getCurrentJsonData();
    ArrayList<ArrayList<String>> featuresInfo = new ArrayList<>();
    ArrayList<String> headers = new ArrayList<>();
    headers.add("City");
    headers.add("State");
    headers.add("HOLC Grade");
    headers.add("HOLC ID");
    headers.add("Neighborhood ID");
    headers.add("Latitude");
    headers.add("Longitude");
    featuresInfo.add(headers);

    for (Feature feature : featureCollection.getFeatures()) {
      Geometry geometry = feature.getGeometry();
      if (geometry == null) {
        continue;
      }

      if ("MultiPolygon".equals(geometry.getType())) {
        outerloop:
        for (List<List<List<Double>>> polygon : geometry.getCoordinates()) {
          for (List<List<Double>> ring : polygon) {
            for (List<Double> coordinates : ring) {
              if (isInBoundingBox(coordinates, lowerX, lowerY, upperX, upperY)) {
                ArrayList<String> featureInfo = getStrings(feature, coordinates);
                featuresInfo.add(featureInfo);
                break outerloop;
              }
            }
          }
        }
      }
    }
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = now.format(formatter);

    return new JsonData(featuresInfo, null, formattedDateTime);
  }

  @NotNull
  private ArrayList<String> getStrings(Feature feature, List<Double> coord) {
    ArrayList<String> featureInfo = new ArrayList<>();
    Properties properties = feature.getProperties();
    featureInfo.add(properties.getCity());
    featureInfo.add(properties.getState());
    featureInfo.add(properties.getHolcGrade());
    featureInfo.add(properties.getHolcId());
    featureInfo.add(String.valueOf(properties.getNeighborhoodId()));
    featureInfo.add(String.valueOf(coord.get(1))); // lat
    featureInfo.add(String.valueOf(coord.get(0))); // long
    return featureInfo;
  }
}
