package edu.ub.pis2018.g5.a24hservice.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import Controller.Controller;
import edu.ub.pis2018.g5.a24hservice.R;

public class PasswordActivity extends AppCompatActivity {
    Button resetBu;
    EditText emailPasswordEdTe;
    ProgressBar progressBar;
    Controller controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        controller=Controller.getInstance();
        resetBu =findViewById(R.id.buttonReset);
        emailPasswordEdTe =findViewById(R.id.editPasswordEmail);

        progressBar=findViewById(R.id.progressBarPassword);
        resetBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email= emailPasswordEdTe.getText().toString().trim();//trim es per eliminar espais de davant i final
                controller.resetPassword(PasswordActivity.this,email);
            }
        });
        //PEL BOTO DE TIRAR ENRERE
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    //This method deactivates the progressbar and lets the user click on the activity again
    public void makeWindowActive(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
    }
    //This method activates the progressbar and doesn't let the user click
    public void makeWindowInactive(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    public void emailEmpty(){
        emailPasswordEdTe.setError(getString(R.string.email_required));
        emailPasswordEdTe.requestFocus();
        return;
    }
    public void emailIncorrect(){
        emailPasswordEdTe.setError(getString(R.string.email_notvalid));
        emailPasswordEdTe.requestFocus();
        return;
    }
}
