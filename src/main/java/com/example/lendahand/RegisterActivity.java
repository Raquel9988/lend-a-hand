package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class RegisterActivity extends AppCompatActivity {
    // UI Elements
    EditText edUsername, edEmail, edPassword, edConfirm, edPhoneNumber, edBio;
    Button btn;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI elements
        edUsername = findViewById(R.id.editTextUsernameReg);
        edPassword = findViewById(R.id.editTextPasswordReg);
        edEmail = findViewById(R.id.editTextEmailReg);
        edPhoneNumber = findViewById(R.id.editTextPhoneNumberReg);
        edConfirm = findViewById(R.id.editTextConfirmPasswordReg);
        edBio = findViewById(R.id.editTextBioReg);
        btn = findViewById(R.id.ButtonLoginRegistration);
        tv = findViewById(R.id.textViewRegistrationAlreadyHave);

        // Go to LoginActivity if already have an account
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // Register button click listener
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input values
                String username = edUsername.getText().toString();
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                String confirm = edConfirm.getText().toString();
                String phoneNumber = edPhoneNumber.getText().toString();
                String bio = edBio.getText().toString();

                // Validate password and confirm password
                if (!password.equals(confirm)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate password requirements
                if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*()].*")) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters, contain a number, and a special character!", Toast.LENGTH_LONG).show();
                    return;
                }

                // Send registration request to PHP server
                registerUser(username, email, password, phoneNumber, bio);
            }
        });
    }

    // Method to register user by sending data to PHP server
    private void registerUser(String username, String email, String password, String phoneNumber, String bio) {
        // URL of your PHP script (Make sure to change this to the correct URL)
        String url = "https://lamp.ms.wits.ac.za/home/s1609751/registration.php";

        // Create a new Volley request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Map to hold the POST request parameters
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("email", email);
        params.put("password", password);
        params.put("phone_number", phoneNumber);
        params.put("bio", bio);

        // Create the POST request with Volley
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Parse the JSON response from the server
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    String message = jsonResponse.getString("message");

                    // Show a message based on success or failure
                    if (success) {
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Redirect to LoginActivity
                    } else {
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Error communicating with the server.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Return the parameters to be sent in the POST request
                return params;
            }
        };

        // Add the request to the Volley request queue
        queue.add(request);
    }
}
