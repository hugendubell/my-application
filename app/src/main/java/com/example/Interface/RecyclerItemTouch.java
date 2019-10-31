package com.example.Interface;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouch {

    void onswiped(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
