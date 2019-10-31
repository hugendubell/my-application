package com.example.pools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.model.Model;
import com.example.Common.Common;
import com.example.Common.Config;
import com.example.Model.Cart;


import com.example.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import info.hoang8f.widget.FButton;

public class CartActivity extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 9999 ;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    
    TextView txtTotalPrice;
    FButton btnPlace;

    private Cart cart;

    private int overTotalPrice = 0;




    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;


//Paypal payment
static PayPalConfiguration config = new PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //USE SANDBOX BECAUSE WE TEST,CHANGE IT LATE IF YOU ARE GOING TO P
        .clientId(Config.PAYPAL_CLIENT_ID);


    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    String phone;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init Paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnPlace = (FButton)findViewById(R.id.btnPlaceOrder);
        txtTotalPrice = (TextView)findViewById(R.id.total);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overTotalPrice == 0){
                    Toast.makeText(CartActivity.this, "Cart empty, please add party items", Toast.LENGTH_SHORT).show();
                }
                    ///Crreate new Request
                    PayPalPayment payment = new PayPalPayment(new BigDecimal(overTotalPrice), "ILS", "  Poolz App Order",
                            PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent1 = new Intent(getApplicationContext(), PaymentActivity.class);

                    //send the same configuration for restart resiliency
                    intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                    intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                    startActivityForResult(intent1, PAYPAL_REQUEST_CODE);
                }




        });



    }






    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                .child(Common.currentUser.getPhone())
                .child("Party products"),Cart.class)
                .build();

       adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {




                holder.cartItemName.setText(model.getPname());
                holder.cartItemPrice.setText(model.getPrice() +" ₪");
                holder.cartItemCount.setText("Quantity = " + model.getQuantity());
                Picasso.with(getBaseContext()).load(model.getPimage())
                        .resize(70,70)
                        .centerCrop().placeholder(R.drawable.logo)
                        .into(holder.cart_image);




                int oneTypeProductTprice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductTprice;
                //Locale locale = new Locale("en","US");
                //NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                txtTotalPrice.setText(String.valueOf(overTotalPrice) + " ₪");


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {

                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart options: ");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0)
                                {
                                    cartListRef.child("User View")
                                            .child(Common.currentUser.getPhone())
                                            .child("Party products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        cartListRef.child("Admin View")
                                                                .child(Common.currentUser.getPhone())
                                                                .child("Party products")
                                                                .child(model.getPid())
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        Toast.makeText(CartActivity.this, "Party item removed successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });


                                                    }
                                                    overTotalPrice = overTotalPrice - oneTypeProductTprice;
                                                    txtTotalPrice.setText(String.valueOf(overTotalPrice) + " ₪");

                                                }
                                            });
                                }
                            }
                        });
                        builder.show();



                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_layout,viewGroup,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }


        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();




    }

    final DatabaseReference cartPayCon = FirebaseDatabase.getInstance().getReference().child("Conformation payment");

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {



        if (requestCode == PAYPAL_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation!=null)
                {
                    try{
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        conformationFire();

                        Toast.makeText(this, "Thank you order placed", Toast.LENGTH_SHORT).show();
                        finish();
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
            else if (requestCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            else  if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid payment", Toast.LENGTH_SHORT).show();
        }
    }

    //once conformed it will be updated in the firebase database
    private void conformationFire() {

        PartyDetails partyDetails = new PartyDetails();

        String saveCurrentTime,saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",partyDetails.partyId);
        cartMap.put("pname",partyDetails.party_name.getText().toString());
        cartMap.put("price",partyDetails.party_price.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",partyDetails.numberButton.getNumber());
        cartMap.put("Conformation status", "approved");

        cartPayCon.child(Common.currentUser.getPhone()).updateChildren(cartMap);

    }


}
