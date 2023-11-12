package edu.brown.cs.student.main.geoJsonParser;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonParser {

  public FeatureCollection parse(String filePath) throws IOException {
    String json = new String(Files.readAllBytes(Paths.get(filePath)));

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<FeatureCollection> adapter = moshi.adapter(FeatureCollection.class);

    return adapter.fromJson(json);
  }
}
