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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aasim.ecellvsm.Models.LoginResponse;
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

public class LoginActivity extends AppCompatActivity {

    Button loginbutton ;
    Button registerbutton;
    EditText usernametxt;
    EditText passwordtxt;
    Snackbar altertsnack;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
        int value = sp.getInt("loggedflag", 0);

        if(value == 1)
        {
            Intent mainAct = new Intent(this,MainActivity.class);
            mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mainAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(mainAct);
        }
        setContentView(R.layout.activity_login);


        loginbutton = (Button) findViewById(R.id.loginbtn);
        registerbutton = (Button) findViewById(R.id.registeractbtn);
        usernametxt = (EditText) findViewById(R.id.usernameText);
        passwordtxt = (EditText) findViewById(R.id.passwordtext);

        loginbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                login();
            }
        });
        registerbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent mainAct = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(mainAct);
            }
        });


        altertsnack = Snackbar.make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

    }


    public boolean validatefields(){
        final String username = usernametxt.getText().toString().trim();
        final String password = passwordtxt.getText().toString().trim();
        if(password.length() == 0 || username.length() == 0){
            altertsnack.setText("Fields cannot be empty");
            altertsnack.show();
            return false;
        }
        return true;
    }
    public void login(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

//        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);


        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
        final String username = usernametxt.getText().toString().trim();
        final String password = passwordtxt.getText().toString().trim();
        if(validatefields()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"login",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Login Response",response);
                            Gson gson = new GsonBuilder().create();
                            LoginResponse res = gson.fromJson(response,LoginResponse.class);
                            if(res.getSuccess() == 1){

                                SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
                                SharedPreferences.Editor ed=sp.edit();
                                ed.putInt(Configurations.KEY_LOGGEDFLAG,1);
                                ed.putString(Configurations.KEY_USERID,res.getUser().getId());
                                ed.putString(Configurations.KEY_USERNAME,res.getUser().getUsername());
                                ed.commit();
                                MyFirebaseInstanceIdService.sendRegistrationToServer(sp,getApplicationContext());
                                Intent mainAct = new Intent(LoginActivity.this,MainActivity.class);
                                mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mainAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mainAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainAct);

                            }
                            else {
                                if(res.getError() == 1){
                                    altertsnack.setText("Check your username and password");
                                    altertsnack.show();
                                }
                                if(res.getError() == 2){
                                    altertsnack.setText("Activate Account By Contacting Event Manager");
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

    }
}
