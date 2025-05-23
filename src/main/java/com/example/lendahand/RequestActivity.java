package com.example.lendahand;

import android.content.Intent;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends AppCompatActivity{
    //UI Elements
    Spinner itemSpinner;
    EditText edQuantity;
    Button btnRequest;
    ArrayAdapter<String> adapter;
    ArrayList<String> itemList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        itemSpinner=findViewById(R.id.itemSpinner);
        edQuantity=findViewById(R.id.editTextQuantity);
        btnRequest=findViewById(R.id.buttonSubmitRequest);

        Button buttonBack=findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        itemList.add("Select an item");

        // Set up spinner adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);

        // Fetch item list from server
        loadItemsFromServer();

        // Submit button click
        btnRequest.setOnClickListener(this::submitRequest);
    }

    //loading all items from server to itemSpinner
    private void loadItemsFromServer() {
        String url="https://lamp.ms.wits.ac.za/home/s2814875/get_items.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            itemList.add(response.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Failed to load items", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    //submitting requests
    private void submitRequest(View v) {
        if (!validateForm()) return;

        String selectedItem = itemSpinner.getSelectedItem().toString();
        String quantityStr = edQuantity.getText().toString().trim();

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        if (username == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://lamp.ms.wits.ac.za/home/s2814875/submit_request.php";


        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Request submitted!", Toast.LENGTH_SHORT).show();
                    itemSpinner.setSelection(0);
                    edQuantity.setText("");
                },
                error -> Toast.makeText(this, "Submission failed.", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("item", selectedItem);
                params.put("quantity", quantityStr);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    //making sure that everything is entered correctly
    private boolean validateForm() {
        String selectedItem = itemSpinner.getSelectedItem().toString();
        String quantityStr = edQuantity.getText().toString().trim();

        if (selectedItem.equals("Select an item")) {
            Toast.makeText(this, "Please select an item.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity.", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}