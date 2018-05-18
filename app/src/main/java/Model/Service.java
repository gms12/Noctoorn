package Model;

import org.joda.time.DateTime;

/**
 * Created by alex on 25/04/2018.
 */

public class Service {
    private String name;
    private String id;
    private Coordinates position;
    private int type;
    private SimpleDate lastModified;
    private boolean visible;
    public static final int OTHER = 0;
    public static final int PHARMACY = 1;
    public static final int WATER_SOURCE = 2;
    public static final int SUPERMARKET = 3;
    public static final int POLICE_STATION = 4;
    public static final int MEDICAL_EMERGENCY = 5;
    public static final int GAS_STATION = 6;
    public static final int GYM = 7;
    public static final int PARKING = 8;
    public static final int ATM_MACHINE = 9;
    public static final int LAUNDRY = 10;


    public Service(){
        this.name = "Service not initialized";
        this.position = new Coordinates(0, 0);
        this.type = -1;
        this.lastModified = new SimpleDate();
        this.setVisible(true);
    }

    public Service(String id, String name, Coordinates pos, int tipus) {
        this.id = id;
        this.name = name;
        this.position = pos;
        this.type = tipus;
        this.lastModified = new SimpleDate(new DateTime());
        this.setVisible(true);
    }

    public Service(String id, String name, double x, double y, int tipus) {
        this.id = id;
        this.name = name;
        this.position = new Coordinates(x, y);
        this.type = tipus;
        this.lastModified = new SimpleDate(new DateTime());
        this.setVisible(true);
    }

    public String getName() {
        return this.name;
    }

    public Coordinates getPosition() {
        return this.position;
    }

    public int getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public void setPositionCoordinates(double x, double y){
        this.position = new Coordinates(x, y);
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SimpleDate getLastModified() {
        return lastModified;
    }

    public void setLastModified(SimpleDate lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
