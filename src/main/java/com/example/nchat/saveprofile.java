package com.example.nchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class saveprofile extends AppCompatActivity {
    
    private CardView getUserImage;
    private ImageView getUserImageInImageView;
    private static int PICK_IMAGE = 123;
    private Uri imagePath;
    private EditText getUserName;
    private android.widget.Button saveProfile;

    private FirebaseAuth firebaseAuth;
    private String name;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String ImageUriAcessToken;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar progressBarOfSaveProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveprofile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        getUserName = findViewById(R.id.getUserName);
        getUserImage = findViewById(R.id.getUserImage);
        getUserImageInImageView = findViewById(R.id.getdp);
        saveProfile = findViewById(R.id.saveProfile);
        progressBarOfSaveProfile = findViewById(R.id.progressBarOfSaveProfile);

        getUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
                //just opens galaery and select the image
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=getUserName.getText().toString();
                if (name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Username is Empty", Toast.LENGTH_SHORT).show();
                }else if (imagePath == null){
                    Toast.makeText(getApplicationContext(), "Profile Image not selected", Toast.LENGTH_SHORT).show();
                }else {
                    progressBarOfSaveProfile.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    progressBarOfSaveProfile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(saveprofile.this,chatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
    private void sendDataForNewUser(){

        sendDataToRealTimeDatabase(); //First data to relatime database then storage to store the image then Firestore

    }

    private void sendDataToRealTimeDatabase(){
            //to work with realtime database we need a class but it is not required in cloud storage and firestore

        name=getUserName.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference =firebaseDatabase.getReference(firebaseAuth.getUid());//reference by the name of Uid to get the Uid

        userProfile userProfile = new userProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(userProfile); // to save the data in database
        Toast.makeText(getApplicationContext(),"User Profile Saved Successfully",Toast.LENGTH_SHORT).show();

        //to send image to database
        sendImageToStorage();


    }

    private void sendImageToStorage (){
        //First we will compress the image before adding into storage to avoid slowness
        StorageReference imageref = storageReference.child("images").child(firebaseAuth.getUid()).child("Profile Pic");
        //image compression code

        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();          //to create array of image


        //to store image in database
        UploadTask uploadTask = imageref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //to get the url of the image stored in database to store in firestore
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAcessToken=uri.toString();
                        Toast.makeText(getApplicationContext(),"URI get success",Toast.LENGTH_SHORT).show();
                        sendDataToCloudFireStore();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"URI get failed",Toast.LENGTH_SHORT).show();

                    }
                });

                Toast.makeText(getApplicationContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image not uploaded",Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void sendDataToCloudFireStore() {

        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String , Object> userdata = new HashMap<>();
        userdata.put("name",name);
        userdata.put("image",ImageUriAcessToken);
        userdata.put("Uid",firebaseAuth.getUid());
        userdata.put("status","Online");
        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Data on Cloud Firestore uploaded successfully",Toast.LENGTH_SHORT).show();

            }
        });

    }


    //to get the image in Image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imagePath=data.getData();
            getUserImageInImageView.setImageURI(imagePath);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}