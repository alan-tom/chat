package com.alan.chat.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alan.chat.Model.Chat;
import com.alan.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private ArrayList<Chat> mChat;
    private FirebaseUser user;

    public MessageAdapter(Context mContext, ArrayList<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        if(viewType == MSG_TYPE_LEFT){
            layout = R.layout.chat_item_left;
        }else{
            layout = R.layout.chat_item_right;
        }
        return new MessageAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.message.setText(chat.getMessage());
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(chat.getSender().equals(user.getUid()) && chat.isSeen()){
            holder.seen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(user.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView message,seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.messageId);
            seen = itemView.findViewById(R.id.seenId);
            seen.setVisibility(View.GONE);
        }
    }
}
