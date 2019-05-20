package com.example.pools;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.Interface.ItemClickListener;
import com.example.Model.Party;
import com.example.ViewHolder.PartyViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PartyList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference partyList;

    String partyId = "";

    FirebaseRecyclerAdapter<Party, PartyViewHolder>adapter;

    //Search functionality

    FirebaseRecyclerAdapter<Party, PartyViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Firebase
        database = FirebaseDatabase.getInstance();
        partyList = database.getReference("Party");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_party);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //get Intent here
        if (getIntent() != null)
            partyId = getIntent().getStringExtra("PartyId");
         if (!partyId.isEmpty() && partyId!= null )
         {
             loadPartyList(partyId);
         }
         //Search

        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
         materialSearchBar.setHint("Enter your party");
         //MaterialSearchBar.setSpeechMode(false);No need because we already define it at HTML
         loadSuggest();//WRITE FUNCTION TO LOAD SUGGEST FROM FIREBASE

        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //when user types their text, we will change suggest list
                List<String> suggest = new ArrayList<>();
                for (String search:suggestList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //WHEN SEARCH BAR IS CLOSE
                //RESTORE ORIGINAL SUGGEST
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //WHEN SEARCH FINISH
                startSearch(text);
            }

            private void startSearch(CharSequence text) {
                searchAdapter = new FirebaseRecyclerAdapter<Party, PartyViewHolder>(
                        Party.class,
                        R.layout.party_item,
                        PartyViewHolder.class,
                        partyList.orderByChild("Name").equalTo(text.toString())//Compare name
                ) {
                    @Override
                    protected void populateViewHolder(PartyViewHolder viewHolder, Party model, int position) {
                        viewHolder.party_name.setText(model.getName());
                        Picasso.with(getBaseContext()).load(model.getImage())
                                .into(viewHolder.party_image);

                        final Party local = model;
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                //Start new Activity
                                Intent partyDetail = new Intent(PartyList.this,PartyDetails.class);
                                partyDetail.putExtra("PartyId",searchAdapter.getRef(position).getKey());//send Party Id to new Activity
                                startActivity(partyDetail);
                            }
                        });
                    }
                };
                recyclerView.setAdapter(searchAdapter);//Set adapter for recycler view Search result
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
        




    }

    private void loadSuggest() {
        partyList.orderByChild("PartyIds").equalTo(partyId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Party item = postSnapShot.getValue(Party.class);
                            suggestList.add(item.getName());//Add name of Party to suggest list
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadPartyList(String partyId) {
     adapter = new FirebaseRecyclerAdapter<Party, PartyViewHolder>(Party.class,
             R.layout.party_item,
             PartyViewHolder.class,
             partyList.orderByChild("PartyIds").equalTo(partyId)) { // LIKE Select * from Party where PartyId =
         @Override
         protected void populateViewHolder(PartyViewHolder viewHolder, Party model, int position) {
          viewHolder.party_name.setText(model.getName());
          viewHolder.party_price.setText(String.format("$ %s",model.getPrice().toString()));
             Picasso.with(getBaseContext()).load(model.getImage())
                     .into(viewHolder.party_image);

             final Party local = model;
             viewHolder.setItemClickListener(new ItemClickListener() {
                 @Override
                 public void onClick(View view, int position, boolean isLongClick) {
                   //Start new Activity
                    Intent partyDetail = new Intent(PartyList.this,PartyDetails.class);
                    partyDetail.putExtra("PartyId",adapter.getRef(position).getKey());//send Party Id to new Activity
                    startActivity(partyDetail);
                 }
             });
         }
     };
     //set Adapter
        recyclerView.setAdapter(adapter);
    }
}
