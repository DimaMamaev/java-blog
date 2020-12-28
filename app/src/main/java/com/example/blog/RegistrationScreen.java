package com.example.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import Utils.BlogApi;

public class RegistrationScreen extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private EditText username;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);
        getSupportActionBar().setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();

        Button registrationBtn = findViewById(R.id.registrationUserBtn);
        email = findViewById(R.id.email_registration);
        password = findViewById(R.id.password_registration);
        username = findViewById(R.id.username_acc_registration);
        progressBar = findViewById(R.id.loading_bar_registration);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null) {
                } else {
                }
            }
        };


        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email.getText().toString()) &&
                    !TextUtils.isEmpty(password.getText().toString()) &&
                    !TextUtils.isEmpty(username.getText().toString())  ) {

                    String validEmailString = email.getText().toString().trim();
                    String validPasswordString = password.getText().toString().trim();
                    String validUsernameString = username.getText().toString().trim();

                    createAccount(validEmailString, validPasswordString, validUsernameString);
                } else {
                    Toast toast = Toast.makeText(RegistrationScreen.this, "Empty fields are not allowed", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }


    private void createAccount(String email, String password, String username) {

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            String currentUserId = currentUser.getUid();

                            Map<String, String> userObject = new HashMap<>();
                            userObject.put("userId", currentUserId);
                            userObject.put("username", username);

                            collectionReference.add(userObject)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.getResult().exists()) {
                                                       progressBar.setVisibility(View.INVISIBLE);

                                                       String name = task.getResult().getString("username");

                                                        BlogApi blogApi = BlogApi.getInstance();
                                                        blogApi.setUserId(currentUserId);
                                                        blogApi.setUsername(name);

                                                        startActivity(new Intent(RegistrationScreen.this, PostScreen.class));
                                                    } else {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                            });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast toast = Toast.makeText(RegistrationScreen.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                                        toast.show();                                    }
                                });

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast toast = Toast.makeText(RegistrationScreen.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                        toast.show();                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}