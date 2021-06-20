package com.example.nchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    
    EditText getPhoneNumber;
    android.widget.Button sendOTP;
    CountryCodePicker countryCodePicker;
    String countryCode;
    String phoneNumber;

    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryCodePicker = findViewById(R.id.countryCodePicker);
        sendOTP = findViewById(R.id.sendOTPButton);
        getPhoneNumber =findViewById(R.id.phoneNumber);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth=FirebaseAuth.getInstance();

        countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode=countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                number = getPhoneNumber.getText().toString();
                if (number.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter Your Phone Number",Toast.LENGTH_SHORT).show();
                }else if (number.length() < 10){
                    Toast.makeText(getApplicationContext(),"Please Enter a Valid Phone Number",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressBar.setVisibility(view.VISIBLE);
                    phoneNumber=countryCode+number;

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(callbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);

                }

            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //To fetch the OTP Automatically from app

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(),"OTP Sent To Registered Number",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                codeSent=s;

                Intent intent = new Intent(MainActivity.this, OTPverification.class);
                intent.putExtra("OTP",codeSent);
                startActivity(intent);

            }
        };


    }
    //If user is logging second time after OTP verification
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, chatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}