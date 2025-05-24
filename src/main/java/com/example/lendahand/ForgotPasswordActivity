package com.example.lendahand;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText editTextNewPassword, editTextConfirmPassword;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextNewPassword = findViewById(R.id.newPassword);
        editTextConfirmPassword = findViewById(R.id.confirmPassword);

        // Get username from Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Please enter your username on the login screen first.", Toast.LENGTH_LONG).show();
        }
    }

    // Back button clicked
    public void doBackFP(View view) {
        finish();  // return to LoginActivity
    }

    // Update password button clicked
    public void doUpdatePassword(View view) {
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Username not provided. Please return to login and enter username.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in both password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        new ResetPasswordTask().execute(username, newPassword);
    }

    private class ResetPasswordTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String newPassword = params[1];

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2611748/update_password.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "username=" + username + "&new_password=" + newPassword;

                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                return (responseCode == HttpURLConnection.HTTP_OK) ? "success" : "server_error";

            } catch (Exception e) {
                e.printStackTrace();
                return "exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if ("success".equals(result)) {
                Toast.makeText(ForgotPasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                finish();  // go back to LoginActivity
            } else if ("server_error".equals(result)) {
                Toast.makeText(ForgotPasswordActivity.this, "Server error. Try again later.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Error occurred. Check your connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
