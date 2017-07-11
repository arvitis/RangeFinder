package com.diplomatiki.krikonis.rangefinder.app.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.diplomatiki.krikonis.rangefinder.R;

public class ProManagerActivity extends AppCompatActivity {
    private Button btnLogout;
    private SQLiteHandler db;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_manager);
        btnLogout = (Button) findViewById(R.id.btnLogoutPro);
        getSupportActionBar().setTitle("Pro Manager Activity");
// SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }







        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }
    private void logoutUser() {
        session.setLogin(false,"");

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(ProManagerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
