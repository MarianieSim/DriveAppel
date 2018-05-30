package com.example.simeonm.da;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView Welcome;
    private ImageView picID;

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mAuth = FirebaseAuth.getInstance();
        Button GPS = (Button)findViewById(R.id.GPS);
        Welcome = (TextView)findViewById(R.id.Welcome);
        picID = (ImageView)findViewById(R.id.pic);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, MapsActivity.class));
            }
        });

        GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            Welcome.setText("  Welcome, " + name);
            Uri photoUrl = user.getPhotoUrl();
            picID.setImageURI(photoUrl);

        }

        Button logout = (Button)findViewById(R.id.Signout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

            }
        });
    }

}
