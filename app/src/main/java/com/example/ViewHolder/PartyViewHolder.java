package com.example.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Common.Common;
import com.example.Interface.ItemClickListener;
import com.example.pools.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PartyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView party_name,party_price;
    public ImageView party_image,btnShare,btnFav,filled;
    DatabaseReference DatabaseLike;
    String check = "unfill";

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
        filled = (ImageView)itemView.findViewById(R.id.filled);

        DatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        //AuthLike = FirebaseAuth.getInstance();


        itemView.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }


}