package edu.ub.pis2018.g5.a24hservice.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import Controller.Controller;
import edu.ub.pis2018.g5.a24hservice.R;

public class AddServiceActivity extends AppCompatActivity {
    //Prova de commit
    private Controller controller;

    private Spinner serviceTypeSp;
    private EditText serviceNameEdTe;
    private Button addBt;
    private TextView latitudeTeVi, longitudeTeVi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        controller = Controller.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serviceTypeSp = (Spinner)findViewById(R.id.spinnerTipusServei);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.list_categories, android.R.layout.simple_spinner_item);
        serviceTypeSp.setAdapter(adapter);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        addBt = (Button) findViewById(R.id.afegirServeiButton);
        serviceNameEdTe = (EditText) findViewById(R.id.nomServeiText);
        latitudeTeVi = (TextView) findViewById(R.id.latitudeLabel);
        longitudeTeVi = (TextView) findViewById(R.id.longitudeLabel);

        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                controller.addService(AddServiceActivity.this, serviceNameEdTe, serviceTypeSp);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.addServiceActivityOnResume(AddServiceActivity.this, latitudeTeVi, longitudeTeVi);
    }



}
