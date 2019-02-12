package com.aasim.ecellvsm;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aasim.ecellvsm.Models.Company;
import com.aasim.ecellvsm.Models.GetAllCompanyResponse;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MarketFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MarketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private RecyclerView marketlist ;
    private RecyclerView.Adapter marketlistadapter;
    private RecyclerView.LayoutManager marketlistlayoutmanager;
    private View rootView;
    private List<MarketListItem> marketlistarr = new ArrayList<>();
    private SwipeRefreshLayout marketfragrefresh;
    Snackbar altertsnack;
    public boolean recyclerefresh;

    public MarketFragment() {
        // Required empty public constructor
    }

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
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
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle("Market Rates");
        fetchmarketrates();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_market, container, false);
        marketlist = (RecyclerView) rootView.findViewById(R.id.market_recycler_home);
        marketlist.setHasFixedSize(true);
        marketlistlayoutmanager = new LinearLayoutManager(getActivity());
        marketlist.setLayoutManager(marketlistlayoutmanager);
        marketlistadapter = new MarketListAdapter(marketlistarr);
        marketlist.setAdapter(marketlistadapter);
        marketlist.addOnItemTouchListener(new ListTouchListener(getActivity(), marketlist, new ListTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MarketListItem company = marketlistarr.get(position);
                ((MainActivity)getActivity()).openCompanyFrag(company.getShortname(),1);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        marketlist.setOnTouchListener(
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



        altertsnack = Snackbar.make(getActivity().findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        marketfragrefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.marketFragmentRefresh);
        marketfragrefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchmarketrates();
                    }
                }
        );

        fetchmarketrates();
        return rootView;
    }


    private void fetchmarketrates() {
        recyclerefresh = true;
        marketlist.setVisibility(View.GONE);
        marketfragrefresh.post(new Runnable() {
            @Override
            public void run() {
                marketfragrefresh.setRefreshing(true);
            }
        });
        marketlistarr.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"getallcompany",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        GetAllCompanyResponse res = gson.fromJson(response,GetAllCompanyResponse.class);
                        if(res.getSuccess() == 1){
                            marketlistarr.clear();
                            for(int i=0;i<res.getCompany().size();i++){
                                Company companyres = res.getCompany().get(i);
                                String name = companyres.getName();
                                String shortname = companyres.getShortname();
                                float rate = companyres.getRate();
                                float previous = companyres.getPrevious();
                                float change = ((rate-previous)/previous)*100;
                                double d = 2.34568;


                                DecimalFormat f1 = new DecimalFormat("##,##,##,###.00");

                                MarketListItem  company = new MarketListItem(name,shortname,"Rs."+f1.format(rate),change+"");
                                marketlistarr.add(company);
                            }
                            marketlist.getRecycledViewPool().clear();
                            marketlistadapter.notifyDataSetChanged();
                            marketfragrefresh.setRefreshing(false);

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
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        marketlistarr.clear();
        marketlist.setVisibility(View.VISIBLE);
        recyclerefresh = false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
