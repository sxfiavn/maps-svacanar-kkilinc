package edu.brown.cs.student.main.server.datasource.redline_caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.server.datasource.redline_no_caching.GeoJsonDataSource;
import edu.brown.cs.student.main.server.datasource.redline_no_caching.JsonData;
import java.util.concurrent.TimeUnit;

public class CachedJsonDataSource implements GeoJsonDataSource {
  private final GeoJsonDataSource wrappedSource;
  private final LoadingCache<String, JsonData> cache;

  public CachedJsonDataSource(GeoJsonDataSource toWrap, int maxSize, int expirationMinutes) {
    this.wrappedSource = toWrap;

    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expirationMinutes, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public JsonData load(String key) {
                    String[] parts = key.split("_");
                    return wrappedSource.searchFeaturesInBoundingBox(
                        Double.parseDouble(parts[0]),
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]));
                  }
                });
  }

  @Override
  public JsonData searchFeaturesInBoundingBox(
      double lowerX, double lowerY, double upperX, double upperY) {
    return this.cache.getUnchecked(lowerX + "_" + lowerY + "_" + upperX + "_" + upperY);
  }
}
