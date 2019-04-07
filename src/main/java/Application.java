import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class Application {

    private static class Coordinate {
        private double lat;
        private double lng;

        public Coordinate(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

    private static final double EARTHS_RADIUS = 6378137.0;
    private static PlaceService placeService = new PlaceService();

    public static void main(String[] args) {

        ArrayList<Coordinate> points = new ArrayList<>();
        try {
            // Grabbing info from points.txt
            BufferedReader br = new BufferedReader(new FileReader("points.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(", ");
                points.add(new Coordinate(Double.parseDouble(temp[0]), Double.parseDouble(temp[1])));
            }
            br.close();

            // Going through each area and fetching restaurants. Writing results in text files.
            int i = 1;
            int startingI = 27; //Set this to whatever point you wanna start at, otherwise leave it at 1.
            for (Coordinate point : points) {
                if (i >= startingI) {
                    System.out.println("\n\nProcessing location " + i);
                    BufferedWriter generalOut = new BufferedWriter(new FileWriter("data/summary.txt", true));
                    BufferedWriter out = new BufferedWriter(new FileWriter("data/location_" + String.format("%03d", i) + ".txt"));
                    HashMap<String, Place> places = getAllRestaurants(point.lat, point.lng);
                    // Writing general info to summary file
                    generalOut.write(i + "\t" + point.lat + "\t" + point.lng + "\t" + places.size());
                    generalOut.newLine();
                    for (Place place : places.values()) {
                        out.write(place.toString());
                        out.newLine();
                    }
                    // Close both to make sure nothing is lost is case of failure.
                    out.close();
                    generalOut.close();
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, Place> getAllRestaurants(double lat, double lng) {
        double searchRadius = 1000;

        HashMap<String, Place> places = placeService.getPlacesFromCoordinates(Double.toString(lat), Double.toString(lng));
        System.out.println("Initial restaurant count:" + places.size());
        if (places.size() >= 60) {
            System.out.println("Additional areas will be scanned");
            double topLat = getLatitudeOffSet(lat, searchRadius / 2);
            double bottomLat = getLatitudeOffSet(lat, -searchRadius / 2);
            double leftLong = getLongitudeOffSet(lng, lat, -searchRadius / 2);
            double rightLong = getLongitudeOffSet(lng, lat, searchRadius / 2);
            places.putAll(placeService.getPlacesFromCoordinates(Double.toString(topLat), Double.toString(leftLong)));
            places.putAll(placeService.getPlacesFromCoordinates(Double.toString(topLat), Double.toString(lng)));
            places.putAll(placeService.getPlacesFromCoordinates(Double.toString(topLat), Double.toString(rightLong)));
            places.putAll(placeService.getPlacesFromCoordinates(Double.toString(lat), Double.toString(leftLong)));
            places.putAll(placeService.getPlacesFromCoordinates(Double.toString(lat), Double.toString(rightLong)));
            places.putAll(placeService.getPlacesFromCoordinates(Double.toString(bottomLat), Double.toString(leftLong)));
            places.putAll(placeService.getPlacesFromCoordinates(Double.toString(bottomLat), Double.toString(lng)));
            places.putAll(placeService.getPlacesFromCoordinates(Double.toString(bottomLat), Double.toString(rightLong)));
            System.out.println("Final results: " + places.size());
        }
        Iterator it = places.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Place> pair = (Map.Entry) it.next();
//            if (!pair.getValue().getAddress().contains("Montreal") || !pair.getValue().getAddress().contains("Montréal")) {
//                it.remove();
//            }

            double lat2 = pair.getValue().getLocation().getCoordinates().getLatitude();
            double lng2 = pair.getValue().getLocation().getCoordinates().getLongitude();
            if (gpsDistance(lat, lng, lat2, lng2) > searchRadius) {
                it.remove();
            }
        }
        System.out.println("Final filtered results: " + places.size());
        return places;
    }

    private static double gpsDistance(double lat1, double lng1, double lat2, double lng2) {
        double dlat = Math.toRadians(lat2 - lat1);
        double dlng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dlng / 2) * Math.sin(dlng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EARTHS_RADIUS * c;
        //System.out.println("point 1: " + lat1 + ", " + lng1);
        //System.out.println("point 2: " + lat2 + ", " + lng2);
        //System.out.println("Distance: " + d);
        return d;
    }

    private static double getLatitudeOffSet(double latitude, double offset) {
        double dlat = offset / EARTHS_RADIUS;
        return latitude + (dlat * 180.0 / Math.PI);
    }

    private static double getLongitudeOffSet(double longitude, double latitude, double offset) {
        double dlon = (offset / EARTHS_RADIUS) * (180.0 / Math.PI / Math.cos((Math.PI * latitude) / 180.0));
        double newLongitude = longitude + dlon;
        return newLongitude;
    }

}
