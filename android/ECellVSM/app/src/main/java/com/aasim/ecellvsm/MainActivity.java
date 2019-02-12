package com.aasim.ecellvsm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.aasim.ecellvsm.Models.BeginResponse;
import com.aasim.ecellvsm.Models.GetUserResponse;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,CompanyFragment.OnFragmentInteractionListener {
    private Boolean exit = false;
    private Boolean csecondary = false;
    private Boolean ssecondary = false;
    private Boolean logout = false;
    private TextView usernametxt,balancetxt,nettxt;
    public static final String KEY_USERID = "id";
    Snackbar altertsnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ManageFragment myFragment = (ManageFragment) getSupportFragmentManager().findFragmentByTag("pending");
                if (myFragment != null && myFragment.isVisible()) {
                    // add your code here
                    Log.e("GOT IT","GOT MANGAE");
                    myFragment.dismisssnacks();
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = MarketFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainframe, fragment).commit();

        setTitle("Market Rates");

        View headerView = navigationView.getHeaderView(0);
        usernametxt = (TextView)headerView.findViewById(R.id.usernametxtnav);
        balancetxt = (TextView)headerView.findViewById(R.id.balancetxtnav);
        nettxt = (TextView)headerView.findViewById(R.id.nettxtnav);


        altertsnack = Snackbar.make(findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        initnavheader();


        Intent i = getIntent();
        String open =   i.getStringExtra("open");
        if (open != null && open.equals("pending")) {
            fragmentClass = ManageFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager openfragmentManager = getSupportFragmentManager();
            openfragmentManager.beginTransaction().replace(R.id.mainframe, fragment,"pending").commit();
            setTitle("Pending Transactions");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEventRunning();
        initnavheader();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(csecondary){
                Class fragmentClass = MarketFragment.class;
                Fragment fragment = null;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainframe, fragment).addToBackStack(null);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                csecondary = false;
                return;
            }
            if(ssecondary){
                Class fragmentClass = SharesFragment.class;
                Fragment fragment = null;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainframe, fragment).addToBackStack(null);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                ssecondary = false;
                return;
            }
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        String tag ="";
        ssecondary = false;
        csecondary = false;
        if (id == R.id.nav_market) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            fragmentClass = MarketFragment.class;
            tag = "market";

        } else if (id == R.id.nav_leaderboards) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            fragmentClass = LeaderBoardFragment.class;
            tag = "leader";

        } else if (id == R.id.nav_shares) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            fragmentClass = SharesFragment.class;
            tag= "shares";

        }
        else if (id == R.id.nav_pending){
            DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            fragmentClass = ManageFragment.class;
            tag = "pending";
        }
        else if (id == R.id.nav_logout){

            if (logout) {
                SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed=sp.edit();
                ed.putInt("loggedflag",0);
                ed.commit();
                Intent loginAct = new Intent(this,LoginActivity.class);
                loginAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                loginAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                loginAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(loginAct);

            } else {
                Toast.makeText(this, "Press Logout again to Logout.",
                        Toast.LENGTH_SHORT).show();
                logout = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        logout = false;
                    }
                }, 3 * 1000);

            }
            return true;

        }



        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }



        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainframe, fragment,tag).commit();

        setTitle(item.getTitle());
        return true;
    }

    public void openCompanyFrag(String name,int parent){
        if(parent==1)
            csecondary = true;
        else
            ssecondary = true;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString("company",name);
        args.putInt("parent",parent);
        Class fragmentClass = CompanyFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainframe, fragment).addToBackStack(null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        setTitle(name);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void initnavheader(){
        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
        String username = sp.getString(Configurations.KEY_USERNAME,"Not Found");
        usernametxt.setText(username);
        final String userid = sp.getString(Configurations.KEY_USERID,"Not Found");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"getuser",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        GetUserResponse res = gson.fromJson(response,GetUserResponse.class);
                        if(res.getSuccess() == 1){
                            if(res.getUser() == null){
                                SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
                                SharedPreferences.Editor ed=sp.edit();
                                ed.putInt(Configurations.KEY_LOGGEDFLAG,0);
                                ed.commit();
                                Intent mainAct = new Intent(MainActivity.this,LoginActivity.class);
                                mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mainAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mainAct.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainAct);
                            }
                            DecimalFormat f1 = new DecimalFormat("##,##,##,###.00");
                            Log.e("CHECK",res.getUser().getUsername());
                            nettxt.setText("Networth: Rs."+f1.format( BigDecimal.valueOf(res.getUser().getNetworth())));
                            balancetxt.setText("Balance: Rs."+f1.format(BigDecimal.valueOf(res.getUser().getBalance())));
                        }
                        else {
                            if(res.getError() == 1){
                                altertsnack.setText("Some internal server error try again after some time");
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
                params.put(KEY_USERID,userid);
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
                            Intent mainAct = new Intent(MainActivity.this,StopActivity.class);
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
