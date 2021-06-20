package com.example.nchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class updateProfile extends AppCompatActivity {

    private EditText newUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private ImageView getNewUserinImageView;

    private StorageReference storageReference;
    private String ImageURIAccessToken;
    private androidx.appcompat.widget.Toolbar toolbarofUpdateProfile;
    private ImageButton backButtonOfUpdateProfile;

    ProgressBar progressBarOfUpdateProfile;
    android.widget.Button updateProfileButton;

    private Uri imagePath;
    Intent intent;

    private static int PICK_IMAGE=123;

    String newName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        toolbarofUpdateProfile=findViewById(R.id.toolbarOfUpdateProfile);
        backButtonOfUpdateProfile=findViewById(R.id.backButtonOfUpdateProfile);
        getNewUserinImageView=findViewById(R.id.getnewUserInUserImage);
        progressBarOfUpdateProfile=findViewById(R.id.progressBarOfUpdateProfile);
        newUserName=findViewById(R.id.getNewUserName);
        updateProfileButton=findViewById(R.id.updateProfileButton);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        intent = getIntent();
        setSupportActionBar(toolbarofUpdateProfile);

        backButtonOfUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        newUserName.setText(intent.getStringExtra("nameofuser"));

        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newName=newUserName.getText().toString();
                if(newName.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Name Is Empty",Toast.LENGTH_SHORT).show();
                }else if(imagePath!=null){
                    progressBarOfUpdateProfile.setVisibility(View.VISIBLE);
                    //update the name
                    userProfile userProfile= new userProfile(newName,firebaseAuth.getUid());
                    databaseReference.setValue(userProfile);

                    //updateimage
                    updateImagetoStorage();

                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                    progressBarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    Intent intent =new Intent(updateProfile.this,chatActivity.class);
                    startActivity(intent);
                    finish();

                }
                else{
                    progressBarOfUpdateProfile.setVisibility(View.VISIBLE);
                    //update the name
                    userProfile userProfile= new userProfile(newName,firebaseAuth.getUid());
                    databaseReference.setValue(userProfile);
                    updateNameOnCloudFirestore();
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();

                    progressBarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    Intent intent =new Intent(updateProfile.this,chatActivity.class);
                    startActivity(intent);
                    finish();



                }

            }
        });


        getNewUserinImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        storageReference=firebaseStorage.getReference();
        storageReference.child("images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIAccessToken=uri.toString();
                Picasso.get().load(uri).into(getNewUserinImageView);

            }
        });
    }

    private void updateNameOnCloudFirestore() {

        //overriding the existing data
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String , Object> userdata = new HashMap<>();
        userdata.put("name",newName);
        userdata.put("image",ImageURIAccessToken);
        userdata.put("Uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Profile Updated Successfully",Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void updateImagetoStorage() {

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
                        ImageURIAccessToken=uri.toString();
                        Toast.makeText(getApplicationContext(),"URI get success",Toast.LENGTH_SHORT).show();
                        updateNameOnCloudFirestore();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"URI get failed",Toast.LENGTH_SHORT).show();

                    }
                });

                Toast.makeText(getApplicationContext(),"Image Updated Successfully",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image not updated",Toast.LENGTH_SHORT).show();

            }
        });


    }


    //to get the image in Image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imagePath=data.getData();
            getNewUserinImageView.setImageURI(imagePath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());

        documentReference.update("status","offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"User is Offline",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());


        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"User is Online",Toast.LENGTH_SHORT).show();
            }
        });

    }
}