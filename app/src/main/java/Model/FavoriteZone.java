package Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mpedembe7.alumnes on 03/05/18.
 */

public class FavoriteZone {

    private String name;
    private Coordinates position;

    public FavoriteZone() {
        this.name = "";
        this.position = new Coordinates(0,0);
    }

    public FavoriteZone(String name, Coordinates position) {
        this.name = name;
        this.position = position;
    }

    public FavoriteZone(String name, double lat, double lng) {
        this.name = name;
        this.position = new Coordinates(lat, lng);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }
}
