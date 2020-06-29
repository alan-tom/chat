package com.alan.chat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alan.chat.MessageActivity;
import com.alan.chat.Model.Chat;
import com.alan.chat.Model.User;
import com.alan.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<User> mUsers;
    private boolean isChat;
    private String theLastMessage;

    public UserAdapter(Context mContext, ArrayList<User> mUsers, boolean isChat) {
        Log.d("UserProb","Entered1");
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("UserProb","Entered2");
        return new UserAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if (isChat){
            getLastMessage(user.getId(),holder.lastMessage);
        }else{
            holder.lastMessage.setText(user.getStatus());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("UserId",user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView username;
        private TextView lastMessage;
        private TextView lastTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("UserProb","Entered3");
            username = (TextView) itemView.findViewById(R.id.profileNameId);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessageId);
            lastTime = (TextView) itemView.findViewById(R.id.timeId);
        }
    }
    private void getLastMessage(final String userId, final TextView mLastMessage){
        theLastMessage ="default";
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(user.getUid().equals(chat.getReceiver()) && userId.equals(chat.getSender()) || userId.equals(chat.getReceiver()) && user.getUid().equals(chat.getSender())){
                        theLastMessage = chat.getMessage();
                    }
                }
                switch (theLastMessage){
                    case "default":
                        mLastMessage.setText(R.string.newChatMessage);
                        break;
                    default:
                        mLastMessage.setText(theLastMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
