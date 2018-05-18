package edu.ub.pis2018.g5.a24hservice.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import edu.ub.pis2018.g5.a24hservice.R;

public class ShowProfileActivity extends AppCompatActivity {
    TextView totalPoints;
    TextView monthlyPoints;
    TextView weeklyPoints;
    TextView dailyPoints;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        monthlyPoints = findViewById(R.id.monthlypoints);
        weeklyPoints = findViewById(R.id.weeklypoints);
        dailyPoints = findViewById(R.id.dailypoints);
        username = findViewById(R.id.usernameProfileText);
        /*
         *  TODO
         */
    }
}
