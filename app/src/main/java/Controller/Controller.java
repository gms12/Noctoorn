package Controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Arrays;

import Model.Coordinates;
import Model.Database;
import Model.Service;
import Model.User;
import edu.ub.pis2018.g5.a24hservice.Activities.AddServiceActivity;
import edu.ub.pis2018.g5.a24hservice.Activities.LogInActivity;
import edu.ub.pis2018.g5.a24hservice.Activities.MapFragment;
import edu.ub.pis2018.g5.a24hservice.Activities.NavigationMenuActivity;
import edu.ub.pis2018.g5.a24hservice.Activities.PasswordActivity;
import edu.ub.pis2018.g5.a24hservice.Activities.SignUpActivity;
import edu.ub.pis2018.g5.a24hservice.R;

public class Controller {

    private static Controller instance;
    private FirebaseAuth mAuth;
    private User currentUser;
    private Database database;
    private Location currentLocation;
    private String[] categoriesList;
    private String[] categoriesListShort;
    private boolean[] categoriesChecked;
    private LocationListener locationListener;
    //ACTIVITIES
    private LogInActivity logInActivity;
    private SignUpActivity signUpActivity;
    private PasswordActivity passwordActivity;

    //SCREEN
    private int currentScreen;

    //FRAGMENTS
    private MapFragment currentMapFragment;

    //ATTRIBUTES
    private Service currentService;
    private Coordinates centredPosition;
    private boolean isCentredAtPosition;

    
    private Controller(){
        mAuth = FirebaseAuth.getInstance();
        setDatabase(Database.getInstance());
        updateUserData(mAuth.getCurrentUser());

        currentScreen = 0;
        categoriesList = null;
        currentService = null;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCurrentPosition(logInActivity.getApplicationContext());
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };
    }

    public static Controller getInstance(){
        if(instance == null){
            synchronized(Controller.class){
                if(instance == null){
                    instance = new Controller();
                }
            }
        }
        return instance;
    }
    ///////////GETTERS AND SETTERS//////////
    public void setCategoriesListShort(String[] cl){
        categoriesListShort=cl;
    }
    public String[] getCategoriesListShort(){
        return categoriesListShort;
    }
    public void setCategoriesList(String[] cl){
        categoriesList=cl;
        categoriesChecked = new boolean[getCategoriesList().length];
        Arrays.fill(categoriesChecked, true);
    }
    public String[] getCategoriesList(){
        return categoriesList;
    }
    public boolean[] getCategoriesChecked(){
        return categoriesChecked;
    }
    public FirebaseAuth getMAuth(){
        return mAuth;
    }
    public User getCurrentUser(){
        return currentUser;
    }
    public ArrayList<Service> getServices() {
        return this.getDatabase().getServices();
    }
    public Service getCurrentService() {
        return currentService;
    }
    public void setCurrentService(Service currentService) {
        this.currentService = currentService;
    }
    public void setCurrentService(String currentServiceId) {
        for(Service s : getDatabase().getServices()){
            if(s.getId().equals(currentServiceId)){
                this.currentService = s;
                return;
            }
        }
    }
    public Location getCurrentLocation(){
        return this.currentLocation;
    }
    public Coordinates getCurrentPosition(){
        return new Coordinates(currentLocation.getLatitude(), currentLocation.getLongitude());
    }
    public MapFragment getCurrentMapFragment() {
        return currentMapFragment;
    }
    public void setCurrentMapFragment(MapFragment currentMapFragment) {
        this.currentMapFragment = currentMapFragment;
    }
    public Coordinates getCentredPosition() {
        return centredPosition;
    }
    public void setCentredPosition(Coordinates centredPosition) {
        this.centredPosition = centredPosition;
        isCentredAtPosition = true;
    }
    public boolean isCentredAtPosition() {
        return isCentredAtPosition;
    }
    public void setCentredAtPosition(boolean centredAtPosition) {
        isCentredAtPosition = centredAtPosition;
    }

    ///////////LAUNCHERACTIVITY METHODS//////////
    public void launcherToLogIn(Context context){
        context.startActivity(new Intent(context, LogInActivity.class));
    }
    ///////////SIGNUPACTIVITY METHODS////////
    public void register(final Context context, final String email, final String password,
                         final String username,final String password_conf){
        //we assign the context with our activity
        signUpActivity=(SignUpActivity)context;
        //We first check if the data is correct
        if(username.isEmpty()){
            signUpActivity.usernameEmpty();
            return;
        }
        if(email.isEmpty()){
            signUpActivity.emailEmpty();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpActivity.emailIncorrect();
            return;
        }
        if(password.isEmpty()){
            signUpActivity.passwordEmpty();
            return;
        }
        if(password_conf.isEmpty()){
            signUpActivity.passwordConfEmpty();
            return;
        }
        if(password.length()<6){
            signUpActivity.passwordIncorrect();
            return;
        }
        if(!password.equals(password_conf)){
            signUpActivity.passwordConfFail();
            return;
        }
        signUpActivity.makeWindowInactive();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mAuth.signInWithEmailAndPassword(email,password);
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username).build();
                    user.updateProfile(profile)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                signUpActivity.makeWindowActive();
                                                if (task.isSuccessful()){

                                                    Toast.makeText(context, R.string.verification_email, Toast.LENGTH_LONG).show();
                                                    signUpToLogIn(context);
                                                }
                                                else{
                                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        signUpActivity.makeWindowActive();
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    signUpActivity.makeWindowActive();
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(context, R.string.error_emailused, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void signUpAnonymous(final Context context) {
        signUpActivity=(SignUpActivity)context;
        signUpActivity.makeWindowInactive();

        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signUpActivity.makeWindowActive();
                if(task.isSuccessful()) {
                    updateUserData(mAuth.getCurrentUser());
                    signUpActivity.finish();
                    Intent intent=new Intent(context,NavigationMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    signUpActivity.startActivity(intent);
                }
            }
        });
    }

    public void signUpToLogIn(final Context context) {
        signUpActivity=(SignUpActivity)context;
        signUpActivity.finish();
        Intent intent=new Intent(context,LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    //////////PASSWORDACTIVITY METHODS////////
    public void resetPassword(final Context context, String email) {
        passwordActivity=(PasswordActivity)context;
        //Comprovem que sigui correcte
        if(email.isEmpty()){
            passwordActivity.emailEmpty();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            passwordActivity.emailIncorrect();
            return;
        }
        passwordActivity.makeWindowInactive();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                passwordActivity.makeWindowActive();

                if(task.isSuccessful()){
                    Toast.makeText(context, R.string.PasswordResetEmail, Toast.LENGTH_SHORT).show();
                    passwordActivity.finish();
                    passwordActivity.startActivity(new Intent(context,LogInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                else{
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /////////LOGINACTIVITY METHODS////////
    public void logInAnonymous(final Context context) {
        logInActivity=(LogInActivity)context;
        logInActivity.makeWindowInactive();

        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                logInActivity.makeWindowActive();
                if(task.isSuccessful()) {
                    updateUI(context,mAuth.getCurrentUser());
                }
            }
        });
    }

    public void login(final Context context, String email, String password) {
        logInActivity=(LogInActivity)context;
        //Comprovem que siguin correctes
        if(email.isEmpty()){
            logInActivity.emailEmpty();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            logInActivity.emailIncorrect();
            return;
        }
        if(password.isEmpty()){
            logInActivity.passwordEmpty();
            return;
        }
        logInActivity.makeWindowInactive();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                logInActivity.makeWindowActive();
                if(task.isSuccessful()){
                    updateUI(context,mAuth.getCurrentUser());
                }
                else{
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void updateUI(Context context,FirebaseUser user){
        logInActivity=(LogInActivity)context;
        if(user!=null) {
            if (user.isAnonymous() || user.isEmailVerified() || user.getProviderId().equals("facebook.com") || user.getProviderId().equals("google.com")) {
                updateUserData(user);

                logInActivity.finish();//tanca aquesta activity
                Intent intent = new Intent(context, NavigationMenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logInActivity.startActivity(intent);
            }
        }
    }
    public void logInToSignUp(Context context){
        context.startActivity(new Intent(context, SignUpActivity.class));
    }
    public void logInToPassword(Context context){
        context.startActivity(new Intent(context,PasswordActivity.class));
    }


    public void updateUserData(FirebaseUser user){
        if(user==null || user.isAnonymous()){
            currentUser=null;
        }
        else {

            //aqui ara comprovem si l'usuari esta a la base de dades, i si es aixi actualitzem les seves dades
            //Firebase busca llocs a la base de dades
            currentUser= getDatabase().downloadUserData(user);
        }
    }

    ///////NAVIGATION_ACT METHODS//////////
    public void signOut(Context context){
        mAuth.signOut();
        currentUser=null;
        context.startActivity(new Intent(context, LogInActivity.class));
        ((NavigationMenuActivity)context).finish();
    }

    public int getCurrentScreen(){
        return this.currentScreen;
    }

    public void setCurrentScreen(int id){
        this.currentScreen = id;
    }
    public void updateCheckedPlaces(){
        for(Service service:getServices()){
            service.setVisible(getCategoriesChecked()[service.getType()]);
        }
    }
    ///////ADD_SERVICE_ACT METHODS//////////
    public void addService(Context context, EditText name_EdTe, Spinner type_service_Sp){
        String service_name = name_EdTe.getText().toString().trim();
        int type = type_service_Sp.getSelectedItemPosition();
        double latitude, longitude;
        if(currentLocation == null){
            Toast.makeText(context, "Please, turn on location services", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }else {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
            if (!TextUtils.isEmpty(service_name)) {
                String id_user = this.getCurrentUser().getId();
                getDatabase().addService(id_user, service_name, new Coordinates(latitude, longitude), type);

                Toast.makeText(context, "Servei afegit!", Toast.LENGTH_LONG).show();
                ((AddServiceActivity) context).finish();
            } else {
                Toast.makeText(context, "Falten dades per afegir el servei", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void updateCurrentPosition(Context context){
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")){
            Toast.makeText(context, "Please, turn on location services", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((AddServiceActivity) context), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            Criteria cr = new Criteria();
            lm.requestLocationUpdates(lm.getBestProvider(cr, false), 0, 0, locationListener);
            currentLocation = lm.getLastKnownLocation(lm.getBestProvider(cr, false));
            //lm.removeUpdates(locationListener);
        }
    }

    public void addServiceActivityOnResume(Context context, TextView lat, TextView lng){
        this.updateCurrentPosition(context);
        if(currentLocation == null){
            lat.setText(String.valueOf(40.76));
            lng.setText(String.valueOf(-74.0));
        }else {
            lat.setText(String.valueOf(currentLocation.getLongitude()));
            lng.setText(String.valueOf(currentLocation.getLatitude()));
        }
    }

    ///////MAP_FRAGMENT METHODS//////////

    public int getPinOfService(int type){
        switch (type){
            case 0:    return R.drawable.pin_other;
            case 1:     return R.drawable.pin_pharmacy;
            case 2:     return R.drawable.pin_water_source;
            case 3:     return R.drawable.pin_supermarket;
            case 4:     return R.drawable.pin_police;
            case 5:     return R.drawable.pin_medical_emergencies;
            case 6:     return R.drawable.pin_gas_station;
            case 7:     return R.drawable.pin_gym;
            case 8:     return R.drawable.pin_parking;
            case 9:     return R.drawable.pin_atm_machine;
            case 10:     return R.drawable.pin_laundry;
        }
        return 0;
    }

    public int getPinOfServiceRound(int type){
        switch (type){
            case 0:    return R.drawable.pin_other_round;
            case 1:     return R.drawable.pin_pharmacy_round;
            case 2:     return R.drawable.pin_water_source_round;
            case 3:     return R.drawable.pin_supermarket_round;
            case 4:     return R.drawable.pin_police_round;
            case 5:     return R.drawable.pin_medical_emergencies_round;
            case 6:     return R.drawable.pin_gas_station_round;
            case 7:     return R.drawable.pin_gym_round;
            case 8:     return R.drawable.pin_parking_round;
            case 9:     return R.drawable.pin_atm_machine_round;
            case 10:     return R.drawable.pin_laundry_round;
        }
        return 0;
    }

    public int getColorOfService(int type){
        switch (type){
            case 0:    return R.color.service_color_other;
            case 1:     return R.color.service_color_pharmacy;
            case 2:     return R.color.service_color_water_source;
            case 3:     return R.color.service_color_supermarket;
            case 4:     return R.color.service_color_police;
            case 5:     return R.color.service_color_medical_emergencies;
            case 6:     return R.color.service_color_gas_station;
            case 7:     return R.color.service_color_gym;
            case 8:     return R.color.service_color_parking;
            case 9:     return R.color.service_color_atm_machine;
            case 10:     return R.color.service_color_laundry;
        }
        return 0;
    }

    public void showServicesOnMap(Context context, GoogleMap mGoogleMap) {
        if(mGoogleMap != null) {
            mGoogleMap.clear();
            for (Service ser : this.getDatabase().getServices()) {
                if (ser.isVisible()) {
                    LatLng pos = new LatLng(ser.getPosition().getLatitude(), ser.getPosition().getLongitude());
                    MarkerOptions marker = new MarkerOptions().position(pos).title(ser.getName())
                            .snippet(categoriesList[ser.getType()] + "::" + ser.getId());
                    Bitmap servicePin = null;
                    int drawableId = getPinOfService(ser.getType());
                    if (drawableId != 0) {
                        servicePin = getBitmap(context, drawableId);
                    }
                    if (servicePin != null) {
                        marker.icon(BitmapDescriptorFactory.fromBitmap(servicePin));
                    }
                    mGoogleMap.addMarker(marker);
                }
            }
        }

    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap((int)(vectorDrawable.getIntrinsicWidth()*1.3),
                (int)(vectorDrawable.getIntrinsicHeight()*1.3), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public void setMapOnUser(MapFragment mapFragment, GoogleMap mGoogleMap) {
        LatLng p;
        if(currentLocation == null) p = new LatLng(40.76, -74.0);//NY
        else p = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraPosition C = CameraPosition.builder().target(p).zoom(11).bearing(0).tilt(0).build();
        if(mGoogleMap != null) mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(C));
    }

    public void setMapOnPosition(MapFragment mapFragment, GoogleMap mGoogleMap, Coordinates position) {
        LatLng p = new LatLng(position.getLatitude(), position.getLongitude());
        CameraPosition C = CameraPosition.builder().target(p).zoom(14).bearing(0).tilt(0).build();
        if(mGoogleMap != null) mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(C));
    }

    public void disableAddServiceButton(MapFragment mapFragment, FloatingActionButton addService, int gone) {
        if(this.getCurrentUser()==null){
            addService.setVisibility(gone);
            addService.setActivated(false);
        }
    }

    public void mapReady(final Context context, GoogleMap mGoogleMap, final FloatingActionButton serviceInformationFlAcBu) {
        this.showServicesOnMap(context, mGoogleMap);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.mapstyle_day));
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = ((NavigationMenuActivity)context).getLayoutInflater().inflate(R.layout.window_map_layout, null);
                String name = marker.getTitle();
                String info = marker.getSnippet();
                LatLng position = marker.getPosition();
                TextView nameTeVi = (TextView) v.findViewById(R.id.service_name);
                TextView typeTeVi = (TextView) v.findViewById(R.id.service_type);
                TextView distanceTeVi = (TextView) v.findViewById(R.id.service_distance);
                int pos = info.indexOf("::");
                String type = info.substring(0, pos);
                nameTeVi.setText(name);
                typeTeVi.setText(type);
                distanceTeVi.setText(getServiceDistanceString(new Coordinates(position.latitude, position.longitude), currentLocation));
                return v;
            }
        });
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                serviceInformationFlAcBu.setVisibility(View.VISIBLE);
                serviceInformationFlAcBu.setActivated(true);
                String info = marker.getSnippet();
                int pos = info.indexOf("::");
                String id = info.substring(pos+2);
                setCurrentService(id);
                return false;
            }
        });

        mGoogleMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                serviceInformationFlAcBu.setVisibility(View.GONE);
                serviceInformationFlAcBu.setActivated(false);
            }
        });
    }
    /////////////////USERSETTINGS METHODS/////////////////////
    public void changePhotoUri(Intent data) {
        //we assign the image uri to the current user
        this.getCurrentUser().setPhotoURI(data.getData());
        this.getDatabase().setPhotoUri(data.getData());
        this.getDatabase().uploadUserImage(currentUser);
    }
    public void deleteAccount(final Context context, final String email, final String password){
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mAuth.getCurrentUser().reauthenticate(credential).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, R.string.user_deleted, Toast.LENGTH_SHORT).show();
                                currentUser=null;
                                context.startActivity(new Intent(context, LogInActivity.class));
                            }
                            else
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void changePassword(final Context context, String email, String password, final String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mAuth.getCurrentUser().reauthenticate(credential).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mAuth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, R.string.password_changed, Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void UploadUserData(){
        getDatabase().uploadUserData(currentUser);
    }
    //SERVICE_INFORMATION
    public double getServiceDistanceNum(Service ser, Location position){
        return this.getDatabase().getServiceDistanceNum(ser, position);
    }

    public String getServiceDistanceString(Service ser, Location position){
        return this.getDatabase().getServiceDistanceString(ser, position);
    }

    public double getServiceDistanceNum(Coordinates pos, Location position){
        return this.getDatabase().getServiceDistanceNum(pos, position);
    }

    public String getServiceDistanceString(Coordinates pos, Location position){
        return this.getDatabase().getServiceDistanceString(pos, position);
    }

    public void sortServicesDistance(Coordinates position){
        this.getDatabase().sortServicesDistance(position);
    }

    public void updateService(Service service){
        getDatabase().updateService(currentUser, service);
    }

    public void removeService(Service service){
        getDatabase().removeService(currentUser, service);
    }

    public void changeNameService(Service service, String name){
        getDatabase().changeNameService(currentUser, service, name);
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
}
