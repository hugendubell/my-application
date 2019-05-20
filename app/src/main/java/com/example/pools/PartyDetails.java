package com.example.pools;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.Database.Database;
import com.example.Model.Order;
import com.example.Model.Party;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PartyDetails extends AppCompatActivity {

    TextView party_name,party_price,party_description;
    ImageView  img_party;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;

    ElegantNumberButton numberButton;

    String partyId ="";

    FirebaseDatabase database;
    DatabaseReference Party;

    Party currentParty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Firebase
        database = FirebaseDatabase.getInstance();
        Party = database.getReference("Party");

        //Init view
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        partyId,
                        numberButton.getNumber(),
                        currentParty.getName(),
                        currentParty.getPrice()

                ));
                Toast.makeText(PartyDetails.this, "Added to your party Cart!", Toast.LENGTH_SHORT).show();
            }

        });

        party_description = (TextView)findViewById(R.id.party_description);
        party_name = (TextView)findViewById(R.id.party_name);
        party_price = (TextView)findViewById(R.id.party_price);
        img_party = (ImageView) findViewById(R.id.img_party);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsingAppbar);


        //get PartyId from Intent
        if (getIntent()!=null)
            partyId = getIntent().getStringExtra("PartyId");
            if (!partyId.isEmpty())
            {
                getDetailParty(partyId);
            }






    }

    private void getDetailParty(final String partyId) {
        Party.child(partyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentParty = dataSnapshot.getValue(Party.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentParty.getImage())
                        .into(img_party);

                collapsingToolbarLayout.setTitle(currentParty.getName());

                party_price.setText(currentParty.getPrice());

                party_name.setText(currentParty.getName());

                party_description.setText(currentParty.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
