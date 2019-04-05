import com.google.gson.annotations.SerializedName;

public class Place {

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

    private class Location {
        @SerializedName("location")
        private Coordinates coordinates;

        public Coordinates getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "coordinates=" + coordinates +
                    '}';
        }

        private class Coordinates {
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

            @Override
            public String toString() {
                return "Coordinates{" +
                        "latitude=" + latitude +
                        ", longitude=" + longitude +
                        '}';
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
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", location=" + location +
                '}';
    }
}
