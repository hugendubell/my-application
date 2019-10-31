package com.example.pools;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.Common.Common;
import com.example.Model.Party;
import com.example.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class PartyDetails extends AppCompatActivity implements RatingDialogListener {

    TextView party_name,party_price,party_description,party_address;
    ImageView  img_party,party_mapLoc;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart, btnRating;


    RatingBar ratingBar;

    ElegantNumberButton numberButton;

    String partyId ="";

    FirebaseDatabase database;
    DatabaseReference Party;
    DatabaseReference ratingTbl;

    Party currentParty;

    Button addToCartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Firebase
        partyId = getIntent().getStringExtra("pid");
        database = FirebaseDatabase.getInstance();
        Party = database.getReference("Party");
        ratingTbl = database.getReference("Rating");

        //Init view
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);

        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);
        btnRating = (FloatingActionButton)findViewById(R.id.btnRating);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);


        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartList = new Intent(PartyDetails.this,CartActivity.class);
                startActivity(cartList);
            }
        });



        addToCartButton = (Button)findViewById(R.id.btnAddToCart);
        party_description = (TextView)findViewById(R.id.party_description);
        party_name = (TextView)findViewById(R.id.party_name);
        party_price = (TextView)findViewById(R.id.party_price);
        img_party = (ImageView) findViewById(R.id.img_party);
        party_mapLoc = (ImageView)findViewById(R.id.party_mapLoc);
        party_address = (TextView)findViewById(R.id.party_address);



        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsingAppbar);


        getProductDetails(partyId);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }


        });


        //get PartyId from Intent(partyId is the number of the party itself)
        if (getIntent()!=null)
            partyId = getIntent().getStringExtra("PartyId");
            if (!partyId.isEmpty())
            {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    getDetailParty(partyId);
                    getRatingParty(partyId);
                }
                else{
                Toast.makeText(PartyDetails.this, "Please check Internet connection ", Toast.LENGTH_SHORT).show();
                return;
            }
            }






    }

    public HashMap<String, Object> addingToCartList() {
        String saveCurrentTime,saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

       final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",partyId);
        cartMap.put("pname",party_name.getText().toString());
        cartMap.put("price",party_price.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("image",img_party.toString());
        cartMap.put("quantity",numberButton.getNumber());



        cartListRef.child("User View").child(Common.currentUser.getPhone())
                .child("Party products").child(partyId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(Common.currentUser.getPhone())
                                    .child("Party products").child(partyId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(PartyDetails.this, "Added to your party list cart", Toast.LENGTH_SHORT).show();

                                                //Intent intent = new Intent(PartyDetails.this,Home.class);
                                                //startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });


        return cartMap;
    }

    private void getProductDetails(String partyId) {
    }


    private void getRatingParty(String partyId){

        com.google.firebase.database.Query partyRating = ratingTbl.orderByChild("partyId").equalTo(partyId);

        partyRating.addValueEventListener(new ValueEventListener() {
            int count = 0,sum =0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count!=0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void showRatingDialog(){
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Party")
                .setDescription("Select rating by stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.lightGrey)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(PartyDetails.this)
                .show();


    }

    private void getDetailParty(final String partyId) {
        Party.child(partyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentParty = dataSnapshot.getValue(Party.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentParty.getImage())
                        .into(img_party);

                Picasso.with(getBaseContext()).load(currentParty.getImageMap())
                        .into(party_mapLoc);


                collapsingToolbarLayout.setTitle(currentParty.getName());

                party_price.setText(currentParty.getPrice());

                party_name.setText(currentParty.getName());

                party_description.setText(currentParty.getDescription());

                party_address.setText(currentParty.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {
        //GET RATING AND UPLOAD TO FIREBASE
        Rating rating = new Rating(Common.currentUser.getPhone(),
                partyId,
                String.valueOf(value),
                comments);

        //Fix user can rate multiple times
        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(PartyDetails.this, "Thank you for your rating!!!", Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
