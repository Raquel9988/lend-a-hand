package com.example.lendahand;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

public class LeaderboardRow extends LinearLayout {

    private TextView rankView;
    private TextView nameView;
    private TextView totalView;

    public LeaderboardRow(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setPadding(20, 20, 20, 20);
        setBackgroundResource(R.drawable.leaderboard); // Background drawable
        setElevation(6f);

        // Initialize TextViews
        rankView = new TextView(context);
        nameView = new TextView(context);
        totalView = new TextView(context);

        int textColor = Color.parseColor("#333333");

        rankView.setTextColor(textColor);
        nameView.setTextColor(textColor);
        totalView.setTextColor(textColor);

        rankView.setPadding(10, 10, 10, 10);
        nameView.setPadding(10, 10, 10, 10);
        totalView.setPadding(10, 10, 10, 10);

        rankView.setTextSize(16);
        nameView.setTextSize(16);
        totalView.setTextSize(16);

        // Equal width distribution
        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        addView(rankView, params);
        addView(nameView, params);
        addView(totalView, params);

        // Apply margin to the whole row
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(16, 8, 16, 8);
        setLayoutParams(rowParams);
    }

    // Populate row with integer data (used for real leaderboard entries)
    public void populate(int rank, String name, int total) {
        rankView.setText(String.valueOf(rank));
        nameView.setText(name);
        totalView.setText(String.valueOf(total));

        // Style for data rows
        rankView.setTypeface(null, Typeface.BOLD);
        nameView.setTypeface(null, Typeface.BOLD);
        totalView.setTypeface(null, Typeface.BOLD);
    }

    // Overloaded method to populate header row
    public void populate(String rank, String name, String total) {
        rankView.setText(rank);
        nameView.setText(name);
        totalView.setText(total);

        // Style for header row
        rankView.setTypeface(null, Typeface.BOLD_ITALIC);
        nameView.setTypeface(null, Typeface.BOLD_ITALIC);
        totalView.setTypeface(null, Typeface.BOLD_ITALIC);
    }
}
