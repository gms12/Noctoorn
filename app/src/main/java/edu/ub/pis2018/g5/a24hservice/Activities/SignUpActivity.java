package edu.ub.pis2018.g5.a24hservice.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import Controller.Controller;
import edu.ub.pis2018.g5.a24hservice.R;
public class SignUpActivity extends AppCompatActivity {

    private EditText eMailEdTe, passwordEdTe, usernameEdTe, passwordConfEdTe;
    private ProgressBar progressBar;

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //findViewById(R.id.textViewLogIn).setOnClickListener(this);

        eMailEdTe =findViewById(R.id.emailSignUpText);
        passwordEdTe =findViewById(R.id.passwordSignUpText);
        usernameEdTe =findViewById(R.id.usernameSignUpText);
        passwordConfEdTe =findViewById(R.id.passwordSignUpTextConfirm);

        Button signUpbut=findViewById(R.id.signUpButton);
        Button notNow=findViewById(R.id.notNowButtonSU);
        TextView logIn=findViewById(R.id.textViewLogIn);

        progressBar=findViewById(R.id.progressBarSignUp);
        progressBar.bringToFront();

        controller=Controller.getInstance();

        //Listeners buttons
        signUpbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email= eMailEdTe.getText().toString().trim();
                final String password= passwordEdTe.getText().toString().trim();
                final String username= usernameEdTe.getText().toString().trim();
                final String password_conf= passwordConfEdTe.getText().toString().trim();
                controller.register(SignUpActivity.this,email,password,username,password_conf);
            }
        });

        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleguem al controller la funcio d'entrar de manera anònima
                controller.signUpAnonymous(SignUpActivity.this);
            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleguem al controller
                controller.signUpToLogIn(SignUpActivity.this);
            }
        });
    }

    public void emailEmpty(){
        eMailEdTe.setError(getString(R.string.email_required));
        eMailEdTe.requestFocus();
        return;
    }
    public void usernameEmpty(){
        usernameEdTe.setError(getString(R.string.username_required));
        usernameEdTe.requestFocus();
        return;
    }
    public void passwordEmpty(){
        passwordEdTe.setError(getString(R.string.password_required));
        passwordEdTe.requestFocus();
        return;
    }
    public void emailIncorrect(){
        eMailEdTe.setError(getString(R.string.email_notvalid));
        eMailEdTe.requestFocus();
        return;
    }
    public void passwordIncorrect(){
        passwordEdTe.setError(getString(R.string.password_length));
        passwordEdTe.requestFocus();
        return;
    }
    public void passwordConfEmpty() {
        passwordConfEdTe.setError(getString(R.string.password_conf_required));
        passwordConfEdTe.requestFocus();
        return;
    }

    public void passwordConfFail() {
        passwordConfEdTe.setError(getString(R.string.passwords_match));
        passwordConfEdTe.requestFocus();
        return;
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


}
