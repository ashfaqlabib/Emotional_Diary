package com.example.emotionaldiary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import com.example.emotionaldiary.models.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etEmail;
    private Button btnSaveProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_edit_profile);

        View root = findViewById(R.id.rootLayout);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {

            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    0,
                    bars.top,
                    0,
                    bars.bottom
            );

            return insets;
        });

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            return;
        }

        etEmail.setText(user.getEmail());

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        UserModel model = documentSnapshot.toObject(UserModel.class);

                        if (model != null) {

                            etFirstName.setText(model.getFirstName());
                            etLastName.setText(model.getLastName());

                        }
                    }
                });

        btnSaveProfile.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("Enter First Name");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Enter Last Name");
            return;
        }

        progressDialog.show();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            progressDialog.dismiss();
            return;
        }

        String fullName = firstName + " " + lastName;

        UserProfileChangeRequest request =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build();

        user.updateProfile(request)
                .addOnCompleteListener(task -> {

                    UserModel model = new UserModel(
                            firstName,
                            lastName,
                            user.getEmail(),
                            "",
                            System.currentTimeMillis()
                    );

                    db.collection("users")
                            .document(user.getUid())
                            .set(model)
                            .addOnSuccessListener(unused -> {

                                progressDialog.dismiss();

                                setResult(RESULT_OK);

                                finish();

                            });

                });
    }
}