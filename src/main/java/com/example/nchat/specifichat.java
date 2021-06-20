package com.example.nchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class specifichat extends AppCompatActivity {

    EditText getMessage;
    ImageButton sendMessageButton;
    CardView sendMessageCardView;
    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;
    ImageView imageViewOfSpecificUser;
    TextView nameOfSpecificUser;

    private String enteredMessage;
    Intent intent;
    String recieverName,senderName,recieverUid,senderUid;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    String senderRoom,recieverRoom;

    ImageButton backButtonOfSpecificChat;

    RecyclerView recyclerView;

    MessagesAdapter messagesAdapter;
    ArrayList<messages> messagesArrayList;



    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specifichat);

        getMessage=findViewById(R.id.getmessage);
        sendMessageCardView=findViewById(R.id.carviewofsendmessage);
        sendMessageButton=findViewById(R.id.imageviewofsendmessage);
        mtoolbarofspecificchat=findViewById(R.id.toolbarofspecificchat);
        nameOfSpecificUser=findViewById(R.id.nameofspecificuser);
        imageViewOfSpecificUser=findViewById(R.id.specificuserimageinimageview);
        backButtonOfSpecificChat=findViewById(R.id.backbuttonofspecificchat);
        intent=getIntent();
        messagesArrayList=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerviewofspecific);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter=new MessagesAdapter(specifichat.this,messagesArrayList);

        recyclerView.setAdapter(messagesAdapter);


        setSupportActionBar(mtoolbarofspecificchat);

        mtoolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            //clicked on toolbar to view detailed profile
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Toolbar is clicked",Toast.LENGTH_SHORT).show();

            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a");

        senderUid=firebaseAuth.getUid();
        recieverUid=getIntent().getStringExtra("receiveruid");
        recieverName=getIntent().getStringExtra("name");

        senderRoom=senderUid+recieverUid;
        recieverRoom=recieverUid+senderUid;


        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");
        messagesAdapter = new MessagesAdapter(specifichat.this,messagesArrayList);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                messagesArrayList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    messages messages = snapshot1.getValue(messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });



        backButtonOfSpecificChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nameOfSpecificUser.setText(recieverName);
        String uri=intent.getStringExtra("imageuri");

        if(uri.isEmpty()){
            Toast.makeText(getApplicationContext(),"Null is recieved",Toast.LENGTH_SHORT).show();
        }
        else {
            Picasso.get().load(uri).into(imageViewOfSpecificUser);
        }

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredMessage=getMessage.getText().toString();
                if(enteredMessage.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter the message",Toast.LENGTH_SHORT).show();

                }
                else{

                    Date date = new Date();
                    currentTime=simpleDateFormat.format(calendar.getTime());

                    messages messages = new messages(enteredMessage,firebaseAuth.getUid(),date.getTime(),currentTime);

                    firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference()
                                    .child("chats")
                                    .child(recieverRoom)
                                    .child("messages")
                                    .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    });

                    getMessage.setText(null);


                }
                

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesAdapter!=null){
            messagesAdapter.notifyDataSetChanged();
        }
    }
}