package com.example.lendahand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_acitivty);

        SharedPreferences sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "");
        Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_SHORT).show();

        CardView cardDonate = findViewById(R.id.cardDonate);
        cardDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, DonateActivity.class));
            }
        });

        CardView cardRequest = findViewById(R.id.cardRequest);
        cardRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RequestActivity.class));
            }
        });

        CardView cardMatch = findViewById(R.id.cardMatch);
        cardMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MatchActivity.class));
            }
        });

        CardView cardLeaderboard = findViewById(R.id.cardLeaderboard);
        cardLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, LeaderBoardActivity.class));
            }
        });

        CardView cardProfile = findViewById(R.id.cardProfile);
        cardProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ViewProfileActivity.class));
            }
        });

        CardView cardUpdateBio = findViewById(R.id.cardUpdateBio);
        cardUpdateBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, UpdateActivity.class));
            }
        });

        CardView cardExit = findViewById(R.id.cardExit);
        cardExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish(); // optional: to prevent back navigation
            }
        });
    }
}
