package com.example.pools;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.Adapters.GalleryImageAdapter;
import com.example.Interface.IRecyclerViewClickListener;

import java.util.Random;

public class Gallery extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery2);

        /*ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Gallery");*/

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Toolbar toolbar = findViewById(R.id.gallery_toolbar);
        toolbar.setTitle("");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_gallery);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        videoView = (VideoView)findViewById(R.id.video_view);


        //creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/thepool-124cc.appspot.com/o/AQUABOULEVARD%2BPOOL%2BPARTY%2B-%2BR%25C3%25A9veillon%2B%25C3%25A0%2BParis.mp4?alt=media&token=804d1316-6a28-4fea-9ee8-8339558ae783");

        //setting MediaController and Video Uri
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);

        //start videoView
        videoView.start();


        Random random = new Random();
         final  String[]  images =  new String[40];

         for (int i=0;i<images.length;i++){
             images[i] = "https://picsum.photos/600?image="+random.nextInt(1000+1);


             final IRecyclerViewClickListener listener = new IRecyclerViewClickListener() {
                 @Override
                 public void onClick(View view, int position) {
                 //open the activities full screen

                     Intent i = new Intent(getApplicationContext(),FullScreenGalleryActivity.class);
                     //the actual pic
                     i.putExtra("IMAGES",images);
                     //the position on the pic index
                     i.putExtra("POSITION",position);
                     startActivity(i);


                 }
             };

             GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(this,images,listener);
             recyclerView.setAdapter(galleryImageAdapter);

         }
    }
}
