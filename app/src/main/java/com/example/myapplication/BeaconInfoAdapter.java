package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BeaconInfoAdapter extends RecyclerView.Adapter<BeaconInfoAdapter.MyViewHolder> {
    Context context;
    private List<BeaconInfo> beaconInfoList;

    public BeaconInfoAdapter(Context context, List<BeaconInfo> beaconInfoList) {
        this.context = context;
        this.beaconInfoList = beaconInfoList;
    }

    @NonNull
    @Override
    public BeaconInfoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconInfoAdapter.MyViewHolder holder, int position) {
        BeaconInfo beaconInfo = beaconInfoList.get(position);
        holder.tvBeaconMac.setText(beaconInfo.getBeaconMac());
        holder.tvBeaconRssi.setText(beaconInfo.getBeaconRssi());
    }

    @Override
    public int getItemCount() {
       return beaconInfoList.size();
//        return 15;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llExtend;
        private ImageView ivExtend;
        private TextView tvBeaconMac, tvBeaconRssi;
        private boolean isExtend;
        public MyViewHolder(View view) {
            super(view);
            isExtend =false;
            tvBeaconMac =view.findViewById(R.id.tvBeaconMac);
            tvBeaconRssi =view.findViewById(R.id.tvBeaconRssi);
            ivExtend= view.findViewById(R.id.ivExtend);
            llExtend =view.findViewById(R.id.llExtend);
            ivExtend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExtend == false){
                        llExtend.setVisibility(View.VISIBLE);
                        ivExtend.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
                        isExtend =true;
                    }else{
                        llExtend.setVisibility(View.GONE);
                        ivExtend.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
                        isExtend =false;
                    }

                }
            });

        }
    }
}
