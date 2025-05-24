package com.example.lendahand;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.util.*;

public class MatchActivity extends AppCompatActivity {

    EditText itemInput;
    Button matchButton;
    ListView matchList;
    ArrayAdapter<String> adapter;
    ArrayList<String> userList = new ArrayList<>();
    ArrayList<JSONObject> matchData = new ArrayList<>();

    String MATCH_URL = "https://lamp.ms.wits.ac.za/home/s2799582/matching.php";
    String DONATE_URL = "https://lamp.ms.wits.ac.za/home/s2611748/get_items.php";

    String donorUsername = "Username"; // Replace with actual logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        itemInput = findViewById(R.id.itemInput);
        matchButton = findViewById(R.id.matchButton);
        matchList = findViewById(R.id.matchList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        matchList.setAdapter(adapter);

        matchButton.setOnClickListener(v -> fetchMatches());

        matchList.setOnItemClickListener((parent, view, position, id) -> {
            try {
                JSONObject selected = matchData.get(position);
                showDonateDialog(selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button buttonBackHome = findViewById(R.id.BackHome);
        buttonBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // optional: closes MatchActivity
            }
        });
    }

    private void fetchMatches() {
        String itemName = itemInput.getText().toString().trim();
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Enter item name", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = MATCH_URL + "?item_name=" + Uri.encode(itemName);
        userList.clear();
        matchData.clear();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            matchData.add(obj);
                            String info = obj.getString("username") + " (" +
                                    obj.getString("quantity_needed") + ") - " +
                                    obj.getString("contact_number");
                            userList.add(info);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Failed to load matches", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void showDonateDialog(JSONObject recipient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter quantity to donate");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Donate", (dialog, which) -> {
            String qtyStr = input.getText().toString().trim();
            if (!qtyStr.isEmpty()) {
                int quantity = Integer.parseInt(qtyStr);
                try {
                    donateItem(recipient, quantity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void donateItem(JSONObject recipient, int quantity) throws JSONException {
        int requestId = recipient.getInt("id");
        String itemName = itemInput.getText().toString().trim();

        StringRequest postRequest = new StringRequest(Request.Method.POST, DONATE_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(this, "Donation successful!", Toast.LENGTH_SHORT).show();
                            fetchMatches(); // Refresh list
                        } else {
                            Toast.makeText(this, "Donation failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error sending donation", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("donor_username", donorUsername);
                params.put("item_name", itemName);
                params.put("quantity_donated", String.valueOf(quantity));
                params.put("request_id", String.valueOf(requestId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(postRequest);
    }
}
