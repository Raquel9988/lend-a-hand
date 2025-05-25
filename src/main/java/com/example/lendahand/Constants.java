package com.example.lendahand;

public class Constants {
    // Base URL for all API endpoints
    public static final String BASE_URL = "https://lamp.ms.wits.ac.za/home/s2611748/";

    // User-related endpoints
    public static final String LOGIN_URL = BASE_URL + "login.php";
    public static final String REGISTER_URL = BASE_URL + "registration.php";
    public static final String REGISTER2_URL = BASE_URL + "registration2.php";

    // Item-related endpoints
    public static final String GET_ITEMS_URL = BASE_URL + "get_items.php";
    public static final String INSERT_ITEM_URL = BASE_URL + "insert_item.php";

    // Donation endpoints
    public static final String SUBMIT_DONATION_URL = BASE_URL + "submit_donation.php";
    public static final String SUBMIT_REQUEST_URL = BASE_URL + "submit_request.php";

    // Matching endpoints
    public static final String GET_MATCHES_URL = BASE_URL + "get_matches.php";
    public static final String MATCH_DONATION_URL = BASE_URL + "match_donation.php";

    // Shared Preferences keys
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String USERNAME_KEY = "username";

    // Validation constants
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_QUANTITY = 1000; // Maximum allowed quantity for donations/requests
}
