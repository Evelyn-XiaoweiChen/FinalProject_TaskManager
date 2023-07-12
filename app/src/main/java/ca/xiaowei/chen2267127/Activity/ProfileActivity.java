package ca.xiaowei.chen2267127.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ca.xiaowei.chen2267127.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, profile, logout;
    FirebaseAuth mAuth;
    TextView usernameText, emailText;
    Button editProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeFirebaseAuth();
        initialize();
        fetchDataFromFirestore();
    }

    private void initializeFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void initialize() {
        drawerLayout = findViewById(R.id.drawerLayout);
        usernameText = findViewById(R.id.profileUsername);
        emailText = findViewById(R.id.profileEmail);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        profile = findViewById(R.id.profile);
        logout = findViewById(R.id.exitApp);
        editProfileBtn = findViewById(R.id.editProfileButton);
        editProfileBtn.setOnClickListener(this);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ProfileActivity.this, HomeActivity.class);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ProfileActivity.this, LoginActivity.class);
            }
        });
    }

    public void fetchDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // DocumentSnapshot contains the data
                                String username = document.getString("username");
                                String email = document.getString("email");

                                // Update your UI with the retrieved data
                                // For example, set the username and email in TextViews
                                usernameText.setText(username);
                                emailText.setText(email);
                            } else {
                                // Toast.makeText(this,"user does not exist",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error fetching data from Firestore
                        }
                    }
                });
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.editProfileButton) {
            showUpdatePasswordDialog();
        }
    }

    private void showUpdatePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.update_password_dialog, null);

        EditText currentPasswordEditText = dialogView.findViewById(R.id.textCurrentPassword);
        EditText newPasswordEditText = dialogView.findViewById(R.id.textNewPassword);
        EditText confirmNewPasswordEditText = dialogView.findViewById(R.id.textConfirmNewPassword);

        builder.setView(dialogView)
                .setTitle("Update Password")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String currentPassword = currentPasswordEditText.getText().toString();
                        String newPassword = newPasswordEditText.getText().toString();
                        String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

                        // Perform password update logic here
                        // Call the method to update the password
                        updatePassword(currentPassword, newPassword, confirmNewPassword);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updatePassword(String currentPassword, String newPassword, String confirmNewPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Check if the new password and confirm password match
            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, "New password and confirm password do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Reauthenticate the user with their current password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Reauthentication successful, update the password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Password updated successfully
                                                    Toast.makeText(ProfileActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();

                                                    //keep in mind that the password field in Firestore is not automatically synchronized with the updated password.
                                                    // need to explicitly update the password field in Firestore after a successful password update.
                                                    updatePasswordInFirestore(newPassword);
                                                    // Send email notification to the user
                                                    sendPasswordChangeEmail();
                                                } else {
                                                    // Failed to update password
                                                    Toast.makeText(ProfileActivity.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                                                }
                                            }


                                        });
                            } else {
                                // Reauthentication failed
                                Toast.makeText(ProfileActivity.this, "Failed to authenticate with current password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // synchronous firestore after updating psw
    private void updatePasswordInFirestore(String newPassword) {
        String userId = mAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("users").document(userId);
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("password", newPassword);

        userRef.update(updatedData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password field updated in Firestore
                            Toast.makeText(ProfileActivity.this, "Password updated in Firestore.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update password field in Firestore
                            Toast.makeText(ProfileActivity.this, "Failed to update password in Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendPasswordChangeEmail() {
        String email = mAuth.getCurrentUser().getEmail();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Email sent successfully
                            Toast.makeText(ProfileActivity.this, "Password change email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Email failed to send
                            Toast.makeText(ProfileActivity.this, "Failed to send password change email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}