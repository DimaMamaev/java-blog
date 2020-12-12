package com.example.blog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class PostScreen extends AppCompatActivity {

    private Button saveBtn;
    private ProgressBar progressBar;
    private ImageView addPhoto;
    private EditText postTitle;
    private EditText postDesc;
    private TextView postUser;
    private TextView postDate;

    private String currentUserId;
    private String getCurrentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Posts");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_screen);

        progressBar = findViewById(R.id.progressBar_post);
        saveBtn = findViewById(R.id.button_post_create);
        addPhoto = findViewById(R.id.add_post_image);
        postTitle = findViewById(R.id.post_title);
        postDesc = findViewById(R.id.post_description);
        postUser = findViewById(R.id.post_username);
        postDate = findViewById(R.id.post_date);




        firebaseAuth = FirebaseAuth.getInstance();



    }
}