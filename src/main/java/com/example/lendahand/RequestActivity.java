package com.example.lendahand;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends AppCompatActivity{
    //UI Elements
    EditText edItemName, edQuantity;
    Button btnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        edItemName = findViewById(R.id.editTextItemName);
        edQuantity = findViewById(R.id.editTextQuantity);
        btnRequest = findViewById(R.id.buttonSubmitRequest);

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Get input values
                String itemName = edItemName.getText().toString().trim();
                String quantity = edQuantity.getText().toString().trim();

                //Making sure the user has entered values
                if (itemName.isEmpty() || quantity.isEmpty()){
                    Toast.makeText(RequestActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
                String bio = preferences.getString("bio", "");
                String phonenumber = preferences.getString("phonenumber", "");

                if (bio.isEmpty() || phonenumber.isEmpty()){
                    Toast.makeText(RequestActivity.this, "Missing user bio/contact number", Toast.LENGTH_SHORT).show();
                    return;
                }

                //submitting the input values
                submitRequest(itemName, quantity, bio, phonenumber);
            }
        });
    }

    //Method to submit input values
    private void submitRequest(String itemName, String quantity, String bio, String phonenumber){
        //URL
        String url = "https://lamp.ms.wits.ac.za/home/s2814875/submit_request.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<>();
        params.put("item_name", itemName);
        params.put("quantity", quantity);
        params.put("bio", bio);
        params.put("phonenumber", phonenumber);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            String message = jsonResponse.getString("message");

                            Toast.makeText(RequestActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (success) {
                                edItemName.setText("");
                                edQuantity.setText("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RequestActivity.this, "Invalid server response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(RequestActivity.this, "Failed to submit request", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(request);
    }
}