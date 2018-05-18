package edu.ub.pis2018.g5.a24hservice.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import Controller.Controller;
import edu.ub.pis2018.g5.a24hservice.R;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    //Text camp to write the mail and password
    private EditText eMailEdTe, passwordEdTe;
    //Progressbar
    private ProgressBar progressBar;
    //Controller
    private Controller controller;


    //coses del google
    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG="m";
    //coses del fb
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        //add listeners
        findViewById(R.id.textViewSignUp).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.textViewForgotPassword).setOnClickListener(this);
        //assign elements to layout
        eMailEdTe =findViewById(R.id.emailText);
        passwordEdTe =findViewById(R.id.passwordText);
        progressBar=findViewById(R.id.progressBarLogIn);
        progressBar.bringToFront();
        mGoogleBtn=findViewById(R.id.googleBtn);
        Button notNow=findViewById(R.id.notNowButton);

        controller=Controller.getInstance();

        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.logInAnonymous(LogInActivity.this);
            }
        });
        //GOOGLE SIGN IN
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_fb);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

    }//end onCreate

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        makeWindowInactive();
        controller.getMAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        makeWindowActive();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = controller.getMAuth().getCurrentUser();
                            controller.updateUI(LogInActivity.this,user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            controller.updateUI(LogInActivity.this,null);
                        }

                        // ...
                    }
                });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        makeWindowInactive();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        controller.getMAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        makeWindowActive();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = controller.getMAuth().getCurrentUser();
                            controller.updateUI(LogInActivity.this,user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            controller.updateUI(LogInActivity.this,null);
                        }

                        // ...
                    }
                });
    }
    private void login(){
        final String email= eMailEdTe.getText().toString().trim();
        final String password= passwordEdTe.getText().toString().trim();
        controller.login(LogInActivity.this,email,password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        controller.updateUI(LogInActivity.this,controller.getMAuth().getCurrentUser());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textViewSignUp:
                controller.logInToSignUp(LogInActivity.this);break;
            case R.id.loginButton:
                login();break;
            case R.id.textViewForgotPassword:
                controller.logInToPassword(LogInActivity.this);break;

        }
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
        eMailEdTe.setError(getString(R.string.email_required));
        eMailEdTe.requestFocus();
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
}
