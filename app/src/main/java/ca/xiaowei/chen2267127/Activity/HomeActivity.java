package ca.xiaowei.chen2267127.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.xiaowei.chen2267127.R;
import ca.xiaowei.chen2267127.Model.Task;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    TextView usernameTextView, weatherTextView, cityTextView;
    TextView totalTasksTextView, upcomingTaskTextView, notesTextView;
    ImageView weatherIconImageView;
    Spinner categorySpinner;
    String selectedCategory = "";
    private static final String API_KEY = "a15c30671e264bbc86d01146231407";
    private static final String API_URL = "https://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeFirebaseAuth();
        initializeFirestore();
        initialize();
        countTaskNumberFromFirestore();
        displayLatestTaskFromFirestore();
        bottomBarNavigate();
        // Execute the API request in a background thread
        new FetchWeatherTask().execute();
    }

    private void initializeFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initializeFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void initialize() {
        cityTextView = findViewById(R.id.cityTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        usernameTextView = findViewById(R.id.home_username);
        totalTasksTextView = findViewById(R.id.home_taskNumber);
        upcomingTaskTextView = findViewById(R.id.home_upcomingTask);
        notesTextView = findViewById(R.id.home_note);
        categorySpinner = findViewById(R.id.home_spinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();

                if ("All".equals(selectedCategory)) {
                    selectedCategory = ""; // Set selectedCategory as empty for all categories
                }

                updateUI(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when nothing is selected
            }
        });
    }

    private void updateUI(String selectedCategory) {
        if ("All".equals(selectedCategory)) {
            // Show total number, upcoming task, and notes for all categories
            countTaskNumberFromFirestore();
            displayLatestTaskFromFirestore();

        } else {
            // Show total number, upcoming task, and notes for the selected category
            countTaskNumberFromFirestore(selectedCategory);
            displayLatestTaskFromFirestore(selectedCategory);
        }

    }

    private void countTaskNumberFromFirestore() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String loggedInUserId = currentUser.getUid();

            CollectionReference tasksCollection = FirebaseFirestore.getInstance().collection("tasks");

            Query query = tasksCollection.whereEqualTo("userId", loggedInUserId);


            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                int totalTasks = queryDocumentSnapshots.size(); // Get the total number of tasks
                // Update UI with the totalTasks value
                totalTasksTextView.setText(String.valueOf(totalTasks));
            }).addOnFailureListener(e -> {
                // Handle any errors that occurred during data retrieval
                Toast.makeText(HomeActivity.this, "Failed to retrieve tasks from Firestore", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void countTaskNumberFromFirestore(String selectedCategory) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String loggedInUserId = currentUser.getUid();

            CollectionReference tasksCollection = FirebaseFirestore.getInstance().collection("tasks");

            Query query;

            if (TextUtils.isEmpty(selectedCategory)) {
                query = tasksCollection.whereEqualTo("userId", loggedInUserId);
            } else {
                query = tasksCollection.whereEqualTo("userId", loggedInUserId)
                        .whereEqualTo("category", selectedCategory.toLowerCase());
            }

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                int totalTasks = queryDocumentSnapshots.size(); // Get the total number of tasks
                // Update UI with the totalTasks value
                totalTasksTextView.setText(String.valueOf(totalTasks));
            }).addOnFailureListener(e -> {
                // Handle any errors that occurred during data retrieval
                Toast.makeText(HomeActivity.this, "Failed to retrieve tasks from Firestore", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void displayLatestTaskFromFirestore() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String loggedInUserId = currentUser.getUid();

            FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
            CollectionReference tasksCollection = firestoreDB.collection("tasks");
            CollectionReference usersCollection = firestoreDB.collection("users");
            usersCollection.document(loggedInUserId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");

                            // Use the username as needed
                            usernameTextView.setText(username);
                        } else {
                            // User document does not exist
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Failed to retrieve user document from Firestore
                    });

            // Query the Firestore collection for tasks belonging to the logged-in user and order them by date and time in descending order
            // set the index in firestore, notes: choose collection,not group for this case
            Query query = tasksCollection
                    .whereEqualTo("userId", loggedInUserId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(1);

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    // Extract task details from the document
                    String taskId = documentSnapshot.getId();
                    String title = documentSnapshot.getString("title");
                    String categories = documentSnapshot.getString("category");
                    String address = documentSnapshot.getString("address");
                    String notes = documentSnapshot.getString("notes");

                    // Convert Firestore timestamp to Date
                    Timestamp timestamp = documentSnapshot.getTimestamp("date");
                    Date date = timestamp != null ? timestamp.toDate() : null;

                    // Create a Task object with the retrieved details
                    Task latestTask = new Task(taskId, loggedInUserId, title, categories, address, notes, date, null);

                    upcomingTaskTextView.setText(latestTask.getTitle());
                    notesTextView.setText(latestTask.getNotes());

                    Log.d("Firestore", "Latest Task ID: " + taskId);
                    Log.d("Firestore", "Latest Task Title: " + title);
                } else {
                    // No tasks found
                    // Handle the case when there are no tasks available
                    Log.d("Firestore", "No tasks found");
                }
            }).addOnFailureListener(e -> {
                // Handle any errors that occurred during data retrieval
                Toast.makeText(HomeActivity.this, "Failed to retrieve the latest task from Firestore", Toast.LENGTH_SHORT).show();
                Log.e("Firestore", "Failed to retrieve the latest task from Firestore", e);
            });

        }
    }

    private void displayLatestTaskFromFirestore(String selectedCategory) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String loggedInUserId = currentUser.getUid();

            FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
            CollectionReference tasksCollection = firestoreDB.collection("tasks");

            Query query;

            if (TextUtils.isEmpty(selectedCategory)) {
                query = tasksCollection.whereEqualTo("userId", loggedInUserId)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .limit(1);
            } else {
                query = tasksCollection.whereEqualTo("userId", loggedInUserId)
                        .whereEqualTo("category", selectedCategory.toLowerCase())
                        .orderBy("date", Query.Direction.DESCENDING)
                        .limit(1);
            }

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    // Extract task details from the document
                    String taskId = documentSnapshot.getId();
                    String title = documentSnapshot.getString("title");
                    String category = documentSnapshot.getString("category");
                    String address = documentSnapshot.getString("address");
                    String notes = documentSnapshot.getString("notes");

                    // Convert Firestore timestamp to Date
                    Timestamp timestamp = documentSnapshot.getTimestamp("date");
                    Date date = timestamp != null ? timestamp.toDate() : null;

                    // Create a Task object with the retrieved details
                    Task latestTask = new Task(taskId, loggedInUserId, title, category, address, notes, date, null);
                    Log.d("Firestore", "Latest Task ID: " + taskId);
                    Log.d("Firestore", "Latest Task Title: " + title);
                    Log.d("Firestore", "Latest Task Category: " + category);
                    upcomingTaskTextView.setText(latestTask.getTitle());
                    notesTextView.setText(latestTask.getNotes());
                } else {
                    upcomingTaskTextView.setText("None");
                    notesTextView.setText("None");
                }
            }).addOnFailureListener(e -> {
                // Handle any errors that occurred during data retrieval
                Toast.makeText(HomeActivity.this, "Failed to retrieve the latest task from Firestore", Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void bottomBarNavigate() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomBar);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            } else if (item.getItemId() == R.id.home_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.home_list) {
                startActivity(new Intent(getApplicationContext(), TaskListActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.home_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            return false;
        });
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, WeatherData> {
        private String cityName = "Montreal";

        @Override
        protected WeatherData doInBackground(Void... voids) {
            try {
                String apiUrl = String.format(API_URL, cityName);

                // Create a URL object with the API URL
                URL url = new URL(apiUrl);

                // Create an HttpURLConnection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to GET
                connection.setRequestMethod("GET");

                // Get the response code
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Close the reader and connection
                    reader.close();
                    connection.disconnect();

                    // Parse the response
                    return parseWeatherData(response.toString());
                } else {
                    // Handle the error response
                    // ...
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(WeatherData weatherData) {
            if (weatherData != null) {
                // Display the weather data
                weatherTextView.setText("Temperature: " + weatherData.getTemperature() + "°C");
                cityTextView.setText("City: " + cityName);
                // Load the weather icon using a library like Picasso, Glide, or similar
                Picasso.get().load(weatherData.getIconUrl()).into(weatherIconImageView);
            } else {
                // Handle the error case
                // ...
            }
        }

        private WeatherData parseWeatherData(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject current = jsonObject.getJSONObject("current");
                double temperature = current.getDouble("temp_c");

                JSONObject condition = current.getJSONObject("condition");
                String icon = condition.getString("icon");
// Construct the complete icon URL
                String iconUrl = "https:" + icon;
                // Return the weather data
                return new WeatherData(temperature, iconUrl, cityName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private static class WeatherData {
        private final double temperature;
        private final String iconUrl;
        private final String cityName;

        public WeatherData(double temperature, String iconUrl, String cityName) {
            this.temperature = temperature;
            this.iconUrl = iconUrl;
            this.cityName = cityName;
        }

        public double getTemperature() {
            return temperature;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public String getCityName() {
            return cityName;
        }
    }
}