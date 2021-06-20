package com.example.nchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPverification extends AppCompatActivity {
    
    TextView changeNumber;
    EditText getOTP;
    android.widget.Button verifyOTP;

    String enteredOTP;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBarOfVerifyOTP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        changeNumber = findViewById(R.id.changeNumber);
        getOTP = findViewById(R.id.getOTP);
        verifyOTP = findViewById(R.id.verifyOTP);

        progressBarOfVerifyOTP = findViewById(R.id.progressBarOfVerifyOTP);
        firebaseAuth = FirebaseAuth.getInstance();

        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OTPverification.this, MainActivity.class);

                startActivity(intent);
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredOTP = getOTP.getText().toString();
                if (enteredOTP.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter Your OTP", Toast.LENGTH_SHORT).show();
                }else{
                    progressBarOfVerifyOTP.setVisibility(View.VISIBLE);
                    String codeReceived = getIntent().getStringExtra("OTP");
                    //To compare the OTP received and OTP entered
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeReceived,enteredOTP);
                    signInWithPhoneAuthCredential(credential);


                }
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBarOfVerifyOTP.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "OTP Verification Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OTPverification.this,saveprofile.class);
                    startActivity(intent);
                    finish();
                }else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        progressBarOfVerifyOTP.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "OTP Verification Failed Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


}