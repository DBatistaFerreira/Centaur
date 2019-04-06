import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Application {

    private static final double EARTHS_RADIUS = 6378137.0;
    private static final double RADIUS_OF_SEARCH_LATITUDE = 300.0; // meters Y
    private static final double RADIUS_OF_SEARCH_LONGITUDE = 300.0; // meters X
    private static PlaceService placeService = new PlaceService();

    public static void main(String[] args) {
        //HashMap<String, Place> places = placeService.getPlacesFromCoordinates("45.485340","-73.621447");

        //Scanning Westmount
//        HashMap<String, Place> places = getAllRestaurants(45.495642, -73.618438, 45.473589, -73.580635, "Westmount");

        //Scanning Cote-st-luc
//        HashMap<String, Place> places = getAllRestaurants(45.493075, -73.687729, 45.451900, -73.629805, "Côte Saint-Luc");

        //Scanning Cote-st-luc
//        HashMap<String, Place> places = getAllRestaurants(45.483375, -73.847156, 45.424986,  -73.778919, "Pointe-Claire");

        //Region 1
//        HashMap<String, Place> places = getAllRestaurants(  45.565493, -73.617141,45.546348, -73.586719, "Region 1");

        //Region 2
//        HashMap<String, Place> places = getAllRestaurants(  45.457518, -73.895904,45.448086, -73.876126, "Region 2");

        //Region 3
//        HashMap<String, Place> places = getAllRestaurants( 45.459143, -73.757763,45.445739, -73.740739, "Region 3");

        //Region 4
//        HashMap<String, Place> places = getAllRestaurants( 45.459143, -73.757763,45.445739, -73.740739, "Region 3");

        //Region 5
        HashMap<String, Place> places = getAllRestaurants(45.505457, -73.579227, 45.492868, -73.560976, "Region 3");


        places.values().stream().forEach(System.out::println);
        System.out.println("Amount of restaurants: " + places.size());
    }


    //Westmount has specifically Westmount and not montreal in their address
    public static HashMap<String, Place> getAllRestaurants(double topLeftLatitude, double topLeftLongitude, double bottomRightLatitude, double bottomRightLongitude, String city) {

        final double TOP_LEFT_LATITUDE = topLeftLatitude;
        final double TOP_LEFT_LONGITUDE = topLeftLongitude;
        final double BOTTOM_RIGHT_LATITUDE = bottomRightLatitude;
        final double BOTTOM_RIGHT_LONGITUDE = bottomRightLongitude;

        double currentLatitude = TOP_LEFT_LATITUDE;
        double currentLongitude = TOP_LEFT_LONGITUDE;

        double nextLatitude = currentLatitude;
        double nextLongitude = currentLongitude;
        //Get initial position restaurants
        HashMap<String, Place> places = placeService.getPlacesFromCoordinates(Double.toString(TOP_LEFT_LATITUDE), Double.toString(TOP_LEFT_LONGITUDE));

        while (currentLatitude > BOTTOM_RIGHT_LATITUDE) {
            while (currentLongitude < BOTTOM_RIGHT_LONGITUDE) {
                places.putAll(placeService.getPlacesFromCoordinates(Double.toString(currentLatitude), Double.toString(currentLongitude)));
                nextLongitude = getLongitudeOffSet(currentLongitude, currentLatitude, RADIUS_OF_SEARCH_LONGITUDE);
                currentLongitude = nextLongitude;
            }
            nextLatitude = getLatitudeOffSet(currentLatitude, RADIUS_OF_SEARCH_LATITUDE);
            currentLatitude = nextLatitude;
            currentLongitude = TOP_LEFT_LONGITUDE;
        }

        Iterator it = places.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Place> pair = (Map.Entry) it.next();
//            if (!pair.getValue().getAddress().contains("Montreal") || !pair.getValue().getAddress().contains("Montréal")) {
//                it.remove();
//            }

            //remove this and fix names if you want the actual city ones
            double lat = pair.getValue().getLocation().getCoordinates().getLatitude();
            double lon = pair.getValue().getLocation().getCoordinates().getLongitude();
            if ((lat > topLeftLatitude) || (lat < bottomRightLatitude) || (lon < topLeftLongitude) || (lon > bottomRightLongitude)) {
                it.remove();
            }
        }
        // To remove based on lat/long:
        // lat needs to be greater than topleftlat
        // or lat needs to be smaller than bottomrightlat
        // or long needs to be smaller than topleftlong
        // or long needs to be larger than bottomrightlong
        return places;
    }

    //https://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
    //Source for the following formulas
    private static double getLatitudeOffSet(double latitude, double radius) {
        double dlat = radius / EARTHS_RADIUS;
        return latitude - (dlat * 180.0 / Math.PI);
    }

    private static double getLongitudeOffSet(double longitude, double latitude, double radius) {
        double dlon = (radius / EARTHS_RADIUS) * (180.0 / Math.PI / Math.cos((Math.PI * latitude) / 180.0));
        double newLongitude = longitude + dlon;
        return newLongitude;
    }
}
