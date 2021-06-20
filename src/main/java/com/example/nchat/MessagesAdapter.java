package com.example.nchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<messages> messagesArrayList;

    int ITEM_SEND=1;
    int ITEM_RECIEVE=2;

    public MessagesAdapter(Context context, ArrayList<messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return new SenderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.recieverchatlayout,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        messages messages = messagesArrayList.get(position);
        if (holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder=(SenderViewHolder)holder;
            viewHolder.textViewmessage.setText(messages.getMessage());
            viewHolder.timeOfMessage.setText(messages.getCurrentTime());
        }
        else{
            RecieverViewHolder viewHolder=(RecieverViewHolder)holder;
            viewHolder.textViewmessage.setText(messages.getMessage());
            viewHolder.timeOfMessage.setText(messages.getCurrentTime());
        }


    }

    @Override
    public int getItemViewType(int position) {
        messages messages = messagesArrayList.get(position);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.senderId)){
            return ITEM_SEND;
        }else{
            return ITEM_RECIEVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView textViewmessage;
        TextView timeOfMessage;

        public SenderViewHolder(View itemView) {
            super(itemView);

            textViewmessage=itemView.findViewById(R.id.senderMessage);
            timeOfMessage=itemView.findViewById(R.id.timeOfMessage);

        }
    }


    class RecieverViewHolder extends RecyclerView.ViewHolder{

        TextView textViewmessage;
        TextView timeOfMessage;

        public RecieverViewHolder(View itemView) {
            super(itemView);

            textViewmessage=itemView.findViewById(R.id.senderMessage);
            timeOfMessage=itemView.findViewById(R.id.timeOfMessage);

        }
    }




}
