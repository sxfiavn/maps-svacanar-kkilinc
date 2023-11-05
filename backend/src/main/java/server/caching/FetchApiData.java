package server.caching;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class FetchApiData {
  private List<List<String>> data;


  /**
   * Constructor with state and county maps
   */
  public FetchApiData(String URL){

    data = FetchData(URL);
  }


  /**
   * Fetches data from a given URL and returns it as a list of lists of strings.
   *
   * @param URL The URL to fetch data from.
   * @return A list of lists of strings representing the fetched data.
   */
  private List<List<String>> FetchData(String URL) {
    try {
      // Fetch data from the API
      java.net.URL url = new URL(URL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) { //use enum
        InputStreamReader stream = new InputStreamReader(connection.getInputStream());
        BufferedReader buf = new BufferedReader(stream);
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = buf.readLine()) != null) {
          response.append(inputLine);
        }
        buf.close();

        // Parse the API response using Moshi
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(
            Types.newParameterizedType(List.class, List.class, String.class));

        return adapter.fromJson(response.toString());

      } else {
        System.out.println("Failed to fetch data from the API.");
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Getter for "data"
   *
   * @return data field in constructor (FetchData output for URL)
   */
  public List<List<String>> getData(){
    return data;
  }

}
