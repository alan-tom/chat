package com.alan.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alan.chat.Adapters.MessageAdapter;
import com.alan.chat.Model.Chat;
import com.alan.chat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    private ImageView porfilePic;
    private TextView uname;
    private RecyclerView recyclerView;
    private EditText message;
    private ImageButton sendBut;
    private FirebaseUser fuser;
    private MessageAdapter adapter;
    private ArrayList<Chat> mChats;
    private DatabaseReference reference, databaseReference1, ref;
    private String uId;
    private Intent intent;
    private ValueEventListener seenEventListener;
    private Boolean b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        porfilePic = (ImageView) findViewById(R.id.chatPicId);
        uname = (TextView) findViewById(R.id.chatNameId);
        message = (EditText) findViewById(R.id.messageTextId);
        sendBut = (ImageButton) findViewById(R.id.sendId);

        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerId);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        intent = getIntent();
        uId = intent.getStringExtra("UserId");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        sendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(),uId,msg);
                }
                message.setText("");
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                uname.setText(user.getUsername());

                readMessage(fuser.getUid(),uId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(uId);
    }

    private void seenMessage(final String userId){
        ref = FirebaseDatabase.getInstance().getReference("Chats");
        seenEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen","true");
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addValueEventListener(seenEventListener);
    }

    private void sendMessage(String sender,String receiver, String msg){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",msg);
        hashMap.put("isSeen","false");
        hashMap.put("timestamp",ServerValue.TIMESTAMP);
        ref.push().setValue(hashMap);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid()).child(uId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    databaseReference.child("id").setValue(uId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readMessage(final String myId, final String userId){
        mChats = new ArrayList<Chat>();
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        mChats.add(chat);
                    }
                }
                adapter = new MessageAdapter(MessageActivity.this,mChats);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(Boolean online){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online",online);
        reference1.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        b = true;
        status(true);
        if(seenEventListener == null){
            seenMessage(uId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(false);
        b = false;
        if(seenEventListener!=null){
            ref.removeEventListener(seenEventListener);
            seenEventListener = null;
        }
    }
}
