package Model;

import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Database {
    private static Database instance;
    private FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseDatabase servicesData;
    private DatabaseReference databaseUsers;
    private DatabaseReference databaseServices;
    private DatabaseReference databaseRegistration;
    private ArrayList<Service> services;
    private ArrayList<User> users;
    private Uri photoUri;



    private Database(){
        servicesData = FirebaseDatabase.getInstance();
        databaseUsers = servicesData.getReference("User_test");
        databaseServices = servicesData.getReference("Service_test");
        databaseRegistration = servicesData.getReference("Service_Register_test");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        services = new ArrayList<>();
        this.fillServices(services);
        users = new ArrayList<>();
        this.fillUsers(users);
        setPhotoUri(null);
    }

    public static Database getInstance(){
        if(instance == null){
            synchronized(Database.class){
                if(instance == null){
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    //Getters and setters
    public ArrayList<Service> getServices() {
        return services;
    }

    public Service getServiceById(String id){
        for(Service s : services){
            if(s.getId().equals(id)){
                return s;
            }
        }
        return null;
    }

    public void removeServiceById(String id){
        for(Service s : services){
            if(s.getId().equals(id)){
                services.remove(s);
                return ;
            }
        }
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public User downloadUserData(final FirebaseUser user){
        final User currentUser = new User(user);
        final String[]fav={"fz1","fz2","fz3","fz4","fz5"};
        Query query=databaseUsers.orderByChild("id").equalTo(currentUser.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    uploadUserData(currentUser);
                    return;
                }
                else{
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                        if(singleSnapshot.exists()){
                            currentUser.setPoints(singleSnapshot.child("points").getValue(int.class));
                            for(int i=0;i<singleSnapshot.child("num_fav_zones").getValue(int.class);i++){
                                currentUser.addFavoriteZone(singleSnapshot.child(fav[i]).getValue(FavoriteZone.class));
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        getDownloadUserImage(currentUser);
        return currentUser;
    }

    public void uploadUserData(User user){
        String id=user.getId();
        int numFav=user.getFavZones().size();
        String[]fav={"fz1","fz2","fz3","fz4","fz5"};
        databaseUsers.child(id).child("id").setValue(user.getId());
        databaseUsers.child(id).child("name").setValue(user.getName());
        databaseUsers.child(id).child("email").setValue(user.getEmail());
        databaseUsers.child(id).child("points").setValue(user.getPoints());
        databaseUsers.child(id).child("points_week").setValue(user.getPoints_week());
        databaseUsers.child(id).child("points_month").setValue(user.getPoints_month());
        //databaseUsers.child(id).child("photoURI").setValue(user.getPhotoUrl());
        //databaseUsers.child(id).child("favZones").setValue(currentUser.getFavZones());
        databaseUsers.child(id).child("num_fav_zones").setValue(numFav);
        for(int i=0;i<numFav;i++){
            databaseUsers.child(id).child(fav[i]).setValue(user.getFavZones().get(i));
        }

        for(User us:users){
            if(us.getId().equals(user.getId())){
                users.remove(us);
                break;
            }
        }

        users.add(user);
    }
    public void uploadUserImage(User user){
        if(user.getPhotoUrl()!=null){
            StorageReference ref=storageReference.child("images/"+user.getId());
            ref.putFile(user.getPhotoUrl())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
    public void getDownloadUserImage(User user){
        storageReference.child("images/"+user.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setPhotoUri(uri);
            }
        });
    }
    public void addService(String id_user, String name, Coordinates position, int type){
        String id_service = databaseServices.push().getKey();
        Service newService = new Service(id_service, name, position, type);
        databaseServices.child(id_service).setValue(newService);
        this.services.add(newService);
        String id_reg = databaseRegistration.push().getKey();
        ServiceRegistration sr = new ServiceRegistration(id_service, id_user, new SimpleDate(), ServiceRegistration.SERVICE_CREATED);
        databaseRegistration.child(id_service).child(id_reg).setValue(sr);
    }

    public void fillServices(final ArrayList<Service> services) {
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("SERVICEADDED", dataSnapshot.toString());
                if(services.isEmpty()){
                    for (DataSnapshot serviceSnap : dataSnapshot.getChildren()) {
                        String id = serviceSnap.child("id").getValue(String.class);
                        String name = serviceSnap.child("name").getValue(String.class);
                        double lat = serviceSnap.child("position").child("latitude").getValue(double.class);
                        double lng = serviceSnap.child("position").child("longitude").getValue(double.class);
                        int t = serviceSnap.child("type").getValue(int.class);
                        Service ser = new Service(id, name, lat, lng, t);
                        services.add(ser);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void fillUsers(final ArrayList<User> users) {
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(users.isEmpty()) {
                    Log.i("FILL_USERS", "fillung------------------");
                    for (DataSnapshot userSnap : dataSnapshot.getChildren()) {
                        String id = userSnap.child("id").getValue(String.class);
                        String name = userSnap.child("name").getValue(String.class);
                        String email = userSnap.child("email").getValue(String.class);
                        //Uri photoURI = userSnap.child("photoURI").getValue(Uri.class);
                        int points = userSnap.child("points").getValue(int.class);
                        int points_month = userSnap.child("points_month").getValue(int.class);
                        int points_week = userSnap.child("points_week").getValue(int.class);
                        User newUser = new User(id, name, email, points, points_week, points_month);
                        users.add(newUser);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public double getServiceDistanceNum(Service ser, Location position){
        return getServiceDistanceNum(ser.getPosition(), position);
    }

    public String getServiceDistanceString(Service ser, Location position){
        return getServiceDistanceString(ser.getPosition(), position);
    }

    public double getServiceDistanceNum(Coordinates pos, Location position){
        Location posLoc = new Location("svc");
        posLoc.setLatitude(pos.getLatitude());
        posLoc.setLongitude(pos.getLongitude());
        return position.distanceTo(posLoc);
    }

    public String getServiceDistanceString(Coordinates pos, Location position){
        String units, distanceStr;
        double distance = getServiceDistanceNum(pos, position);
        if(distance >= 1000){
            distance /= 1000;
            distance = Math.round(distance * 100.0) / 100.0;
            distanceStr = Double.toString(distance);
            units = " km";
        }else{
            distance = Math.round(distance / 10.0) * 10.0;
            distanceStr = Integer.toString((int) distance);
            units = " m";
        }
        return distanceStr + units;
    }

    public void sortServicesDistance(Coordinates position){
        final Location pos = new Location("svc");
        pos.setLatitude(position.getLatitude());
        pos.setLongitude(position.getLongitude());
        Collections.sort(services, new Comparator<Service>() {
            @Override
            public int compare(Service service1, Service service2) {
                Double d1 = getServiceDistanceNum(service1, pos);
                Double d2 = getServiceDistanceNum(service2, pos);
                return d1.compareTo(d2);
            }
        });
    }

    public void updateUsers(){
        fillUsers(users);
    }

    //SERVICEINFO
    public void updateService(User user, Service service){
        databaseServices.child(service.getId()).child("lastModified").setValue(new SimpleDate());
        getServiceById(service.getId()).setLastModified(new SimpleDate());
        String id_reg = databaseRegistration.push().getKey();
        ServiceRegistration sr = new ServiceRegistration(service.getId(), user.getId(), new SimpleDate(), ServiceRegistration.SERVICE_UPDATED);
        databaseRegistration.child(service.getId()).child(id_reg).setValue(sr);
    }

    public void removeService(User user, Service service){
        databaseServices.child(service.getId()).removeValue();
        removeServiceById(service.getId());
        String id_reg = databaseRegistration.push().getKey();
        ServiceRegistration sr = new ServiceRegistration(service.getId(), user.getId(), new SimpleDate(), ServiceRegistration.SERVICE_REMOVED);
        databaseRegistration.child(service.getId()).child(id_reg).setValue(sr);
    }

    public void changeNameService(User user, Service service, String name){
        databaseServices.child(service.getId()).child("lastModified").setValue(new SimpleDate());
        databaseServices.child(service.getId()).child("name").setValue(name);
        getServiceById(service.getId()).setLastModified(new SimpleDate());
        getServiceById(service.getId()).setName(name);
        String id_reg = databaseRegistration.push().getKey();
        ServiceRegistration sr = new ServiceRegistration(service.getId(), user.getId(), new SimpleDate(), ServiceRegistration.SERVICE_MODIFIED);
        databaseRegistration.child(service.getId()).child(id_reg).setValue(sr);
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}

