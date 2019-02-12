package com.aasim.ecellvsm;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aasim.ecellvsm.Models.Company;
import com.aasim.ecellvsm.Models.GetAllCompanyResponse;
import com.aasim.ecellvsm.Models.LeaderBoardResponse;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoardFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private View rootview;
    private RecyclerView leaderlist ;
    private RecyclerView.Adapter leaderlistadapter;
    private RecyclerView.LayoutManager leaderlistlayoutmanager;
    private List<leaderlistitem> leaderlistarr = new ArrayList<>();
    private SwipeRefreshLayout leaderlistrefresh;
    private TextView ranktxt,networthtxt;
    Snackbar altertsnack;
    public static final String KEY_USERID = "id";
    public boolean recyclerefresh;
    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    public static LeaderBoardFragment newInstance(String param1, String param2) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_leader_board, container, false);
        leaderlist = (RecyclerView) rootview.findViewById(R.id.leaderboard_recycler_home);
        leaderlist.setHasFixedSize(true);
        leaderlistlayoutmanager = new LinearLayoutManager(getActivity());
        leaderlist.setLayoutManager(leaderlistlayoutmanager);
        leaderlistadapter = new LeaderListAdapter(leaderlistarr);
        leaderlist.setAdapter(leaderlistadapter);
        ranktxt = (TextView)rootview.findViewById(R.id.ranktxt);
        networthtxt = (TextView)rootview.findViewById(R.id.balancetxtleaderboards);
        altertsnack = Snackbar.make(getActivity().findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        leaderlistrefresh = (SwipeRefreshLayout) rootview.findViewById(R.id.leaderboardfragswipe);
        leaderlistrefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchleaderboard();
                    }
                }
        );

        leaderlist.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (recyclerefresh) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );

        fetchleaderboard();
        return rootview;
    }

    private void fetchleaderboard() {
        recyclerefresh = true;
        leaderlist.setVisibility(View.GONE);
        SharedPreferences sp=getActivity().getSharedPreferences("key", Context.MODE_PRIVATE);
        final String userid = sp.getString(Configurations.KEY_USERID,"Not Found");

        leaderlistrefresh.post(new Runnable() {
            @Override
            public void run() {
                leaderlistrefresh.setRefreshing(true);
            }
        });
        leaderlistarr.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"leaderboard",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        LeaderBoardResponse res = gson.fromJson(response,LeaderBoardResponse.class);
                        DecimalFormat f = new DecimalFormat("##.00");
                        DecimalFormat f1 = new DecimalFormat("##,##,##,###.00");
                        if(res.getSuccess() == 1){
                            leaderlistarr.clear();
                            ranktxt.setText(""+res.getRank());
                            networthtxt.setText("Rs."+f1.format( BigDecimal.valueOf(res.getNetworth())));
                            for(int i=0;i<res.getUser().size();i++){
                                User userres = res.getUser().get(i);
                                String username = userres.getUsername();
                                double networth = userres.getNetworth();
                                double previous = userres.getPrevious();
                                double change = ((networth - previous) /networth)*100;


                                leaderlistitem  company = new leaderlistitem(username,"Rs."+f1.format(BigDecimal.valueOf(networth)),"Rank."+(i+1),f.format(change)+"");
                                leaderlistarr.add(company);
                            }
                            leaderlist.getRecycledViewPool().clear();
                            leaderlistadapter.notifyDataSetChanged();
                            leaderlistrefresh.setRefreshing(false);

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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        leaderlistarr.clear();
        leaderlist.setVisibility(View.VISIBLE);
        recyclerefresh = false;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
