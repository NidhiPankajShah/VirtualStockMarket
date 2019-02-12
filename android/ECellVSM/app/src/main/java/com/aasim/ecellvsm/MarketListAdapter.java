package com.aasim.ecellvsm;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aasim on 14/12/16.
 */
public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.MarketListHolder>{
    private List<MarketListItem> marketList = new ArrayList<>();
    View itemView;
    public class MarketListHolder extends  RecyclerView.ViewHolder{
            public TextView shrtnametxt,nametxt,pricetxt,changetxt;

            public MarketListHolder(View v){
                super(v);
                shrtnametxt = (TextView) v.findViewById(R.id.listshrtname);
                nametxt = (TextView) v.findViewById(R.id.listname);
                pricetxt = (TextView) v.findViewById(R.id.listprice);
                changetxt = (TextView) v.findViewById(R.id.listchange);
            }
    }

    public MarketListAdapter(List<MarketListItem> marketList) {
        this.marketList = marketList;

    }

    @Override
    public MarketListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.market_list_row, parent, false);

        return new MarketListHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MarketListHolder holder, int position) {
        MarketListItem company = marketList.get(position);
        float change = Float.parseFloat(company.getChange());
        DecimalFormat f = new DecimalFormat("##.00");

        String changes = "";
        if(change >= 0 ){
            holder.changetxt.setTextColor(itemView.getResources().getColor(R.color.profit));
            holder.changetxt.setBackgroundResource(R.drawable.textview_border);
            changes = "+"+f.format(change)+"%";
        }
        else{
            holder.changetxt.setBackgroundResource(R.drawable.textview_border2);
            holder.changetxt.setTextColor(itemView.getResources().getColor(R.color.loss));
            changes = f.format(change)+"%";
        }

        holder.shrtnametxt.setText(company.getShortname());
        holder.nametxt.setText(company.getName());
        holder.pricetxt.setText(company.getRate());
        holder.changetxt.setText(changes);

    }



    @Override
    public int getItemCount() {
        return marketList.size();
    }

}
