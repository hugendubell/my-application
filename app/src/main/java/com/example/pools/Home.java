package com.example.pools;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.Common.Common;

import com.example.Interface.ItemClickListener;
import com.example.Model.Area;
import com.example.ViewHolder.menuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference Area;
    TextView txtFulLName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Area, menuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton payFab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        //toolbar.setTitleTextColor(Color.parseColor("#2df9ff"));

        toolbar.setBackground(new ColorDrawable(Color.parseColor("#000000")));
        setSupportActionBar(toolbar);

        //View
        swipeRefreshLayout =(SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
        android.R.color.holo_green_dark, android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext()))

                    loadMenu();
                else {
                    Toast.makeText(getBaseContext(), "Please check Internet connection ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        //Default,load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext()))

                    loadMenu();
                else {
                    Toast.makeText(getBaseContext(), "Please check Internet connection ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });




        //init Firebase
        database = FirebaseDatabase.getInstance();
        Area = database.getReference("Area");

        Paper.init(this);



        FloatingActionButton payFab =(FloatingActionButton) findViewById(R.id.payFab);
        payFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartList = new Intent(Home.this,CartActivity.class);
                startActivity(cartList);

            }

        });





        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //SET NAME FOR USER
        View headerView = navigationView.getHeaderView(0);
        txtFulLName = (TextView)headerView.findViewById(R.id.txtFulLName);
        txtFulLName.setText("Welcome " +  Common.currentUser.getName());

        //LOAD MENU

        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        //LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
               // R.anim.layout_fall_down);
        //recycler_menu.setLayoutAnimation(controller);
        recycler_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));



    }

    private void loadMenu() {
        FirebaseRecyclerOptions<Area> options = new FirebaseRecyclerOptions.Builder<Area>()
                .setQuery(Area,Area.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<com.example.Model.Area, menuViewHolder>(options) {
            @NonNull
            @Override
            public menuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_item,viewGroup,false);
                return new menuViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull menuViewHolder viewHolder, int position, @NonNull com.example.Model.Area model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);

                final Area clickitem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get CategoryId and send to new Activity
                        Intent partyList = new Intent(Home.this,PartyList.class);
                        //Because PartyId is key,so we just get key of this item
                        partyList.putExtra("PartyId",adapter.getRef(position).getKey());
                        startActivity(partyList);

                        //Animation
                        //recycler_menu.getAdapter().notifyDataSetChanged();
                        //recycler_menu.scheduleLayoutAnimation();

                    }
                });
            }
        };
        adapter.startListening();
        recycler_menu.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.menu_search)
        startActivity(new Intent(Home.this,SearchActivity.class));



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.main) {
            // Handle gallery action
        } else if (id == R.id.gallery) {

            Intent gallery = new Intent(Home.this,Gallery.class);
            startActivity(gallery);





        } else if (id == R.id.signOut) {
            //Delete remember user & password

            Paper.book().destroy();
            //Logout
            Intent signIn = new Intent(Home.this,SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);


        }else if (id == R.id.MyCart){
            Intent cartList = new Intent(Home.this,CartActivity.class);
            startActivity(cartList);

        }
        else if(id == R.id.abouts){
            Intent abouts = new Intent(Home.this,AboutUs.class);
            startActivity(abouts);

        }
        else if(id == R.id.changePass)
        {
           showChangePasswordDailog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePasswordDailog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("CHANGE PASSWORD");
        alertDialog.setMessage("Please fill all information");

        alertDialog.setIcon(R.drawable.ic_security);

        LayoutInflater inflater = LayoutInflater.from(this);
        View Layout_pwd = inflater.inflate(R.layout.change_password_layout,null);

        MaterialEditText edtPassword = (MaterialEditText)Layout_pwd.findViewById(R.id.edtPassword);

        MaterialEditText edtNewPassword = (MaterialEditText)Layout_pwd.findViewById(R.id.edtNewPassword);

        MaterialEditText edtRepeatPassword = (MaterialEditText)Layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(Layout_pwd);

        //Button
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //change password here
                //FOR USE SPOTSDIALOG,PLEASE USE ALERTDIALOG FROM ANDROID.APP,NOT FROM V7 LIKE ABOVE ALERTDIALOG
                android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                //Check old password
                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword()))
                {
                    //check new password and repeat password
                    if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString()))
                    {
                        Map<String,Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("password",edtNewPassword.getText().toString());

                        //Make update
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, "Password was updated successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else{
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "New password doesn't match!", Toast.LENGTH_SHORT).show();
                    }
                }
              else
                {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Wrong old password!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
            }
        });
        alertDialog.show();


    }
}
