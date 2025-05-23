package com.example.lendahand;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonateActivity extends AppCompatActivity {

    // Declare global variables for UI components
    Spinner itemSpinner;//declared globally
    EditText quantityInput, customItemInput;
    Button submitDonationButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Loads the layout XML

        // Initialize UI components from XML layout
        itemSpinner = findViewById(R.id.itemSpinner);//in the onCreate method
        quantityInput = findViewById(R.id.quantityInput);
        customItemInput = findViewById(R.id.customItemInput);
        submitDonationButton = findViewById(R.id.submitDonationButton);

        // List to populate the Spinner
        List<String> itemList = new ArrayList<>();
        itemList.add("Select an item"); // Default prompt

        // Set up the Spinner with a basic ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);

        // Show/hide the custom item field if "Other" is selected
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = itemSpinner.getSelectedItem().toString();
                if (selected.equals("Other")) {
                    customItemInput.setVisibility(View.VISIBLE); // Show field
                } else {
                    customItemInput.setVisibility(View.GONE); // Hide field
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load item names from your PHP backend
        String url = "https://lamp.ms.wits.ac.za/home/s2611748/get_items.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    // Loop through JSON array and add items to Spinner
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            itemList.add(response.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged(); // Refresh the spinner
                },
                error -> Toast.makeText(this, "Failed to load items", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request); // Send the GET request

        // Set up the submit button listener
        submitDonationButton.setOnClickListener(this::doSubmit);
    }

    // Called when the user clicks "Submit Donation"
    public void doSubmit(View v) {
        if (!validateForm()) return; // Stop if input is invalid

        // Get selected or custom item name
        String selectedItem = itemSpinner.getSelectedItem().toString();
        String customItem = customItemInput.getText().toString().trim();
        String finalItem = selectedItem.equals("Other") ? customItem : selectedItem;

        // Get quantity
        String quantityStr = quantityInput.getText().toString().trim();

        // Get username from shared preferences (saved on login)
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // PHP script to handle submission
        String url = "https://lamp.ms.wits.ac.za/home/s2611748/submit_donation.php";

        // Create POST request using Volley
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            Toast.makeText(this, "Donation submitted successfully!", Toast.LENGTH_LONG).show();

                            // Reset fields after success
                            itemSpinner.setSelection(0);
                            quantityInput.setText("");
                            customItemInput.setText("");
                        } else {
                            Toast.makeText(this, "Submission error.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "JSON error.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error.", Toast.LENGTH_SHORT).show()
        ) {
            // Attach POST parameters to the request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("item", finalItem);
                params.put("quantity", quantityStr);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request); // Execute the POST request
    }

    // Validates form input before submission
    public boolean validateForm() {
        String selectedItem = itemSpinner.getSelectedItem().toString();
        String quantityStr = quantityInput.getText().toString().trim();
        String customItem = customItemInput.getText().toString().trim();
        String finalItem = selectedItem.equals("Other") ? customItem : selectedItem;

        // Validate item input
        if (finalItem.isEmpty() || selectedItem.equals("Select an item")) {
            Toast.makeText(this, "Please select or enter an item.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate quantity input
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Ensure quantity is a positive number
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity entered.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // All good
    }
}
