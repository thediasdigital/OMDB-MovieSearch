package com.example.omdb_moviesearch.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.omdb_moviesearch.R;
import com.example.omdb_moviesearch.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();

        setContentView(binding.getRoot());

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.registerEmail.getText().toString();
                String pass = binding.registerPass.getText().toString();
                String confirm = binding.registerConfirm.getText().toString();

                // Make sure passwords match
                if (!pass.equals(confirm)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    return;
                }

                // Try to register user
                registerUser(email, pass);
            }
        });
    }

    private void registerUser(String email, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // Get the newly registered user
                            FirebaseUser user = auth.getCurrentUser();
                            String uid = auth.getUid();
                            // Add that user to the collection so I can populate their favourites later
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", user.getEmail());
                            db.collection("Users")
                              .document(uid)
                              .set(userData);
                            // Display message and return to login
                            Toast.makeText(Register.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(Register.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}