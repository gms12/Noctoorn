package edu.ub.pis2018.g5.a24hservice.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import Controller.Controller;
import edu.ub.pis2018.g5.a24hservice.R;

public class LauncherActivity extends AppCompatActivity {
    private Controller controller;
    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        controller=Controller.getInstance();
        controller.setCategoriesList(getResources().getStringArray(R.array.list_categories));
        controller.setCategoriesListShort(getResources().getStringArray(R.array.list_categories_short));
        ////TO-DO
        mWaitHandler.postDelayed(new Runnable() {
            public void run() {
                controller.launcherToLogIn(LauncherActivity.this);//cridem al controller per a obrir el login
                finish();//posem el finish despres. D'aquesta manera no es veu com es tanca la pantalla
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWaitHandler.removeCallbacksAndMessages(null);
    }
}
