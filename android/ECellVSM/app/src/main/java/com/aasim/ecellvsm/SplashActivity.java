package com.aasim.ecellvsm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.aasim.ecellvsm.Models.BeginResponse;
import com.aasim.ecellvsm.Models.LoginResponse;
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
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    Snackbar altertsnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers(), new Crashlytics());
        setContentView(R.layout.activity_splash);

        altertsnack = Snackbar.make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configurations.baseurl+"begin",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Login Response",response);
                        Gson gson = new GsonBuilder().create();
                        BeginResponse res = gson.fromJson(response,BeginResponse.class);
                        Log.e("Response",Integer.toString(res.getBegin()));
                        if(res.getBegin() == 1) {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    // This method will be executed once the timer is over
                                    // Start your app main activity
                                    SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
                                    int value = sp.getInt(Configurations.KEY_LOGGEDFLAG, 0);

                                    if(value == 1)
                                    {
                                        Intent mainAct = new Intent(SplashActivity.this,MainActivity.class);
                                        mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mainAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        mainAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(mainAct);
                                    }
                                    else {
                                        Intent LoginAct = new Intent(SplashActivity.this,LoginActivity.class);
                                        LoginAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        LoginAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        LoginAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(LoginAct);


                                    }

                                    // close this activity
                                    finish();
                                }
                            }, SPLASH_TIME_OUT);



                        }
                        else {
                            Intent mainAct = new Intent(SplashActivity.this,StopActivity.class);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainAct);
                        }
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
                        Log.e("Error",message);
                        altertsnack.setText(message);
                        altertsnack.show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}

