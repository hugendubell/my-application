package com.example.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Interface.ItemClickListener;
import com.example.pools.R;

public class PartyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView party_name,party_price;
    public ImageView party_image,btnShare,btnFav;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public PartyViewHolder(@NonNull View itemView) {
        super(itemView);

        party_name = (TextView)itemView.findViewById(R.id.party_name);
        party_image = (ImageView)itemView.findViewById(R.id.party_Image);
        party_price = (TextView) itemView.findViewById(R.id.party_price);
        btnShare = (ImageView)itemView.findViewById(R.id.btnShare);
        btnFav = (ImageView)itemView.findViewById(R.id.btnFav);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
