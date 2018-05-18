package edu.ub.pis2018.g5.a24hservice.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import Controller.Controller;
import Model.Service;
import edu.ub.pis2018.g5.a24hservice.R;

public class ServiceInformationActivity extends AppCompatActivity {
    private Controller controller;
    private Service currentService;
    private TextView typeServiceTeVi, distanceTeVi, adressTeVi;
    private ImageView pinTypeImVi;
    private Button viewOnMapBu, goBu;
    private Toolbar toolbar;
    private AlertDialog.Builder builderRename, builderRUSure;
    private View viewRename, viewRUSure;
    private EditText newNameEdTe;
    private Context contextRename, contextRUSure;
    private LayoutInflater layoutInflaterRename, layoutInflaterRUSure;
    private net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout collapsingToolbar;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = Controller.getInstance();

        currentService = controller.getCurrentService();
        setContentView(R.layout.activity_service_information);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(currentService.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int color = getColor(controller.getColorOfService(currentService.getType()));
        toolbar.setBackgroundColor(color);
        collapsingToolbar = (net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        collapsingToolbar.setBackgroundColor(color);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.TextAppearance_MyApp_Title_Collapsed);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.TextAppearance_MyApp_Title_Expanded);

        typeServiceTeVi = (TextView)findViewById(R.id.type_service_info_TeVi);
        distanceTeVi = (TextView)findViewById(R.id.distance);
        adressTeVi = (TextView)findViewById(R.id.adress_info);
        pinTypeImVi = (ImageView)findViewById(R.id.pin_round);

        typeServiceTeVi.setText(controller.getCategoriesList()[currentService.getType()]);
        pinTypeImVi.setImageResource(controller.getPinOfServiceRound(currentService.getType()));
        distanceTeVi.setText(controller.getServiceDistanceString(currentService, controller.getCurrentLocation()));

        //Delete
        builderRUSure = new AlertDialog.Builder(this);
        builderRUSure.setMessage(R.string.dialog_remove_service_message).setTitle(R.string.dialog_remove_service_title);

        builderRUSure.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeService();
                dialogInterface.dismiss();
            }
        });

        builderRUSure.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //Rename
        builderRename = new AlertDialog.Builder(this);
        builderRename.setMessage(R.string.dialog_rename_service_message).setTitle(R.string.dialog_rename_service_title);
        contextRename = builderRename.getContext();
        layoutInflaterRename = LayoutInflater.from(contextRename);
        viewRename = layoutInflaterRename.inflate(R.layout.dialog_rename, null);
        builderRename.setView(viewRename);
        newNameEdTe = (EditText) viewRename.findViewById(R.id.new_service_name);

        builderRename.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = newNameEdTe.getText().toString().trim();
                changeNameService(name);
                dialogInterface.dismiss();
            }
        });

        builderRename.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        viewOnMapBu = (Button) findViewById(R.id.viewOnMapButton);
        goBu = (Button)findViewById(R.id.button_go);
        viewOnMapBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.setCurrentScreen(0);
                controller.setCentredPosition(currentService.getPosition());
                finish();
            }
        });

        goBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&origin=&destination="
                                + String.valueOf(currentService.getPosition().getLatitude()) + ","
                                + String.valueOf(currentService.getPosition().getLongitude())));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(controller.getCurrentUser() != null && controller.getServiceDistanceNum(currentService, controller.getCurrentLocation()) < 100){
            getMenuInflater().inflate(R.menu.serviceinfo_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check_service:
                updateService();
                Toast.makeText(this, "Service updated!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.update_service:
                builderRename.create().show();
                return true;
            case R.id.remove_service:
                builderRUSure.create().show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateService(){
        controller.updateService(currentService);
    }

    public void changeNameService(String newName){
        toolbar.setTitle(newName);
        collapsingToolbar.setTitle(newName);
        controller.changeNameService(currentService, newName);
        Toast.makeText(this, "Name changed!", Toast.LENGTH_SHORT).show();
    }

    public void removeService(){
        controller.removeService(currentService);
        Toast.makeText(this, "Service removed!", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
