package com.example.blog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

import Utils.BlogApi;

public class PostScreen extends AppCompatActivity implements View.OnClickListener {

    private static final int GALARY_CODE = 1;
    private Button saveBtn;
    private ProgressBar progressBar;
    private ImageView addPhoto;
    private EditText postTitle;
    private EditText postDesc;
    private TextView postUser;
    private TextView postDate;
    private ImageView postImage;

    private String currentUserId;
    private String getCurrentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Posts");
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_screen);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar_post);
        saveBtn = findViewById(R.id.button_post_create);
        addPhoto = findViewById(R.id.add_post_image);
        postTitle = findViewById(R.id.post_title);
        postDesc = findViewById(R.id.post_description);
        postUser = findViewById(R.id.post_username);
        postDate = findViewById(R.id.post_date);
        postImage = findViewById(R.id.post_image);

        saveBtn.setOnClickListener(this);

        if (BlogApi.getInstance() != null) {
            currentUserId = BlogApi.getInstance().getUserId();
            getCurrentUserName = BlogApi.getInstance().getUsername();

            postUser.setText(getCurrentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_post_create:
                break;
            case R.id.add_post_image:
                Intent toGalery = new Intent(Intent.ACTION_GET_CONTENT);
                toGalery.setType("image/*");
                startActivityForResult(toGalery, GALARY_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode, data);
        if(requestCode == GALARY_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                imageUri = data.getData();
                postImage.setImageURI(imageUri);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}

