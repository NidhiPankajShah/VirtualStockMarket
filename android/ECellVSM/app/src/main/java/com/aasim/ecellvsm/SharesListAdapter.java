package com.aasim.ecellvsm;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aasim on 14/12/16.
 */
public class SharesListAdapter extends RecyclerView.Adapter<SharesListAdapter.SharesListHolder> {
    private List<sharesListItem> shareslist = new ArrayList<>();
    View itemView;
    public class SharesListHolder extends  RecyclerView.ViewHolder{
        public TextView shrtnametxt,nametxt,pricetxt,changetxt,worthtxt,sharestxt;

        public SharesListHolder(View v){
            super(v);
            shrtnametxt = (TextView) v.findViewById(R.id.sharelistsrtname);
            nametxt = (TextView) v.findViewById(R.id.sharelistname);
            pricetxt = (TextView) v.findViewById(R.id.sharelistprice);
            changetxt = (TextView) v.findViewById(R.id.sharelistchange);
            worthtxt = (TextView) v.findViewById(R.id.sharelistworth);
            sharestxt = (TextView) v.findViewById(R.id.sharelistshares);

        }
    }

    public SharesListAdapter(List<sharesListItem> shareslist) {
        this.shareslist = shareslist;

    }

    @Override
    public SharesListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.share_list_row, parent, false);

        return new SharesListHolder(itemView);
    }



    @Override
    public void onBindViewHolder(SharesListHolder holder, int position) {
        sharesListItem company = shareslist.get(position);
        holder.shrtnametxt.setText(company.getShortname());
        holder.nametxt.setText(company.getName());
        holder.pricetxt.setText(company.getRate());
        holder.sharestxt.setText(company.getShares());
        holder.worthtxt.setText(company.getWorth());
        if(Float.parseFloat(company.getChange())>=0) {
            holder.changetxt.setText("+"+company.getChange()+"%");
            holder.changetxt.setBackgroundResource(R.drawable.textview_border);
            holder.changetxt.setTextColor(itemView.getResources().getColor(R.color.profit));
        }
        if(Float.parseFloat(company.getChange())<0) {
            holder.changetxt.setBackgroundResource(R.drawable.textview_border2);
            holder.changetxt.setTextColor(itemView.getResources().getColor(R.color.loss));

        }

    }



    @Override
    public int getItemCount() {
        return shareslist.size();
    }
}
