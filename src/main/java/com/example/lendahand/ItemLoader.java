package com.example.lendahand;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;

public class ItemLoader {

    public static void loadItems(Context context, Spinner spinner, boolean includeOther) {
        ArrayList<String> itemList = new ArrayList<>();

        // Always include default option
        itemList.add("Select an item");
        if (includeOther) itemList.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Fetch items from server
        String url = "https://lamp.ms.wits.ac.za/home/s2611748/get_items.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            String item = response.getString(i);

                            // Avoid duplicate 'Other' or 'Select' entries
                            if (!item.equalsIgnoreCase("Other") && !item.equalsIgnoreCase("Select an item")) {
                                itemList.add(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(context, "Failed to load items", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(context).add(request);
    }
}
