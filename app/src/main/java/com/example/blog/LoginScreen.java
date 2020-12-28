package com.example.blog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Utils.BlogApi;

public class LoginScreen extends AppCompatActivity {

    private AutoCompleteTextView email;
    private EditText password;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();

        Button registrationBtn = findViewById(R.id.registrationBtn);
        Button loginBtn = findViewById(R.id.signInBtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.loading_bar_login);

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, RegistrationScreen.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();

                if (!TextUtils.isEmpty(emailValue) && !TextUtils.isEmpty(passwordValue)) {
                    loginApp(emailValue, passwordValue);
                } else {
                 Toast toast = Toast.makeText(LoginScreen.this, "Empty fields not allowed!", Toast.LENGTH_SHORT);
                 toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                 toast.show();
                }
            }

            private void loginApp(String emailValue, String passwordValue) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            String currentUserId = user.getUid();

                            collectionReference
                                .whereEqualTo("userId", currentUserId)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value,
                                                        @Nullable FirebaseFirestoreException error) {
                                        assert value != null;
                                        if (!value.isEmpty()) {
                                            for (QueryDocumentSnapshot snapshot: value) {
                                                BlogApi blogApi = BlogApi.getInstance();
                                                blogApi.setUsername(snapshot.getString("username"));
                                                blogApi.setUserId(snapshot.getString("userId"));

                                                startActivity(new Intent(LoginScreen.this, PostListScreen.class));
                                            }
                                        }
                                    }
                                });
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast toast = Toast.makeText(LoginScreen.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    });
            }
        });
    }
}