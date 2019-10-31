package com.example.pools;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.Common.Common;
import com.example.Interface.ItemClickListener;
import com.example.Model.Party;
import com.example.ViewHolder.PartyViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    //Search
    FirebaseRecyclerAdapter<Party, PartyViewHolder> adapter;
    FirebaseRecyclerAdapter<Party, PartyViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String partyId = "";
    FirebaseDatabase database;
    DatabaseReference partyList;
    DatabaseReference mDatabaseLike;
    private boolean like = false;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Firebase
        database = FirebaseDatabase.getInstance();
        partyList = database.getReference("Party");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_search);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mDatabaseLike = database.getReference().child("Likes");

        //swipeLayout
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.allSearchSwipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //GET INTENT HERE
                /*if (getIntent() != null)
                    partyId = getIntent().getStringExtra("PartyId");
                if (!partyId.isEmpty() && partyId!= null )
                {*/
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadAllParties();
                    }else {
                        Toast.makeText(SearchActivity.this, "Please check Internet connection ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                /*if (getIntent() != null)
                    partyId = getIntent().getStringExtra("PartyId");
                if (!partyId.isEmpty() && partyId!= null )
                {*/
                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadAllParties();
                    }else {
                        Toast.makeText(SearchActivity.this, "Please check Internet connection ", Toast.LENGTH_SHORT).show();
                        return;
                    }




        //Search

        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your party");

        //MaterialSearchBar.setSpeechMode(false); No need because we already define it at HTML
        loadSuggest();//WRITE FUNCTION TO LOAD SUGGEST FROM FIREBASE

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
                    recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //WHEN SEARCH FINISH
                startSearch(text);
            }


            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
            }
        });

        //loadAllParties
        loadAllParties();

    }

    private void startSearch(CharSequence text) {
        //create query by name
        Query searchByName = partyList.orderByChild("Name").equalTo(text.toString());
        //create options with query
        FirebaseRecyclerOptions<Party> partyOptions = new FirebaseRecyclerOptions.Builder<Party>()
                .setQuery(searchByName,Party.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Party, PartyViewHolder>(partyOptions) {

            PartyList partyList = new PartyList();

            @Override
            protected void onBindViewHolder(@NonNull PartyViewHolder viewHolder, int position, @NonNull Party model) {
                viewHolder.party_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.party_image);

                viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pTitle = viewHolder.party_name.getText().toString().trim();

                        BitmapDrawable bitmapDrawable = (BitmapDrawable)viewHolder.party_image.getDrawable();
                        if(bitmapDrawable != null){

                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            shareImageAndText(pTitle,bitmap);
                        }

                    }

                    private void shareImageAndText(String pTitle,Bitmap bitmap){


                        //concatenate title and description to share


                        Uri uri = saveImageToShare(bitmap);

                        //share intent
                        Intent sIntent = new Intent(Intent.ACTION_SEND);
                        //URI= bitmap
                        sIntent.putExtra(Intent.EXTRA_STREAM,uri);
                        //ptitle is the title for the party
                        sIntent.putExtra(Intent.EXTRA_TEXT,pTitle);
                        sIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                        sIntent.setType("images/png");
                        startActivity(Intent.createChooser(sIntent,"Share via"));

                    }

                    private Uri saveImageToShare(Bitmap bitmap){
                        File imageFolder = new File(getBaseContext().getCacheDir(),"images");
                        Uri uri = null;

                        try{
                            imageFolder.mkdirs();//create if not exists
                            File file = new File(imageFolder,"shared_image.png");

                            FileOutputStream stream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
                            stream.flush();
                            stream.close();
                            uri = FileProvider.getUriForFile(getBaseContext(),"com.example.pools.fileprovider",file);

                        }catch (Exception e){
                            Toast.makeText(SearchActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        return  uri;
                    }



                });

                viewHolder.btnFav.setOnClickListener(new View.OnClickListener() {
                    String check = "unfilled";
                    final String  Image_key_ = getRef(position).getKey();

                    @Override
                    public void onClick(View v) {
                        like = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (like) {

                                    if (dataSnapshot.child(Image_key_).hasChild(Common.currentUser.getPhone())) {


                                        mDatabaseLike.child(Common.currentUser.getPhone()).child(Image_key_).removeValue();
                                        //viewHolder.btnFav.setImageResource(R.drawable.ic_heart);
                                        Toast.makeText(SearchActivity.this, "you like this party", Toast.LENGTH_SHORT).show();
                                        viewHolder.btnFav.setImageResource(R.drawable.ic_heart);
                                        like = false;
                                    } else {
                                        mDatabaseLike.child(Common.currentUser.getPhone()).child(Image_key_).setValue("Like");
                                        mDatabaseLike.child(Common.currentUser.getPhone()).child("User name").setValue(Common.currentUser.getName());

                                        //animateHeart(viewHolder.filled);
                                        /*viewHolder.btnFav.setVisibility(View.GONE);
                                        viewHolder.filled.setVisibility(View.VISIBLE);*/
                                        viewHolder.btnFav.setImageResource(R.drawable.like);
                                        Toast.makeText(SearchActivity.this, "you like this party", Toast.LENGTH_SHORT).show();

                                        like = false;


                                    }


                                }

                            }



                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                final Party local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent partyDetail = new Intent(SearchActivity.this,PartyDetails.class);
                        partyDetail.putExtra("PartyId",searchAdapter.getRef(position).getKey());//send Party Id to new Activity
                        startActivity(partyDetail);
                    }
                });
            }


            @NonNull
            @Override
            public PartyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.party_item,viewGroup,false);
                return new PartyViewHolder(itemView);

            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);//Set adapter for recycler view Search result
    }

//thi action will load all of the parties
    private void loadAllParties() {
        Query searchParty = partyList;
        //create options with query
        FirebaseRecyclerOptions<Party> partyOptions = new FirebaseRecyclerOptions.Builder<Party>()
                .setQuery(searchParty,Party.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Party, PartyViewHolder>(partyOptions) {
            @NonNull
            @Override
            public PartyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView =LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.party_item,viewGroup,false);
                return new PartyViewHolder(itemView);

            }

            @Override
            protected void onBindViewHolder(@NonNull PartyViewHolder viewHolder, int position, @NonNull Party model) {
                viewHolder.party_name.setText(model.getName());
                viewHolder.party_price.setText(String.format(" %s", model.getPrice().toString()) + "₪");
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.party_image);

                viewHolder.btnFav.setOnClickListener(new View.OnClickListener() {
                    String check = "unfilled";
                    final String  Image_key_ = getRef(position).getKey();

                    @Override
                    public void onClick(View v) {
                        like = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (like) {

                                    if (dataSnapshot.child(Image_key_).hasChild(Common.currentUser.getPhone())) {


                                        mDatabaseLike.child(Common.currentUser.getPhone()).child(Image_key_).removeValue();
                                        //viewHolder.btnFav.setImageResource(R.drawable.ic_heart);
                                        Toast.makeText(SearchActivity.this, "you like this party", Toast.LENGTH_SHORT).show();
                                        viewHolder.btnFav.setImageResource(R.drawable.ic_heart);
                                        like = false;
                                    } else {
                                        mDatabaseLike.child(Common.currentUser.getPhone()).child(Image_key_).setValue("Like");
                                        mDatabaseLike.child(Common.currentUser.getPhone()).child("User name").setValue(Common.currentUser.getName());

                                        //animateHeart(viewHolder.filled);
                                        /*viewHolder.btnFav.setVisibility(View.GONE);
                                        viewHolder.filled.setVisibility(View.VISIBLE);*/
                                        viewHolder.btnFav.setImageResource(R.drawable.like);
                                        Toast.makeText(SearchActivity.this, "you like this party", Toast.LENGTH_SHORT).show();

                                        like = false;


                                    }


                                }

                            }



                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pTitle = viewHolder.party_name.getText().toString().trim();

                        BitmapDrawable bitmapDrawable = (BitmapDrawable)viewHolder.party_image.getDrawable();
                        if(bitmapDrawable != null){

                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            shareImageAndText(pTitle,bitmap);
                        }

                    }

                    private void shareImageAndText(String pTitle,Bitmap bitmap){


                        //concatenate title and description to share


                        Uri uri = saveImageToShare(bitmap);

                        //share intent
                        Intent sIntent = new Intent(Intent.ACTION_SEND);
                        //URI= bitmap
                        sIntent.putExtra(Intent.EXTRA_STREAM,uri);
                        //ptitle is the title for the party
                        sIntent.putExtra(Intent.EXTRA_TEXT,pTitle);
                        sIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                        sIntent.setType("images/png");
                        startActivity(Intent.createChooser(sIntent,"Share via"));

                    }

                    private Uri saveImageToShare(Bitmap bitmap){
                        File imageFolder = new File(getBaseContext().getCacheDir(),"images");
                        Uri uri = null;

                        try{
                            imageFolder.mkdirs();//create if not exists
                            File file = new File(imageFolder,"shared_image.png");

                            FileOutputStream stream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
                            stream.flush();
                            stream.close();
                            uri = FileProvider.getUriForFile(getBaseContext(),"com.example.pools.fileprovider",file);

                        }catch (Exception e){
                            Toast.makeText(SearchActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        return  uri;
                    }
                });

                final Party local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent partyDetail = new Intent(SearchActivity.this, PartyDetails.class);
                        partyDetail.putExtra("PartyId", searchAdapter.getRef(position).getKey());
                        //send Party Id to new Activity
                        startActivity(partyDetail);
                    }
                });
            }
        };


        //set Adapter

        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);// SET ADAPTER IN RECYCLER VIEW IN SEARCH RESULT
        //stop refreshing
        swipeRefreshLayout.setRefreshing(false);

    }

    private void loadSuggest() {
        partyList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Party item = postSnapShot.getValue(Party.class);
                            suggestList.add(item.getName());//Add name of Party to suggest list
                        }
                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    protected void onStop() {
        /*if (searchAdapter!=null)
            searchAdapter.startListening();*/
        if (searchAdapter!=null)
            searchAdapter.startListening();

        super.onStop();
    }
}