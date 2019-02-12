package com.aasim.ecellvsm;

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
public class LeaderListAdapter extends RecyclerView.Adapter<LeaderListAdapter.LeaderListHolder> {
    private List<leaderlistitem> leaderlist = new ArrayList<>();
    View itemView;
    public class LeaderListHolder extends  RecyclerView.ViewHolder{
        public TextView usernametxt,ranktxt,balancetxt,changetxt;


        public LeaderListHolder(View v){
            super(v);
            usernametxt = (TextView) v.findViewById(R.id.usernamelead);
            balancetxt = (TextView) v.findViewById(R.id.listleadbal);
            ranktxt = (TextView) v.findViewById(R.id.listleadrank);
            changetxt = (TextView) v.findViewById(R.id.listleadchange);
        }
    }

    public LeaderListAdapter(List<leaderlistitem> leaderlist) {
        this.leaderlist = leaderlist;

    }

    @Override
    public LeaderListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_list_row, parent, false);

        return new LeaderListHolder(itemView);
    }



    @Override
    public void onBindViewHolder(LeaderListHolder holder, int position) {
        leaderlistitem leader = leaderlist.get(position);



        float change = Float.parseFloat(leader.getChange());

        String changes = "";
        if(change >= 0 ){
            holder.changetxt.setBackgroundResource(R.drawable.textview_border);
            holder.changetxt.setTextColor(itemView.getResources().getColor(R.color.profit));
            changes = "+"+change+"%";
        }
        else{
            holder.changetxt.setBackgroundResource(R.drawable.textview_border2);
            holder.changetxt.setTextColor(itemView.getResources().getColor(R.color.loss));

            changes = change+"%";
        }
        holder.usernametxt.setText(leader.getUsername());
        holder.balancetxt.setText(leader.getBal());
        holder.ranktxt.setText(leader.getRank());
        holder.changetxt.setText(changes);
    }



    @Override
    public int getItemCount() {
        return leaderlist.size();
    }
}
