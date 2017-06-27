

package com.diplomatiki.krikonis.rangefinder.app.app;

/**
 * Created by Arvitis on 22/6/2017.
 * initiate all the volley core objects
 */

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
/*
    public static final String TAG = AppController.class.getSimpleName();

   // private RequestQueue mRequestQueue;

    private static AppController mInstance;

    RequestQueue RequestQueue = Volley.newRequestQueue(getApplicationContext());
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
/*
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
*/
    /*
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    private addToRequestQueue(StringRequest req) {
        //RequestQueue.setTag(TAG);
       // getRequestQueue().add(req);
        RequestQueue.add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    */
}
