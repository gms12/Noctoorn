package edu.ub.pis2018.g5.a24hservice.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;

import Controller.Controller;
import Model.User;
import edu.ub.pis2018.g5.a24hservice.R;

public class NavigationMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Controller controller;
    View headerView;
    TextView usernameNavTeVi, userEmailNavTeVi;
    ImageView imageUserImVi;
    NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private MenuItem currentLocItem, logOut;
    private ArrayList<MenuItem> favZones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);

        controller=Controller.getInstance();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.title_view, null);
        ((TextView)v.findViewById(R.id.title_of_app)).setText(this.getTitle());
        actionBar.setCustomView(v);
        favZones=new ArrayList<>();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(navigationView.getMenu().getItem(controller.getCurrentScreen()).getItemId());
        //displaySelectedScreen(R.id.nav_map);
        navigationView.getMenu().getItem(controller.getCurrentScreen()).setChecked(true);
        logOut=navigationView.getMenu().getItem(5);

        headerView = navigationView.getHeaderView(0);
        usernameNavTeVi = (TextView) headerView.findViewById(R.id.textViewUserName);
        userEmailNavTeVi =headerView.findViewById(R.id.textViewUserEmail);
        imageUserImVi =headerView.findViewById(R.id.imageViewUser);
        controller.updateCurrentPosition(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_layout_map, menu);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.pins_filter_button);
        Bitmap new_icon = resizeBitmapImageFn(icon, 800);
        Drawable d = new BitmapDrawable(getResources(), new_icon);
        menu.findItem(R.id.categories).setIcon(d);
        currentLocItem =menu.findItem(R.id.action_currentPos);
        favZones.add(menu.findItem(R.id.action_FavZone1));
        favZones.add(menu.findItem(R.id.action_FavZone2));
        favZones.add(menu.findItem(R.id.action_FavZone3));
        favZones.add(menu.findItem(R.id.action_FavZone4));
        favZones.add(menu.findItem(R.id.action_FavZone5));

        updateFavZones();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Botó menú desplegable
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        if(id==R.id.categories){

            AlertDialog.Builder mBuilder= new AlertDialog.Builder(NavigationMenuActivity.this);
            mBuilder.setTitle(getResources().getString(R.string.categories));
            mBuilder.setMultiChoiceItems(controller.getCategoriesList(), controller.getCategoriesChecked(), new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int pos, boolean isChecked) {
                    //to do
                }
            });
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    controller.updateCheckedPlaces();

                }
            });

            mBuilder.setNeutralButton(R.string.dialog_selectAll, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int x) {
                    Arrays.fill(controller.getCategoriesChecked(), true);
                    controller.updateCheckedPlaces();
                }
            });

            mBuilder.setNegativeButton(R.string.dialog_discardAll, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int x) {
                    Arrays.fill(controller.getCategoriesChecked(), false);
                    controller.updateCheckedPlaces();
                }
            });
            AlertDialog mDialog=mBuilder.create();
            mDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Log.i("ITEM_DISPLAYED", String.valueOf(id));
        //Log.i("ITEM_DISPLAYED", String.valueOf(currentScreen));
        if(id != navigationView.getMenu().getItem(controller.getCurrentScreen()).getItemId()) {
            for(int i=0; i < navigationView.getMenu().size(); i++){
                if(navigationView.getMenu().getItem(i).getItemId() == id)
                    controller.setCurrentScreen(i);
            }
            if(id==R.id.nav_logOut)controller.setCurrentScreen(0);
            displaySelectedScreen(id);
            return true;
        }else{
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
    }

    private void displaySelectedScreen(int id){
        Fragment fragment = null;
        switch(id){
            case R.id.nav_map:
                if(controller.getCurrentMapFragment() == null){
                    fragment = new MapFragment();
                }else{
                    fragment = controller.getCurrentMapFragment();
                }
                break;
            case R.id.nav_places: fragment = new ServiceListFragment();break;
            case R.id.nav_ranking: fragment = new RankingListFragment(); break;
            case R.id.nav_info: fragment = new AboutUsFragment(); break;
            case R.id.nav_app_settings:fragment=new AppSettingsFragment();break;
            case R.id.nav_logOut:controller.signOut(NavigationMenuActivity.this);finish();break;
        }
        //here we have to update the toolbar depending on the fragment we are.
        //we can do it in another method, this is just a test
        //Dependra del nombre de zones preferides que tingui l'usuari i demes, ja ho gestionare

        updateFavZones();
        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onResume() {
        super.onResume();
        displaySelectedScreen(navigationView.getMenu().getItem(controller.getCurrentScreen()).getItemId());
        //displaySelectedScreen(R.id.nav_map);
        for(int i=0; i < navigationView.getMenu().size(); i++){
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.getMenu().getItem(controller.getCurrentScreen()).setChecked(true);
        updateUserData();
        //updateFavZones();
        controller.updateCurrentPosition(this);

    }
    private void updateFavZones(){
        if(favZones.size()!=0) {
            currentLocItem.setVisible(false);
            for (int i = 0; i < (favZones.size()); i++) favZones.get(i).setVisible(false);
            if (controller.getCurrentUser() != null && controller.getCurrentScreen()!=3) {
                currentLocItem.setVisible(true);
                for (int i = 0; i < controller.getCurrentUser().getFavZones().size(); i++) {
                    favZones.get(i).setTitle(controller.getCurrentUser().getFavZones().get(i).getName());
                    favZones.get(i).setVisible(true);
                }
            }
        }
    }
    private void updateUserData() {
        User user=controller.getCurrentUser();
        if(user==null){
            userEmailNavTeVi.setVisibility(View.GONE);
            usernameNavTeVi.setVisibility(View.GONE);
            imageUserImVi.setVisibility(View.GONE);
            logOut.setTitle(R.string.action_login);
            logOut.setIcon(R.drawable.ic_login);

        }
        else{

            if(user.getEmail()!=null){
                userEmailNavTeVi.setText(user.getEmail());
            }
            else userEmailNavTeVi.setText("");
            if(user.getName()!=null){
                usernameNavTeVi.setText(user.getName());
            }
            else usernameNavTeVi.setText("");
            if(controller.getDatabase().getPhotoUri()!=null){
                Glide.with(this).load(controller.getDatabase().getPhotoUri().toString())
                        .apply(new RequestOptions().centerCrop()).into(imageUserImVi);
            }
            else if(user.getPhotoUrl()!=null) {
                Glide.with(this).load(user.getPhotoUrl().toString())
                        .apply(new RequestOptions().centerCrop()).into(imageUserImVi);
            }//lo del apply era per intentar que encaixes la foto pero res

        }
    }

    private Bitmap resizeBitmapImageFn(Bitmap bmpSource, int maxResolution){
        int iWidth = bmpSource.getWidth();
        int iHeight = bmpSource.getHeight();
        int newWidth = iWidth ;
        int newHeight = iHeight ;
        float rate = 0.0f;

        if(iWidth > iHeight ){
            if(maxResolution < iWidth ){
                rate = maxResolution / (float) iWidth ;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < iHeight ){
                rate = maxResolution / (float) iHeight ;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(bmpSource, newWidth, newHeight, true);
    }
}
