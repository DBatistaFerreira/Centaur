import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Stream;

public class Application {

    private static final double EARTHS_RADIUS = 6378137;
    private static final double RADIUS_OF_SEARCH_LATITUDE = 500; // meters
    private static final double RADIUS_OF_SEARCH_LONGITUDE = 250; // meters
    private static PlaceService placeService = new PlaceService();

    private static FileOutputStream fos;
    private static ObjectOutputStream oos;

    public static void main(String[] args) throws IOException {
        //HashMap<String, Place> places = placeService.getPlacesFromCoordinates("45.485340","-73.621447");

        HashMap<String, Place> places = getAllWestmountRestaurants();
        places.values().stream().forEach(System.out::println);
        System.out.println("Amount of restaurants: " + places.size());
    }

    //Westmount has specifically Westmount and not montreal in their address
    public static HashMap<String, Place> getAllWestmountRestaurants(){

        //Places all of Westmount into a Square
        final double TOP_LEFT_LATITUDE = 45.485340;
        final double TOP_LEFT_LONGITUDE = -73.621447;
        final double TOP_RIGHT_LATITUDE = 45.496558;
        final double TOP_RIGHT_LONGITUDE = -73.621447;
        final double BOTTOM_LEFT_LATITUDE = 45.473787;
        final double BOTTOM_LEFT_LONGITUDE = -73.597991;
        final double BOTTOM_RIGHT_LATITUDE = 45.485031;
        final double BOTTOM_RIGHT_LONGITUDE = -73.597991;

        double currentLatitude = TOP_LEFT_LATITUDE;
        double currentLongitude = TOP_LEFT_LONGITUDE;

        double nextLatitude = getLatitudeOffSet(TOP_LEFT_LATITUDE, RADIUS_OF_SEARCH_LATITUDE);
        double nextLongitude = getLongitudeOffSet(TOP_LEFT_LONGITUDE, TOP_LEFT_LATITUDE, RADIUS_OF_SEARCH_LONGITUDE);

        //Get initial position restaurants
        HashMap<String, Place> places = placeService.getPlacesFromCoordinates(Double.toString(TOP_LEFT_LATITUDE), Double.toString(TOP_LEFT_LONGITUDE));

        double currentLatitudeBounds = TOP_RIGHT_LATITUDE;

        while(currentLongitude < BOTTOM_LEFT_LONGITUDE){
            while(currentLatitude < currentLatitudeBounds){
                currentLatitude = nextLatitude;
                places.putAll(placeService.getPlacesFromCoordinates(Double.toString(currentLatitude), Double.toString(currentLongitude)));
                nextLatitude = getLatitudeOffSet(currentLatitude, RADIUS_OF_SEARCH_LATITUDE);
            }
            currentLatitude = TOP_LEFT_LATITUDE;
            nextLatitude = TOP_LEFT_LATITUDE;
            currentLongitude = nextLongitude;
            nextLongitude = getLongitudeOffSet(currentLongitude, currentLatitude, RADIUS_OF_SEARCH_LONGITUDE);
        }

        Iterator it = places.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,Place> pair = (Map.Entry)it.next();
            if(!pair.getValue().getAddress().toLowerCase().contains("westmount")){
                it.remove();
            }
        }

        return places;
    }

    //https://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
    //Source for the following formulas
    private static double getLatitudeOffSet(double latitude, double radius){
        double dlat = radius/EARTHS_RADIUS;
        return latitude + (dlat * 180/Math.PI);
    }

    private static double getLongitudeOffSet(double longitude, double latitude, double radius){
        double dlon = radius/(EARTHS_RADIUS * Math.cos(Math.PI * latitude/180));
        double newLongitude = longitude + (dlon * 180/Math.PI);
        return newLongitude;
}
}
