package com.example.lendahand;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    private LinearLayout leaderboardContainer;
    private Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        leaderboardContainer = findViewById(R.id.leaderboard_container);
        refreshButton = findViewById(R.id.refresh_button);

        loadLeaderboard();

        refreshButton.setOnClickListener(v -> {
            leaderboardContainer.removeAllViews();
            loadLeaderboard();
        });
    }

    private void loadLeaderboard() {
        new LoadLeaderboard().execute("https://lamp.ms.wits.ac.za/home/s1609751/donations.php");
    }

    private class LoadLeaderboard extends AsyncTask<String, Void, ArrayList<Donor>> {

        @Override
        protected ArrayList<Donor> doInBackground(String... urls) {
            ArrayList<Donor> donors = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray jsonArray = new JSONArray(result.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString("username");  // Corrected here
                    int amount = obj.getInt("amount_donated");

                    boolean found = false;
                    for (Donor donor : donors) {
                        if (donor.name.equals(name)) {
                            donor.total += amount;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        donors.add(new Donor(name, amount));
                    }
                }

                donors.sort((d1, d2) -> Integer.compare(d2.total, d1.total));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return donors;
        }

        @Override
        protected void onPostExecute(ArrayList<Donor> donors) {
            LeaderboardRow header = new LeaderboardRow(LeaderBoardActivity.this);
            header.populate("Position", "Username", "Amount Donated");
            leaderboardContainer.addView(header);

            int rank = 1;
            for (Donor donor : donors) {
                LeaderboardRow row = new LeaderboardRow(LeaderBoardActivity.this);
                row.populate(rank, donor.name, donor.total);
                leaderboardContainer.addView(row);
                rank++;
            }
        }
    }

    private static class Donor {
        String name;
        int total;

        Donor(String name, int total) {
            this.name = name;
            this.total = total;
        }
    }

    public void DoBackLB(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
