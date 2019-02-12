package com.aasim.ecellvsm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aasim.ecellvsm.Models.Company;
import com.aasim.ecellvsm.Models.CompanyDetails;
import com.aasim.ecellvsm.Models.GetAllCompanyResponse;
import com.aasim.ecellvsm.Models.History;
import com.aasim.ecellvsm.Models.TransactionLoadResponse;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompanyFragment extends Fragment {
    private  View rootView;
    private static final String ARG_PARAM1 = "param1";
    private String company;
    private OnFragmentInteractionListener mListener;
    private TextView hourpricetxt,hourchangetxt,lasttranstxt,Lasttranschangetxt,mintxt,maxtxt;
    private Button buy,sell;
    private LineChart comphistory;
    private RelativeLayout main;
    Snackbar altertsnack;


    public CompanyFragment() {
        // Required empty public constructor
    }
    public static CompanyFragment newInstance(String param1) {
        CompanyFragment fragment = new CompanyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
       company=bundle.getString("company");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_company, container, false);
        hourchangetxt = (TextView)rootView.findViewById(R.id.hourchange);
        hourpricetxt = (TextView)rootView.findViewById(R.id.hourprice);
        lasttranstxt = (TextView)rootView.findViewById(R.id.boughtprice);
        mintxt = (TextView)rootView.findViewById(R.id.mingraph);
        maxtxt = (TextView)rootView.findViewById(R.id.maxgraph);
        Lasttranschangetxt = (TextView)rootView.findViewById(R.id.boughtchange);
        buy = (Button)rootView.findViewById(R.id.buybtn);
        sell = (Button)rootView.findViewById(R.id.sellbtn);
        main = (RelativeLayout)rootView.findViewById(R.id.companyfragmain);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transact = new Intent(getActivity(),TranscationActivity.class);
                Bundle b = new Bundle();
                b.putInt("key", 2);
                b.putString("company",company);
                transact.putExtras(b);
                startActivity(transact);

            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transact = new Intent(getActivity(),TranscationActivity.class);
                Bundle b = new Bundle();
                b.putInt("key", 1);
                b.putString("company",company);
                transact.putExtras(b);
                startActivity(transact);

            }
        });
        comphistory = (LineChart)rootView.findViewById(R.id.companychart);




        altertsnack = Snackbar.make(getActivity().findViewById(android.R.id.content), "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = altertsnack.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        initfrag();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setupChart(LineChart chart, LineData data, int color) {

        ((LineDataSet) data.getDataSetByIndex(0)).setCircleColorHole(color);

        // no description text
        chart.getDescription().setEnabled(false);

        // mChart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(false);

        // enable scaling and dragging
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(color);

        // set custom chart offsets (automatic offset calculation is hereby disabled)
       // chart.setViewPortOffsets(10, 10, 10, 10);

        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setSpaceTop(40);
        chart.getAxisLeft().setSpaceBottom(40);
        chart.getAxisRight().setEnabled(false);

        chart.getXAxis().setEnabled(false);



    }

    private LineData getData(int count, float range,ArrayList<History> history) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < history.size(); i++) {
            yVals.add(new Entry(i, history.get(i).getPrice()));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setLineWidth(1.75f);
        set1.setCircleRadius(5f);
        set1.setCircleHoleRadius(2.5f);
        if(history.get(0).getPrice() < history.get(history.size()-1).getPrice()){
            set1.setColor(Color.GREEN);
            set1.setCircleColor(Color.GREEN);
            set1.setFillColor(Color.GREEN);
            set1.setHighLightColor(Color.BLUE);
            set1.setDrawValues(true);
            set1.setValueTextColor(Color.GREEN);
        }

        if(history.get(0).getPrice() > history.get(history.size()-1).getPrice()){
            set1.setColor(Color.RED);
            set1.setCircleColor(Color.RED);
            set1.setFillColor(Color.RED);
            set1.setHighLightColor(Color.BLUE);
            set1.setDrawValues(true);
            set1.setValueTextColor(Color.RED);
        }
        // create a data object with the datasets
        LineData data = new LineData(set1);

        return data;
    }
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("6hrs");
        xAxis.add("5hrs");
        xAxis.add("4hrs");
        xAxis.add("3hrs");
        xAxis.add("2hrs");
        xAxis.add("1hrs");
        return xAxis;
    }

    public void initfrag(){
        main.setVisibility(View.GONE);

        SharedPreferences sp=getActivity().getSharedPreferences("key", Context.MODE_PRIVATE);
        final String userid = sp.getString(Configurations.KEY_USERID,"Not Found");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configurations.baseurl+"companydetails",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response",response);
                        Gson gson = new GsonBuilder().create();
                        CompanyDetails res = gson.fromJson(response,CompanyDetails.class);
                        if(res.getSuccess() == 1){
                            int i ;
                            float previous = res.getCompany().getPrevious();
                            float rate = res.getCompany().getRate();
                            float changepercent = ((rate-previous)/previous)*100;
                            float change = rate-previous;
                            DecimalFormat f = new DecimalFormat("##,##,##,###.00");

                            hourpricetxt.setText("Rs."+f.format(change));
                            if(change>=0)
                            {
                                hourchangetxt.setBackgroundResource(R.drawable.textview_border);
                                hourchangetxt.setTextColor(getResources().getColor(R.color.profit));
                                hourchangetxt.setText("+"+f.format(changepercent)+"%");

                            }
                            else
                            {
                               hourchangetxt.setBackgroundResource(R.drawable.textview_border2);
                               hourchangetxt.setTextColor(getResources().getColor(R.color.loss));
                                hourchangetxt.setText("-"+f.format(changepercent)+"%");
                            }
                            if(res.getHistory().size() != 0)
                            {
                                int colors =  Color.rgb(255, 255, 255);
                                LineData data = getData(6, 100,res.getHistory());
                                setupChart(comphistory, data,colors);
                                maxtxt.setText("-"+res.getHistory().size()+" Hrs");
                            }
                            else {
                                maxtxt.setVisibility(View.GONE);
                                mintxt.setVisibility(View.GONE);
                            }

                            if(res.getCompleted() != null){
                                previous = res.getCompleted().getPrice();
                                changepercent = ((rate-previous)/previous)*100;
                                change = rate-previous;

                                lasttranstxt.setText("Rs."+f.format(change));
                               // Lasttranschangetxt.setText(f.format(changepercent)+"%");

                                if(change>=0)
                                {
                                    Lasttranschangetxt.setBackgroundResource(R.drawable.textview_border);
                                    Lasttranschangetxt.setTextColor(getResources().getColor(R.color.profit));
                                    Lasttranschangetxt.setText("+"+f.format(changepercent)+"%");}
                                else
                                {
                                    Lasttranschangetxt.setBackgroundResource(R.drawable.textview_border2);
                                    Lasttranschangetxt.setTextColor(getResources().getColor(R.color.loss));
                                    Lasttranschangetxt.setText("-"+f.format(changepercent)+"%");

                            }
                                if(change==0){
                                    lasttranstxt.setText("Rs.0.00");
                                }



                            }
                            else {
                                lasttranstxt.setText("No Last Transaction");
                                Lasttranschangetxt.setVisibility(View.GONE);
                                Lasttranschangetxt.setBackgroundColor(Color.GRAY);
                            }

                        }
                        main.setVisibility(View.VISIBLE);
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
                params.put("id",userid);
                params.put("shortname",company);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

}
