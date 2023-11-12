package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.geoJsonParser.FeatureCollection;
import edu.brown.cs.student.main.geoJsonParser.GeoJsonNameSearch;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the incoming request to retrieve GeoJSON with properties information that match the
 * request information. It allows searching for areas with descriptions containing a keyword,
 * which will be highlighted on the map.
 */
public class SearchAreaHandler implements Route {

    /**
     * Handles the incoming request to retrieve GeoJSON with properties information that match the
     * request information.
     *
     * @param request  The incoming request.
     * @param response The outgoing response.
     * @return JSON-formatted string containing the retrieved data or an error message.
     */
    @Override
    public Object handle(Request request, Response response) {
  
      // Create a map to store the response data
      Map<String, Object> responseMap = new HashMap<>();
  
      // Collect all query parameters into a list
      List<String> parameters = new ArrayList<>();
      request.queryParams().forEach(param -> parameters.add(request.queryParams(param)));
  
        Moshi m = new Moshi.Builder().build();
        Type map = Types.newParameterizedType(Map.class, String.class, Object.class);
  
      try {
      
        return m.adapter(map).toJson(Map.of(
      "result", "success",
      "param_list", parameters,
      "geoJson", m.adapter(FeatureCollection.class).toJson(
          new GeoJsonNameSearch(DataStorage.getCurrentJsonData())
              .searchArea(parameters)
      )
  ));
  
      } catch (Exception e) {
        responseMap.put("result", "error_datasource");
        responseMap.put("details", "An error occurred while processing GeoJSON data: " + e.getMessage());
        
        return m.adapter(map).toJson(responseMap);
      }
    }
  }