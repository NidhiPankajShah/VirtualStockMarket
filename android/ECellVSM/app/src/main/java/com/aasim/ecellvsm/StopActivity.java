package com.aasim.ecellvsm;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aasim.ecellvsm.Models.BeginResponse;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StopActivity extends AppCompatActivity {
    private Button refresh;
    private Snackbar altertsnack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        altertsnack = Snackbar.make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        refresh = (Button)findViewById(R.id.refreshbutton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkeventstat();
            }
        });

    }
    void checkeventstat(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configurations.baseurl+"begin",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Login Response",response);
                        Gson gson = new GsonBuilder().create();
                        BeginResponse res = gson.fromJson(response,BeginResponse.class);
                        Log.e("Response",Integer.toString(res.getBegin()));
                        if(res.getBegin() == 1) {
                            Intent mainAct = new Intent(StopActivity.this,MainActivity.class);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainAct);
                        }
                        else {

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
