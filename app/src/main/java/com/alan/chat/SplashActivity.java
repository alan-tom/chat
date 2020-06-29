package com.alan.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static com.alan.chat.R.anim.fade_in;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 2500;
    private TextView textView;
    private Animation fadeIn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "SPLASH_ACTIVITY";
    private Class aClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        textView = (TextView) findViewById(R.id.splashText);

        mAuth = FirebaseAuth.getInstance();


        fadeIn = AnimationUtils.loadAnimation(this,fade_in);
        textView.setAnimation(fadeIn);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG,"User logged in ");
                    aClass = DashboardActivity.class;
                }else{
                    Log.d(TAG,"User not logged in ");
                    aClass = LoginActivity.class;
                }
            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,aClass);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
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
