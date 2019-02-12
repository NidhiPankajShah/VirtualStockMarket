package com.aasim.ecellvsm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aasim.ecellvsm.Models.FCMresponse;
import com.aasim.ecellvsm.Models.GetUserResponse;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aasim on 31/12/16.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Checking", "Refreshed token: " + refreshedToken);
        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(sp,this);
    }




    public static void sendRegistrationToServer(SharedPreferences sp,Context context ){
        int value = sp.getInt(Configurations.KEY_LOGGEDFLAG, 0);
        Log.e("FIREBASE","Method Called");
        Log.e("FIREBASE",""+value);
        if(value == 0){

        }
        else{
            final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            final String userid = sp.getString(Configurations.KEY_USERID,"");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"addfcm",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new GsonBuilder().create();
                            FCMresponse res = gson.fromJson(response,FCMresponse.class);
                            Log.e("FIREBASE",""+res.getSuccess());

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String message = "No error";
                            if (error instanceof NetworkError) {
                                message = "Please check your internet connection!";
                            } else if (error instanceof ServerError) {
                                message = "The server could not be found. Please try again after some time!!";
                            } else if (error instanceof AuthFailureError) {
                                message = "Please check your internet connection!";
                            } else if (error instanceof ParseError) {
                                message = "Parsing error! Please try again after some time!!";
                            } else if (error instanceof NoConnectionError) {
                                message = "Please check your internet connection!";
                            } else if (error instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            }
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("id",userid);
                    params.put("token",refreshedToken);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }
    }



}


