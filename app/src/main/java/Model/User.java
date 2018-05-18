package Model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User {
    //private FirebaseUser user;
    private String id;
    private String name;
    private String email;
    private int points;
    private int points_week;
    private int points_month;
    private Uri photoURI;
    private ArrayList<FavoriteZone> favZones;
    private static final int MAX_NUMBER_ZONES = 5;

    public User() {
        //this.user = null;
        this.points=0;
        this.points_month=0;
        this.points_week=0;
        this.favZones = new ArrayList<>();
        this.setName("");
        this.setEmail("");
        this.setId("");
        this.setPhotoURI(null);
    }

    public User(FirebaseUser user) {
        //this.user = user;
        this.points=0;
        this.points_month=0;
        this.points_week=0;
        this.favZones = new ArrayList<>();
        this.setName("");
        this.setEmail("");
        this.setId("");
        this.setPhotoURI(null);
        if(user.getUid()!=null) this.setId(user.getUid());
        if(user.getDisplayName()!=null) this.setName(user.getDisplayName());
        if(user.getEmail()!=null) this.setEmail(user.getEmail());
        if(user.getPhotoUrl()!=null) this.setPhotoURI(user.getPhotoUrl());
    }

    public User(FirebaseUser user, int points, int points_week, int points_month, ArrayList<FavoriteZone> favZ) {
        //this.user = user;
        this.points = points;
        this.points_month = points_month;
        this.points_week = points_week;
        this.favZones = favZ;
        this.setName("");
        this.setEmail("");
        this.setId("");
        this.setPhotoURI(null);
        if(user.getUid()!=null) this.setId(user.getUid());
        if(user.getDisplayName()!=null) this.setName(user.getDisplayName());
        if(user.getEmail()!=null) this.setEmail(user.getEmail());
        if(user.getPhotoUrl()!=null) this.setPhotoURI(user.getPhotoUrl());
    }

    public User(String id, String name, String email, int points, int points_week, int points_month) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.points = points;
        this.points_month = points_month;
        this.points_week = points_week;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints_month() {
        return points_month;
    }

    public void setPoints_month(int points_month) {
        this.points_month = points_month;
    }

    public int getPoints_week() {
        return points_week;
    }

    public void setPoints_week(int points_week) {
        this.points_week = points_week;
    }

    public ArrayList<FavoriteZone> getFavZones() {
       return favZones;
    }

    public void addFavoriteZone(FavoriteZone zone){
        if(this.favZones.size() == MAX_NUMBER_ZONES){
            throw new RuntimeException("Max number of zones");
        }else{
            this.favZones.add(zone);
        }
    }

    public void removeFavoriteZone(FavoriteZone zone){
        this.favZones.remove(zone);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getPhotoUrl() {
        return photoURI;
    }

    public void setPhotoURI(Uri photoURI) {
        this.photoURI = photoURI;
    }
}
