package server.caching;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;


/**
 * Class utilizing Google's Guava library for caching
 */
public class ApiCallCache {
  private Cache<String, String> ApiCallCache;

  /**
   * Constructor forApiCall class, initializing the ApiCallCache
   */
  private void ApiCallCache(){
    ApiCallCache = buildCache();
  }

  /**
   * Builds and configures a caching mechanism for storing JSON data in string format.
   * Key: actual request as "County, State", Value: JSON in string format so "{}"
   *
   * @return A configured cache for storing JSON data as strings.
   */
  private Cache<String, String> buildCache(){
    return CacheBuilder.newBuilder()
        // As a backend developer, you can change the values below if you wish to:
        .maximumSize(50) // Maximum number of entries to store in the cache
        .expireAfterWrite(10, TimeUnit.MINUTES) // Expiration time for cache entries
        .build();
  }

  public Cache<String, String> getCache(){
    return this.ApiCallCache;
  }
}
