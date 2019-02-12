package com.aasim.ecellvsm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.IntegerRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aasim.ecellvsm.Models.BeginResponse;
import com.aasim.ecellvsm.Models.LeaderBoardResponse;
import com.aasim.ecellvsm.Models.TransactionExecuteResponse;
import com.aasim.ecellvsm.Models.TransactionLoadResponse;
import com.aasim.ecellvsm.Models.User;
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

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class TranscationActivity extends AppCompatActivity {
     int flag;
    String company;
    String longname;
    int quants;
    float cost,stocks;
    DiscreteSeekBar stockquant;
    private RelativeLayout translayout;
    TextView balancetxt,pricetxt,stockstxt,signtext,warningtxt,lefttxt,costtxt;
    Button confirm;
    private Boolean confirmtrans = false;
    private Snackbar altertsnack;
    public static final String KEY_USERID = "id";
    public static final String KEY_SHORTNAME = "shortname";
    public static int globalvalue;
    public float globaltranscost;
    DecimalFormat f1 = new DecimalFormat("##,##,##,###.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            flag = b.getInt("key");
            company = b.getString("company");
        }
        stockquant = (DiscreteSeekBar)findViewById(R.id.stockseek);
        balancetxt = (TextView)findViewById(R.id.transbal);
        costtxt = (TextView)findViewById(R.id.transcost);
        stockstxt = (TextView)findViewById(R.id.stockcount);
        signtext = (TextView)findViewById(R.id.plusminus);
        warningtxt = (TextView)findViewById(R.id.buywarning);
        lefttxt = (TextView)findViewById(R.id.stocksleft);
        confirm = (Button)findViewById(R.id.confirmtrans);
        pricetxt = (TextView) findViewById(R.id.transprice);
        initact();

        pricetxt.setText("Rs."+cost);
        if(flag==2){
            warningtxt.setVisibility(View.GONE);
            signtext.setText("+");
            stockstxt.setText("0");
            costtxt.setText("Rs.0");
            costtxt.setTextColor(Color.GREEN);
            signtext.setTextColor(Color.GREEN);
            stockquant.setMax((int)stocks);
            lefttxt.setVisibility(View.VISIBLE);
            confirm.setText("Sell");
        }

        if(flag==1){
            warningtxt.setVisibility(View.VISIBLE);
            signtext.setText("-");
            stockstxt.setText("0");
            costtxt.setText("Rs.0");
            confirm.setText("Buy");
            costtxt.setTextColor(Color.RED);
            signtext.setTextColor(Color.RED);
            lefttxt.setVisibility(View.GONE);

        }

        altertsnack = Snackbar.make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);


        translayout = (RelativeLayout)findViewById(R.id.translayout);

        inittrans(flag);

        stockquant.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                globalvalue = value ;
               new ServerThread().execute();

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (confirmtrans) {
                    executetransaction();
                } else {
                    Toast.makeText(getApplicationContext(),"Press Button Again To Confirm.",Toast.LENGTH_SHORT).show();
                    confirmtrans = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            confirmtrans = false;
                        }
                    }, 3 * 1000);

                }
            }
        });

        setTitle(company);

    }

    void initact(){
        cost = 100 ;
        stocks=10;
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkEventRunning();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void executetransaction(){

        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
        final String userid = sp.getString(Configurations.KEY_USERID,"Not Found");
        final int type =flag;
        final String long1 = longname;
        final  int quantity = quants;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"transactionexecute",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        TransactionExecuteResponse res = gson.fromJson(response,TransactionExecuteResponse.class);
                        DecimalFormat f = new DecimalFormat("##.00");
                        if(res.getSuccess() == 1){
                            Intent mainAct = new Intent(getApplicationContext(),MainActivity.class);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            mainAct.putExtra("open", "pending");
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
                        altertsnack.setText(message);
                        altertsnack.show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put(KEY_USERID,userid);
                params.put(KEY_SHORTNAME,company);
                params.put("type",""+type);
                params.put("longname",long1);
                params.put("quantity",""+quantity);

                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);









    }

    public void inittrans(final int flag){
        translayout.setVisibility(View.GONE);
        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
        final String userid = sp.getString(Configurations.KEY_USERID,"Not Found");
        final int type = flag;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"transactionload",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        TransactionLoadResponse res = gson.fromJson(response,TransactionLoadResponse.class);
                        DecimalFormat f = new DecimalFormat("##.00");
                        if(res.getSuccess() == 1){


                            if((res.getCompleted() == null  || res.getCompleted().getQuantity() == 0) && flag == 2){
                                altertsnack.setText("Sorry You dont have any stocks in your portfolio to sell");
                                altertsnack.show();
                                confirm.setClickable(false);
                                stocks = 0;
                                stockquant.setMax(0);
                                lefttxt.setText("Stocks Left: 0");
                            }
                            else {
                                Log.e("Response", response);
                                int i;
                                for( i =0 ; i < res.getCompany().size() ; i++){
                                    if(res.getCompany().get(i).getShortname().equals(company))
                                        break;
                                }
                                longname = res.getCompany().get(i).getName();
                                DecimalFormat f1 = new DecimalFormat("##,##,##,###.00");
                                pricetxt.setText("Rs." + f1.format(res.getCompany().get(i).getRate()));
                                balancetxt.setText("Rs." + f1.format(BigDecimal.valueOf(res.getUser().getBalance())));
                                cost = res.getCompany().get(i).getRate();


                                if (type == 1) {
                                    //buy
                                    if(res.getUser().getBalance()<=0){
                                        altertsnack.setText("Sorry! You have no cash to buy  stocks sell some first");
                                        altertsnack.show();
                                        confirm.setClickable(false);
                                        stocks = 0;
                                        stockquant.setMax(0);
                                    }
                                    else {

                                       double total = 0;
                                        for(int j =0 ; j < res.getCompany().size() ; j ++){
                                            for (int k = 0 ; k < res.getPending().size() ; k ++ ){
                                                if(res.getCompany().get(j).getShortname().equals(res.getPending().get(k).getName()))
                                                    total = total + res.getCompany().get(j).getRate() * res.getPending().get(k).getQuantity();
                                            }
                                        }

                                        double max = ((res.getUser().getBalance()-total) / cost);

                                        max = Math.floor(max);

                                        if(max <= 0){
                                            altertsnack.setText("Sorry! You have already bought stocks which will take your entire balance");
                                            altertsnack.show();
                                            confirm.setClickable(false);
                                            stocks = 0;
                                            stockquant.setMax(0);
                                        }
                                        else {
                                            stockquant.setMax((int) max);
                                        }
                                    }


                                }

                                if (type == 2) {
                                    stocks = res.getCompleted().getQuantity();
                                    int tobesold = 0;
                                    int k;
                                    if(res.getPending().size() != 0) {
                                       for (k = 0; k < res.getPending().size(); k++) {
                                            if (res.getPending().get(k).getName().equals(company))
                                                break;
                                        }
                                        if(k!=res.getPending().size())
                                            tobesold = res.getPending().get(k).getQuantity();
                                    }
                                    stockquant.setMax((int) stocks - tobesold);
                                    lefttxt.setText("Stocks Left: " + ((int) stocks - tobesold)+ "");

                                    if(((int) stocks - tobesold) == 0){
                                        altertsnack.setText("Sorry ! you have already put up your all the stocks on sale");
                                        altertsnack.show();
                                        confirm.setClickable(false);
                                        stocks = 0;
                                        stockquant.setMax(0);
                                        lefttxt.setText("Stocks Left: 0");
                                    }
                                }
                            }
                        }
                        translayout.setVisibility(View.VISIBLE);
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

                params.put(KEY_USERID,userid);
                params.put(KEY_SHORTNAME,company);
                params.put("type",String.valueOf(type));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    public void checkEventRunning(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configurations.baseurl+"begin",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Login Response",response);
                        Gson gson = new GsonBuilder().create();
                        BeginResponse res = gson.fromJson(response,BeginResponse.class);
                        Log.e("Response",Integer.toString(res.getBegin()));
                        if(res.getBegin() == 1) {

                        }
                        else {
                            Intent mainAct = new Intent(TranscationActivity.this,StopActivity.class);
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


    public class ServerThread extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute(){
            }
        @Override
        protected String doInBackground(String... arg0) {

            //open socket at specified port on the local host
            try {
                quants = globalvalue;
                globaltranscost  =  cost * globalvalue;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

           return null;
        }

        @Override
        protected void onPostExecute(String text2) {
            // TODO Auto-generated method stub
            super.onPostExecute(text2);
            costtxt.setText("Rs."+f1.format(BigDecimal.valueOf(globaltranscost)));
            lefttxt.setText("Stocks Left :"+(int)(stocks-globalvalue));
        }

    }

}
