package com.alan.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout userWrap, passwrap;
    private EditText username, password;
    private Button login;
    private TextView signup;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uText, pText;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        userWrap = (TextInputLayout) findViewById(R.id.inputUserId);
        passwrap = (TextInputLayout) findViewById(R.id.passwrapId);
        username = (EditText) findViewById(R.id.usernameTextId);
        password = (EditText) findViewById(R.id.passTextId);
        login = (Button) findViewById(R.id.loginId);
        signup = (TextView) findViewById(R.id.signUpId);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG,"User logged in ");
                }else{
                    Log.d(TAG,"User not logged in ");
                }
            }
        };


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userWrap.setErrorEnabled(false);
                passwrap.setErrorEnabled(false);
                int flag = 0;
                uText = username.getText().toString();
                pText = password.getText().toString();
                String error1, error2;
                if (pText.length() < 4 || pText.length() > 20) {
                    error2 = "Password should be 4-20 characters";
                    flag = 1;
                    passwrap.setError(error2);
                }

                if (flag == 0) {
                    mAuth.signInWithEmailAndPassword(uText,pText).addOnCompleteListener(LoginActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Bundle extra = new Bundle();
                                        extra.putString("Username",uText);
                                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                        intent.putExtras(extra);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(LoginActivity.this,"Failed signing in", LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
