package com.example.lendahand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_acitivty);

        SharedPreferences sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "");
        boolean isFirstLogin = sharedpreferences.getBoolean("isFirstLogin", true);

        // Show welcome toast only once after login
        if (isFirstLogin && !username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("isFirstLogin", false);
            editor.apply();
        }

        findViewById(R.id.cardDonate).setOnClickListener(view -> startActivity(new Intent(this, DonateActivity.class)));
        findViewById(R.id.cardRequest).setOnClickListener(view -> startActivity(new Intent(this, RequestActivity.class)));
        findViewById(R.id.cardMatch).setOnClickListener(view -> startActivity(new Intent(this, MatchActivity.class)));
        findViewById(R.id.cardLeaderboard).setOnClickListener(view -> startActivity(new Intent(this, LeaderBoardActivity.class)));
        findViewById(R.id.cardProfile).setOnClickListener(view -> startActivity(new Intent(this, ViewProfileActivity.class)));
        findViewById(R.id.cardUpdateBio).setOnClickListener(view -> startActivity(new Intent(this, UpdateActivity.class)));

        findViewById(R.id.cardExit).setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish(); // prevent back to home
        });
    }
}
