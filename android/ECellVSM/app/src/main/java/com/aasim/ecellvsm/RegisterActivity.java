package com.aasim.ecellvsm;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContentResolverCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aasim.ecellvsm.Models.RegisterResponse;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button loginbutton ;
    Button registerbutton;
    EditText usernametxt;
    EditText passwordtxt;
    EditText confirmpasstxt;
    Snackbar altertsnack;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginbutton = (Button) findViewById(R.id.loginactbtn);
        registerbutton = (Button) findViewById(R.id.registerbtn);
        usernametxt = (EditText) findViewById(R.id.usernameregtxt);
        passwordtxt = (EditText) findViewById(R.id.passwordregtxt);
        confirmpasstxt = (EditText) findViewById(R.id.confirmpassregtxt);

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registeract = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(registeract);
            }
        });


//Snackbar Config
        altertsnack = Snackbar.make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
    }



    public boolean validatefields(){
        final String username = usernametxt.getText().toString().trim();
        final String password = passwordtxt.getText().toString().trim();
        final String confirmpass = confirmpasstxt.getText().toString().trim();
        if(password.length() == 0 || username.length() == 0){
            altertsnack.setText("Fields cannot be empty");
            altertsnack.show();
            return false;
        }

        if(!password.equals(confirmpass)){
            altertsnack.setText("Passwords do not match");
            altertsnack.show();
            return false;
        }

        return true;
    }

    public void register(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);


        final String username = usernametxt.getText().toString().trim();
        final String password = passwordtxt.getText().toString().trim();
        final String confirmpass = confirmpasstxt.getText().toString().trim();

        if(validatefields()){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"register",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new GsonBuilder().create();
                            RegisterResponse res = gson.fromJson(response,RegisterResponse.class);
                            if(res.getSuccess() == 1){
                                altertsnack.setText("Registration Successful Activate Account By Contacting Event Manager");
                                altertsnack.setDuration(Snackbar.LENGTH_LONG);
                                altertsnack.show();
                                altertsnack.setCallback(new Snackbar.Callback() {

                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        Intent loginact = new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(loginact);
                                    }

                                    @Override
                                    public void onShown(Snackbar snackbar) {
                                    }
                                });

                            }
                            else {
                                if(res.getError() == 1){
                                    altertsnack.setText("Some internal server error try again after some time");
                                    altertsnack.show();
                                }
                                if(res.getError() == 2){
                                    altertsnack.setText("Username already present try different name");
                                    altertsnack.show();
                                }
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
                            altertsnack.setText(message);
                            altertsnack.show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(KEY_USERNAME,username);
                    params.put(KEY_PASSWORD,password);
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        }

        /*

        */

    }
}
