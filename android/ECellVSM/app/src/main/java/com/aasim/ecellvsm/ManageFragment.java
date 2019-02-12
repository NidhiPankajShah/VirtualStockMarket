package com.aasim.ecellvsm;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aasim.ecellvsm.Models.Company;
import com.aasim.ecellvsm.Models.GetAllCompanyResponse;
import com.aasim.ecellvsm.Models.Pending;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ManageFragment extends Fragment {

    private RecyclerView managelist ;
    private RecyclerView.Adapter managelistadapter;
    private RecyclerView.LayoutManager managelistlayoutmanager;
    private View rootView;
    private List<ManageListItem> managelistarr = new ArrayList<>();
    private SwipeRefreshLayout managefragrefresh;
    private Snackbar altertsnack;
    public static final String KEY_USERID = "id";
    private boolean recyclerefresh,undoflag;
    private ArrayList<Snackbar> mysnacks = new ArrayList<Snackbar>(100);
    private OnFragmentInteractionListener mListener;

    public ManageFragment() {
    }
    public static ManageFragment newInstance(String param1) {
        ManageFragment fragment = new ManageFragment();
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
        rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        managelist = (RecyclerView) rootView.findViewById(R.id.pending_recycler_home);
        managelist.setHasFixedSize(true);
        managelistlayoutmanager = new LinearLayoutManager(getActivity());
        managelist.setLayoutManager(managelistlayoutmanager);
        managelistadapter = new ManageListAdapter(managelistarr);
        managelist.setAdapter(managelistadapter);
        managelist.addOnItemTouchListener(new ListTouchListener(getActivity(), managelist, new ListTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final ManageListItem trans = managelistarr.get(position);
                final String id = trans.getId();
                Snackbar altertsnack =  Snackbar.make(getActivity().findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);;
                altertsnack.setText("Transaction Cancelled");
                altertsnack.setAction("UNDO",new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoflag = true ;
                        managelistarr.add(position,trans);
                        managelistadapter.notifyDataSetChanged();
                    }});
                altertsnack.setCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if(undoflag == false)
                        canceltrans(id);
                        else
                            undoflag = false;
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                    }
                });
                altertsnack.setActionTextColor(Color.WHITE);

                altertsnack.show();
                managelistarr.remove(position);
                managelistadapter.notifyDataSetChanged();
                mysnacks.add(altertsnack);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        managefragrefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.pendingFragmentRefresh);
        managefragrefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getpending();
                    }
                }
        );

        altertsnack = Snackbar.make(getActivity().findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        managelist.setOnTouchListener(
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
        getpending();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(int i =0 ; i < mysnacks.size() ; i++)
            mysnacks.get(i).dismiss();
    }

    private void getpending() {
        recyclerefresh = true;
        managelist.setVisibility(View.GONE);
        SharedPreferences sp=getActivity().getSharedPreferences("key", Context.MODE_PRIVATE);
        final String userid = sp.getString(Configurations.KEY_USERID,"Not Found");
            managefragrefresh.post(new Runnable() {
                @Override
                public void run() {
                    managefragrefresh.setRefreshing(true);
                }
            });
            managelistarr.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"pending",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new GsonBuilder().create();
                            PendingResponse res = gson.fromJson(response,PendingResponse.class);
                            if(res.getSuccess() == 1){
                                managelistarr.clear();
                                for(int i=0;i<res.getPending().size();i++){
                                    Pending trans = res.getPending().get(i);
                                    String name = trans.getLongname();
                                    String shortname = trans.getName();
                                    int type = trans.getType();
                                    int quantity = trans.getQuantity();
                                    ManageListItem  company = new ManageListItem(name,shortname,"Stocks: "+quantity,type+"",res.getPending().get(i).get_id());
                                    managelistarr.add(company);
                                }
                                managelist.getRecycledViewPool().clear();

                                managelistadapter.notifyDataSetChanged();
                                managefragrefresh.setRefreshing(false);
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
            managelistarr.clear();
            managelist.setVisibility(View.VISIBLE);
        recyclerefresh = false;
    }


    public  void  canceltrans(String id){
        final String transid = id;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"cancelpend",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        PendingResponse res = gson.fromJson(response,PendingResponse.class);
                        if(res.getSuccess() == 1){

                            }
                        else {
                            String message = "Some Server Error Has Occured Try Again After Refreshing Page";
                            altertsnack.setText(message);
                            altertsnack.show();
                            altertsnack.setAction("",new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }});
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
                        altertsnack.setAction("",new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }});
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_USERID,transid);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        managelistarr.clear();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void dismisssnacks(){
        for(int i =0 ; i < mysnacks.size() ; i++)
            mysnacks.get(i).dismiss();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
