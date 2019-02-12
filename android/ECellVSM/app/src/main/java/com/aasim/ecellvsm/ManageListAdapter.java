package com.aasim.ecellvsm;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aasim on 16/12/16.
 */
public class ManageListAdapter extends RecyclerView.Adapter<ManageListAdapter.ManageListHolder> {
    private List<ManageListItem> manageList = new ArrayList<>();
    View itemView;

    public class ManageListHolder extends  RecyclerView.ViewHolder{
        public TextView shrtnametxt,nametxt,pricetxt,stockstxt;

        public ManageListHolder(View v){
            super(v);
            shrtnametxt = (TextView) v.findViewById(R.id.pendlistshtname);
            nametxt = (TextView) v.findViewById(R.id.pendlistname);
            stockstxt = (TextView) v.findViewById(R.id.pendlistshares);
            pricetxt = (TextView) v.findViewById(R.id.pendlistprice);
        }
    }

    public ManageListAdapter(List<ManageListItem> manageList) {
        this.manageList = manageList;

    }

    @Override
    public ManageListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_list_row, parent, false);

        return new ManageListHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ManageListHolder holder, int position) {
        ManageListItem company = manageList.get(position);
        holder.shrtnametxt.setText(company.getShortname());
        holder.nametxt.setText(company.getName());
        holder.stockstxt.setText(company.getShares());
        if(Integer.parseInt(company.getChange()) == 2 ){
            holder.pricetxt.setText("Sell");
            holder.pricetxt.setBackgroundResource(R.drawable.textview_border);
            holder.pricetxt.setTextColor(itemView.getResources().getColor(R.color.profit));
        }
        else{
            holder.pricetxt.setText("Buy");
            holder.pricetxt.setBackgroundResource(R.drawable.textview_border2);
            holder.pricetxt.setTextColor(itemView.getResources().getColor(R.color.loss));
        }

    }



    @Override
    public int getItemCount() {
        return manageList.size();
    }
}
