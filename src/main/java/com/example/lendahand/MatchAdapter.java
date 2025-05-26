package com.example.lendahand;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MatchAdapter extends ArrayAdapter<HashMap<String, String>> {
    private static final String TAG = "MatchAdapter";
    private final Context context;
    private final ArrayList<HashMap<String, String>> matches;

    public MatchAdapter(Context context, ArrayList<HashMap<String, String>> matches) {
        super(context, 0, matches);
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.match_list_item, parent, false);
            holder = new ViewHolder();
            holder.requesterUsername = convertView.findViewById(R.id.requesterUsername);
            holder.requestedItem = convertView.findViewById(R.id.requestedItem);
            holder.remainingQuantity = convertView.findViewById(R.id.remainingQuantity);
            holder.allocateButton = convertView.findViewById(R.id.allocateButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final HashMap<String, String> match = matches.get(position);

        holder.requesterUsername.setText("Requested by: " + match.get("requester_username"));
        holder.requestedItem.setText("Item: " + match.get("item_name"));
        holder.remainingQuantity.setText("Quantity Needed: " + match.get("quantity_needed"));

        holder.allocateButton.setOnClickListener(v -> showQuantityInputDialog(position, match));

        return convertView;
    }

    private void showQuantityInputDialog(int position, HashMap<String, String> match) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter donation quantity");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Quantity (max " + match.get("quantity_needed") + ")");
        builder.setView(input);

        builder.setPositiveButton("Donate", (dialog, which) -> {
            String quantityStr = input.getText().toString().trim();
            if (quantityStr.isEmpty()) {
                Toast.makeText(context, "Please enter a quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantity <= 0) {
                Toast.makeText(context, "Quantity must be positive", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantityNeeded = Integer.parseInt(match.get("quantity_needed"));
            if (quantity > quantityNeeded) {
                Toast.makeText(context, "Cannot donate more than needed", Toast.LENGTH_SHORT).show();
                return;
            }

            showConfirmationDialog(position, match, quantity);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showConfirmationDialog(final int position, HashMap<String, String> match, int quantity) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Donation")
                .setMessage(String.format("Donate %d of %s to %s?",
                        quantity,
                        match.get("item_name"),
                        match.get("requester_username")))
                .setPositiveButton("Confirm", (dialog, which) ->
                        allocateDonation(position, match, quantity))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void allocateDonation(final int position, HashMap<String, String> match, int quantity) {
        SharedPreferences prefs = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String donorUsername = prefs.getString("username", null);

        if (donorUsername == null) {
            Toast.makeText(context, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, Constants.MATCH_DONATION_URL,
                response -> handleAllocationResponse(position, match, quantity, response),
                error -> {
                    Log.e(TAG, "Allocation error: " + error.getMessage());
                    Toast.makeText(context, "Network error. Try again.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("donor_username", donorUsername);
                params.put("request_id", match.get("request_id"));
                params.put("quantity", String.valueOf(quantity));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void handleAllocationResponse(int position, HashMap<String, String> match, int quantity, String response) {
        try {
            JSONObject json = new JSONObject(response);
            String status = json.getString("status");
            String message = json.getString("message");

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            if (status.equals("success")) {
                if (context instanceof MatchActivity) {
                    ((MatchActivity) context).donateAmount(match.get("request_id"), quantity);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            Toast.makeText(context, "Error processing response", Toast.LENGTH_SHORT).show();
        }
    }

    private static class ViewHolder {
        TextView requesterUsername;
        TextView requestedItem;
        TextView remainingQuantity;
        Button allocateButton;
    }
}
