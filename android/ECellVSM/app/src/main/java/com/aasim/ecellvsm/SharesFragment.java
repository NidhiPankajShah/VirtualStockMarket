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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aasim.ecellvsm.Models.Completed;
import com.aasim.ecellvsm.Models.CompletedResponse;
import com.aasim.ecellvsm.Models.PendingResponse;
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


public class SharesFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private RecyclerView shareslist ;
    private RecyclerView.Adapter shareslistAdapter;
    private RecyclerView.LayoutManager shareslistlayoutmanager;
    private View rootView;
    private List<sharesListItem> shareslistarr = new ArrayList<>();
    private SwipeRefreshLayout sharesfragrefresh;
    public static final String KEY_USERID = "id";
    private  Snackbar altertsnack;
    private TextView balancetxt,networthshares;
    private boolean recyclerefresh;
    public SharesFragment() {
        // Required empty public constructor
    }

    public static SharesFragment newInstance(String param1, String param2) {
        SharesFragment fragment = new SharesFragment();
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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_shares, container, false);
        shareslist = (RecyclerView) rootView.findViewById(R.id.share_recycler_home);
        shareslist.setHasFixedSize(true);
        shareslistlayoutmanager = new LinearLayoutManager(getActivity());
        shareslist.setLayoutManager(shareslistlayoutmanager);
        shareslistAdapter = new SharesListAdapter(shareslistarr);
        shareslist.setAdapter(shareslistAdapter);
        balancetxt = (TextView)rootView.findViewById(R.id.balancetxtshares);
        networthshares = (TextView)rootView.findViewById(R.id.networthtxtshares);
        shareslist.addOnItemTouchListener(new ListTouchListener(getActivity(), shareslist, new ListTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                sharesListItem company = shareslistarr.get(position);

                Toast.makeText(getActivity(), company.getShortname() + " is selected!", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).openCompanyFrag(company.getShortname(),2);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        sharesfragrefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.shareFragmentRefresh);
        sharesfragrefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fetchusershares();
                    }
                }
        );


        altertsnack = Snackbar.make(getActivity().findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        shareslist.setOnTouchListener(
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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle("Portfolio");
        fetchusershares();
    }

    private void fetchusershares() {
        recyclerefresh = true;
        shareslist.setVisibility(View.GONE);
        SharedPreferences sp=getActivity().getSharedPreferences("key", Context.MODE_PRIVATE);
        final String userid = sp.getString(Configurations.KEY_USERID,"Not Found");
        shareslistarr.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"completed",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        shareslistarr.clear();
                        Gson gson = new GsonBuilder().create();
                        CompletedResponse res = gson.fromJson(response,CompletedResponse.class);
                        DecimalFormat f1 = new DecimalFormat("##,##,##,###.00");
                        balancetxt.setText("Rs."+ f1.format(BigDecimal.valueOf(res.getUser().getBalance())));
                        networthshares.setText("Rs."+f1.format(BigDecimal.valueOf(res.getUser().getNetworth())));
                        DecimalFormat f = new DecimalFormat("##.00");
                        if(res.getCompleted().size() > 0){

                            for(int i=0;i<res.getCompleted().size();i++){
                                Completed comp = res.getCompleted().get(i);
                                float newprice = 0 ;
                                if(comp.getQuantity() > 0){
                                    sharesListItem company;
                                    for(int j=0;j<res.getCompanies().size();j++){
                                        if(res.getCompanies().get(j).getShortname().equals(comp.getName())){
                                            newprice = res.getCompanies().get(j).getRate();
                                        }
                                    }
                                    Log.e("newprice",newprice+"");
                                    float changer =( (newprice - comp.getPrice())/comp.getPrice())*100;
                                    double changerd =Math.round(changer * 100.0) / 100.0;

                                    float total = newprice * comp.getQuantity();
                                    company = null;
                                    if(changerd>0)
                                    company = new sharesListItem(comp.getLongname(),comp.getName(), "Rs."+f.format(newprice), f.format(changerd),comp.getQuantity()+"x","Rs."+f.format(total));
                                    else
                                    if(changerd<0)
                                        company = new sharesListItem(comp.getLongname(),comp.getName(), "Rs."+f.format(newprice), f.format(changerd),comp.getQuantity()+"x","Rs."+f.format(total));
                                    else if(changerd == 0)
                                        company = new sharesListItem(comp.getLongname(),comp.getName(), "Rs."+f.format(newprice),"0.00",comp.getQuantity()+"x","Rs."+f.format(total));
                                    shareslistarr.add(company);
                                }

                            }
                            shareslist.getRecycledViewPool().clear();

                            shareslistAdapter.notifyDataSetChanged();
                            sharesfragrefresh.setRefreshing(false);
                        }
                        else {
                        }
                        shareslist.getRecycledViewPool().clear();
                        shareslistAdapter.notifyDataSetChanged();
                        sharesfragrefresh.setRefreshing(false);

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
                        altertsnack.setAction("",new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }});
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
        shareslistarr.clear();
        shareslist.setVisibility(View.VISIBLE);
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
