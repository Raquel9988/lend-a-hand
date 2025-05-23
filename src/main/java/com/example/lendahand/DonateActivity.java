package com.example.lendahand;

import android.app.ProgressDialog;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
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
    // UI component declarations
    Spinner itemSpinner;          // Dropdown for selecting items to donate
    EditText quantityInput;       // Input field for donation quantity
    EditText customItemInput;     // Input field for custom items (when "Other" is selected)
    Button submitDonationButton;  // Button to submit the donation


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
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected (not needed here)
            }
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
        if (!validateForm()) return;// Don't proceed if validation fails

        // Get values from form inputs
        String selectedItem = itemSpinner.getSelectedItem().toString();
        String customItem = customItemInput.getText().toString().trim();
        String finalItem = selectedItem.equals("Other") ? customItem : selectedItem;
        String quantityStr = quantityInput.getText().toString().trim();

        // Show confirmation dialog before submitting
        new AlertDialog.Builder(this)
                .setTitle("Confirm Donation")
                .setMessage("You are about to donate:\n\n" +
                        quantityStr + " x " + finalItem + "\n\nProceed?")
                .setPositiveButton("YES", (dialog, which) -> {
                    // User confirmed - show loading dialog and proceed with submission
                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Submitting donation...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    submitDonationToServer(finalItem, quantityStr, progressDialog);
                })
                .setNegativeButton("NO", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
    // Submit donation data to server
    private void submitDonationToServer(String item, String quantity, ProgressDialog progressDialog) {
        // Get logged-in user's username from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://lamp.ms.wits.ac.za/home/s2611748/submit_donation.php";
        // Create POST request with Volley
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle server response
                    progressDialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("status");

                        if (status.equals("success")) {
                            showSuccessDialog(json.optString("message", "Donation submitted successfully!"));
                        } else {
                            // Show error message from server or default message
                            String errorMsg = json.optString("message", "Submission failed");
                            showErrorDialog(errorMsg);
                        }
                    } catch (JSONException e) {
                        showErrorDialog("Invalid server response");
                    }
                },
                error -> {
                    // Handle network errors
                    progressDialog.dismiss();
                    showErrorDialog("Network error: " + error.getMessage());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Set POST parameters for the request
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("item", item);
                params.put("quantity", quantity);
                return params;
            }
        };

        // Set retry policy for the request
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Reset form after successful submission
                    itemSpinner.setSelection(0);
                    quantityInput.setText("");
                    customItemInput.setText("");
                })
                .show();
    }

    private void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .show();
    }

    // Validates form input before submission
    public boolean validateForm() {
        String selectedItem = itemSpinner.getSelectedItem().toString();
        String quantityStr = quantityInput.getText().toString().trim();
        String customItem = customItemInput.getText().toString().trim();
        String finalItem = selectedItem.equals("Other") ? customItem : selectedItem;

        // Validate item selection
        if (selectedItem.equals("Select an item")) {
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate custom item
        if (selectedItem.equals("Other")) {
            if (customItem.isEmpty()) {
                Toast.makeText(this, "Please enter a custom item", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (customItem.length() < 4) {
                Toast.makeText(this, "Item name too short (min 4 chars)", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!customItem.matches("[a-zA-Z0-9 ]+")) {
                Toast.makeText(this, "Only letters, numbers and spaces allowed", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Validate quantity
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (quantity > 1000) {
                Toast.makeText(this, "Quantity too large (max 1000)", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity format", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
