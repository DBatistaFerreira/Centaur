import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Place implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("vicinity")
    private String address;

    @SerializedName("rating")
    private double rating;

    @SerializedName("geometry")
    private Location location;

    public class Location implements Serializable {
        @SerializedName("location")
        private Coordinates coordinates;

        public Coordinates getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
        }

        public class Coordinates implements Serializable {
            @SerializedName("lat")
            private double latitude;

            @SerializedName("lng")
            private double longitude;

            public double getLatitude() {
                return latitude;
            }

            public double getLongitude() {
                return longitude;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "[" + name + ", " + address + ", (" + location.coordinates.getLatitude() + ", " + location.getCoordinates().getLongitude() + ")" + "]";
    }
}
