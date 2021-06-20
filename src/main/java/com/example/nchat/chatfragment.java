package com.example.nchat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class chatfragment extends Fragment {

    private FirebaseFirestore firebasefirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;

    ImageView imageViewOfUser;
    FirestoreRecyclerAdapter<firebasemodel,NotViewHolder> chatAdapter;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chatfragment,container,false);

        firebaseAuth=FirebaseAuth.getInstance();
        firebasefirestore=FirebaseFirestore.getInstance();
        recyclerView= v.findViewById(R.id.recyclerView);

        /*How to fetch all users

        Query query = firebasefirestore.collection("Users");
        */

        //To fetch all users except me
        Query query = firebasefirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid());
        FirestoreRecyclerOptions<firebasemodel> allusername =  new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<firebasemodel, NotViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NotViewHolder holder, int position, @NonNull firebasemodel model) {
                holder.particularuser.setText(model.getName());
                String uri = model.getImage();

                Picasso.get().load(uri).into(imageViewOfUser);

                if (model.getStatus().equals("Online")){
                    holder.statusOfUser.setText(model.getStatus());
                    holder.statusOfUser.setTextColor(Color.GREEN);
                }else{
                    holder.statusOfUser.setText(model.getStatus());
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getActivity(),"Item is clicked",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),specifichat.class);
                        intent.putExtra("name",model.getName());
                        intent.putExtra("receiveruid",model.getUid());
                        intent.putExtra("imageuri",model.getImage());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public NotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout,parent,false);
               return new NotViewHolder(view);
            }
        };

        recyclerView.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);


        return v;


    }

    public class NotViewHolder extends RecyclerView.ViewHolder{

        private TextView particularuser;
        private TextView statusOfUser;

        public NotViewHolder(View itemView) {
            super(itemView);
            particularuser=itemView.findViewById(R.id.nameOfUser);
            statusOfUser=itemView.findViewById(R.id.statusOfUser);
            imageViewOfUser=itemView.findViewById(R.id.imageViewOfUser);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter!=null){
            chatAdapter.stopListening();
        }
    }
}
