import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Application {

    private static final double EARTHS_RADIUS = 6378137.0;
    private static final double RADIUS_OF_SEARCH_LATITUDE = 500.0; // meters
    private static final double RADIUS_OF_SEARCH_LONGITUDE = 250.0; // meters
    private static PlaceService placeService = new PlaceService();

    public static void main(String[] args) {
        //HashMap<String, Place> places = placeService.getPlacesFromCoordinates("45.485340","-73.621447");

        HashMap<String, Place> places = getAllWestmountRestaurants();
        places.values().stream().forEach(System.out::println);
        System.out.println("Amount of restaurants: " + places.size());
    }


    //Westmount has specifically Westmount and not montreal in their address
    public static HashMap<String, Place> getAllWestmountRestaurants(){

        //Places all of Westmount into a Square
        final double TOP_LEFT_LATITUDE = 45.495642; //x1
        final double TOP_LEFT_LONGITUDE = -73.618438; //y1
        final double BOTTOM_RIGHT_LATITUDE = 45.473589; //x2
        final double BOTTOM_RIGHT_LONGITUDE = -73.580635; //y2

        double currentLatitude = TOP_LEFT_LATITUDE;
        double currentLongitude = TOP_LEFT_LONGITUDE;

        double nextLatitude = getLatitudeOffSet(TOP_LEFT_LATITUDE, RADIUS_OF_SEARCH_LATITUDE);
        double nextLongitude = getLongitudeOffSet(TOP_LEFT_LONGITUDE, TOP_LEFT_LATITUDE, RADIUS_OF_SEARCH_LONGITUDE);

        //Get initial position restaurants
        HashMap<String, Place> places = placeService.getPlacesFromCoordinates(Double.toString(TOP_LEFT_LATITUDE), Double.toString(TOP_LEFT_LONGITUDE));

        double currentLatitudeBounds = BOTTOM_RIGHT_LATITUDE;

        while(currentLongitude < BOTTOM_RIGHT_LONGITUDE){
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
            if(pair.getValue().getAddress().contains("Montréal") || pair.getValue().getAddress().contains("Montreal")){
                it.remove();
            }
        }

        return places;
    }

    //https://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
    //Source for the following formulas
    private static double getLatitudeOffSet(double latitude, double radius){
        double dlat = radius/EARTHS_RADIUS;
        return latitude + (dlat * 180.0/Math.PI);
    }

    private static double getLongitudeOffSet(double longitude, double latitude, double radius){
        double dlon = (radius/EARTHS_RADIUS) * (180.0/Math.PI / Math.cos((Math.PI * latitude)/180.0));
        double newLongitude = longitude + dlon;
        return newLongitude;
}
}
