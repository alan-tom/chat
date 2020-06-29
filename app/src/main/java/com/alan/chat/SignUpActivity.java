package com.alan.chat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.HashMap;

import static android.widget.Toast.LENGTH_SHORT;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout fnameL, lnameL,usernameL, emailL, passL, repassL, numL;
    private EditText fname, lname,username, email, pass, repass, num;
    private Button signUp;
    private TextView dateError;
    private DatePicker datePicker;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        fnameL = (TextInputLayout) findViewById(R.id.SignUpFirstNameLayout);
        lnameL = (TextInputLayout) findViewById(R.id.SignUpLastNameLayout);
        emailL = (TextInputLayout) findViewById(R.id.SignUpEmailLayout);
        usernameL = (TextInputLayout) findViewById(R.id.SignUpUsernameLayout);
        passL = (TextInputLayout) findViewById(R.id.SignUpPasswordLayout);
        repassL = (TextInputLayout) findViewById(R.id.SignUpRePasswordLayout);
        fname = (EditText) findViewById(R.id.SignUpFirstNameId);
        lname = (EditText) findViewById(R.id.SignUpLastNameId);
        email = (EditText) findViewById(R.id.SignUpEmailId);
        username = (EditText) findViewById(R.id.SignUpUsernameId);
        pass = (EditText) findViewById(R.id.SignUpPasswordId);
        repass = (EditText) findViewById(R.id.SignUpRePasswordId);
        signUp = (Button) findViewById(R.id.signUpButId);
        dateError = (TextView) findViewById(R.id.dateError);
        datePicker = (DatePicker) findViewById(R.id.SignUpDatePicker);
        datePicker.setMaxDate(new Date().getTime());

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean b = checkfilled();
                if(!b) {
                    final String mName = fname.getText().toString().trim() + " " + lname.getText().toString().trim();
                    final String mEmail = email.getText().toString().trim();
                    final String mUsername = username.getText().toString().trim();
                    String mPass = pass.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if (authResult != null){
                                String userID = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDb = mReference.child(userID);
                                HashMap<String,Object> hashMap = new HashMap<String,Object>();
                                hashMap.put("id",userID);
                                hashMap.put("name",mName);
                                hashMap.put("email",mEmail);
                                hashMap.put("username",mUsername);
                                hashMap.put("status","Let's chat");
                                hashMap.put("online",false);
                                hashMap.put("imageUrl","none");
                                hashMap.put("search",mUsername.toLowerCase());
                                currentUserDb.setValue(hashMap).addOnCompleteListener(SignUpActivity.this,new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(SignUpActivity.this,"Failed in signing in", LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public boolean checkfilled(){
        Boolean flag = Boolean.FALSE;
        if(fname.getText().toString().equals("")){
            flag = Boolean.TRUE;
            fnameL.setError("Must be filled");
        }else if (fname.getText().toString().contains(" ")){
            flag = Boolean.TRUE;
            fnameL.setError("Spaces not allowed");
        }
        if (username.getText().toString().length() < 4 || username.getText().toString().length() > 20){
            flag = Boolean.TRUE;
            usernameL.setError("4-20 characters required");
        }else if (username.getText().toString().contains(" ")) {
            flag = Boolean.TRUE;
            usernameL.setError("spaces not allowed");
        }
            if (email.getText().toString().equals("")) {
            flag = Boolean.TRUE;
            emailL.setError("Must be filled");
        }else if (email.getText().toString().split("@").length != 2){
            flag = Boolean.TRUE;
            emailL.setError("please correct format");
        }else if (email.getText().toString().contains(" ")){
            flag = Boolean.TRUE;
            emailL.setError("spaces not allowed");
        }
        if(pass.getText().toString().length() < 4 || pass.getText().toString().length() > 20){
            flag = Boolean.TRUE;
            passL.setError("4-20 characters required");
        }else if (pass.getText().toString().contains(" ")){
            flag = Boolean.TRUE;
            passL.setError("spaces not allowed");
        }else if (!repass.getText().toString().equals(pass.getText().toString())){
            flag = Boolean.TRUE;
            repassL.setError("should match new password");
        }
        return flag;
    }
}
