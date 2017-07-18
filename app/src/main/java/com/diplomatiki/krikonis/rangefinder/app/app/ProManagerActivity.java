package com.diplomatiki.krikonis.rangefinder.app.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.diplomatiki.krikonis.rangefinder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProManagerActivity extends AppCompatActivity {
    private Button btnLogout;
    private Button btnupdateratings;
    private ListView RatingList;
    private TextView txtrating;
    private SQLiteHandler db;
    private SessionManager session;
    String uid;
    int overallrating;
    Float decratings;
    private static final String TAG = MapsActivity.class.getSimpleName();

    ArrayList<String> listitems =new ArrayList<String>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_manager);
        btnLogout = (Button) findViewById(R.id.btnprologout);
        btnupdateratings = (Button) findViewById(R.id.btnupdateratings);
        RatingList = (ListView) findViewById(R.id.ratinglist);
        txtrating = (TextView) findViewById(R.id.txtrating);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listitems);
        getSupportActionBar().setTitle("Pro Manager Activity");
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Update button click event
        btnupdateratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getratings();
            }
        });
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
        Intent intent = new Intent(ProManagerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void getratings() {
        uid = session.getuid();
        if (!TextUtils.isEmpty(uid)) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("arvitis.ddns.net:62222")
                    .appendPath("android_login_api")
                    .appendPath("getratings.php")
                    .appendQueryParameter("uid", uid);
            String URL_UPDATE = builder.build().toString();
            JsonObjectRequest strReq = new JsonObjectRequest(
                    Request.Method.POST,
                    URL_UPDATE,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Get users Ratings: " + response.toString());
                            try {
                                final JSONArray ratings = response.getJSONArray("ratings");
                                overallrating = 0;
                                for (int i = 0; i < ratings.length(); i++) {
                                    JSONObject rating = ratings.getJSONObject(i);
                                    overallrating += Integer.parseInt(rating.getString("Rating"));
                                    listitems.add("User: " + rating.getString("name") + "\n" +"rated: " + rating.getString("Rating") +"\n" + "on: "+ rating.getString("date") );
                                }
                                RatingList.setAdapter(adapter);
                                decratings = ((float)overallrating)/ratings.length();
                                String str = String.format("%2.01f", decratings);
                                txtrating.setText("Your overall rating is : " + str );
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Update Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) ;
            VolleyController.getInstance(getApplicationContext()).addToRequestQueue(strReq);
        }
        else {
            Toast.makeText(getApplicationContext(), "Your account details have not been stored.Try logging in again!", Toast.LENGTH_LONG).show();
        }
    }
}
