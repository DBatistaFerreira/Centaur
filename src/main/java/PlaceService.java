import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class PlaceService {

    private static final String KEY = "your_api_key";
    public static final String SEARCH_RADIUS = "1000";

    private static Logger logger = Logger.getLogger(PlaceService.class.getName());

    private String nextPageToken;

    private HashMap<String, Place> places = new HashMap<>();

    public HashMap<String, Place> getPlacesFromCoordinates(String latitude, String longitude) {
        logger.info("Starting scanning " + SEARCH_RADIUS + " meters from latitude: " + latitude + " , longitude: " + longitude);
        nextPageToken = "";
        putPlaces(latitude, longitude, "");
        String extraParams = "&pagetoken=" + nextPageToken;
        try {
            Thread.sleep(2000);
            putPlaces(latitude, longitude, extraParams);
            extraParams = "&pagetoken=" + nextPageToken;
            Thread.sleep(2000);
            putPlaces(latitude, longitude, extraParams);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Finished scanning " + SEARCH_RADIUS + " meters from latitude: " + latitude + " , longitude: " + longitude);
        return places;
    }

    private HashMap<String, Place> putPlaces(String latitude, String longitude, String extraParams) {
        try {
            String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + SEARCH_RADIUS + "&type=restaurant&key=" + KEY + extraParams;
            System.out.println(URL);
            String response = RESTConsumer.get(URL);
            if (Objects.isNull(response)) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("results"));

            try {
                nextPageToken = jsonObject.getString("next_page_token");
            } catch (Exception e) {
                nextPageToken = "";
            }

            Gson gson = new Gson();
            ArrayList<Place> placeList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                placeList.add(gson.fromJson(jsonArray.get(i).toString(), Place.class));
                Place currentPlace = placeList.get(i);
                places.put(currentPlace.getId(), currentPlace);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return places;
    }
}
