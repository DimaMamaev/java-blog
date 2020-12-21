package com.example.blog;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import Utils.BlogApi;
import model.Post;

public class PostScreen extends AppCompatActivity implements View.OnClickListener {

    private static final int GALARY_CODE = 1;
    private ProgressBar progressBar;
    private EditText postTitle;
    private EditText postDesc;
    private ImageView postImage;

    private String currentUserId;
    private String getCurrentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private final CollectionReference collectionReference = db.collection("Posts");
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_screen);
        getSupportActionBar().setElevation(0);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar_post);
        Button saveBtn = findViewById(R.id.button_post_create);
        ImageView addPhoto = findViewById(R.id.add_post_image);
        postTitle = findViewById(R.id.post_title);
        postDesc = findViewById(R.id.post_description);
        TextView postUser = findViewById(R.id.post_username);
        TextView postDate = findViewById(R.id.post_date);
        postImage = findViewById(R.id.post_image);

        progressBar.setVisibility(View.INVISIBLE);
        saveBtn.setOnClickListener(this);
        addPhoto.setOnClickListener(this);

        if (BlogApi.getInstance() != null) {
            currentUserId = BlogApi.getInstance().getUserId();
            getCurrentUserName = BlogApi.getInstance().getUsername();
            postUser.setText(getCurrentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_post_create:
                savePost();
                break;
            case R.id.add_post_image:
                Intent toGalery = new Intent(Intent.ACTION_GET_CONTENT);
                toGalery.setType("image/*");
                startActivityForResult(toGalery, GALARY_CODE);
                break;
        }
    }

    private void savePost() {
        String title = postTitle.getText().toString().trim();
        String description = postDesc.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageUri != null) {
                StorageReference imagePath = storageReference
                        .child("post_images")
                        .child("img"+ Timestamp.now().getSeconds());
                imagePath.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        Post postModel = new Post();
                                        postModel.setTitle(title);
                                        postModel.setDescription(description);
                                        postModel.setImageUrl(imageUrl);
                                        postModel.setTimeAdd(new Timestamp(new Date()));
                                        postModel.setUserName(getCurrentUserName);
                                        postModel.setUserId(currentUserId);


                                        collectionReference.add(postModel)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        progressBar.setVisibility(View.INVISIBLE);

                                                        startActivity(new Intent(PostScreen.this, PostListScreen.class));
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(PostScreen.this,
                                                                "Error"+ e.getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(PostScreen.this,
                                        "Error"+ e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
        } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PostScreen.this,
                        "Empty fields and no image downloaded are not allowed!",
                        Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(PostScreen.this, "Oops something went wrong, image hasn't been downloaded!", Toast.LENGTH_LONG).show();
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

