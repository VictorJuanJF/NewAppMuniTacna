package com.example.jimenez.appmunitacna;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jimenez.appmunitacna.objects.Global;
import com.squareup.picasso.Picasso;

public class recordAdapter extends RecyclerView.ViewHolder {

    View mView;

    public recordAdapter(@NonNull View itemView) {
        super(itemView);

        mView=itemView;
    }

    //Set details to recycler view row
    public void setDetails(Context ctx,String title,String description,String image){
        //Views
        TextView mTitleView=mView.findViewById(R.id.tvTitle);
        TextView mDescription=mView.findViewById(R.id.tvDescription);
        ImageView mImageView=mView.findViewById(R.id.ivPhoto);
        //Set data to views
        mTitleView.setText(title);
        mDescription.setText(description);
        Picasso.get()
                .load(image)
                .into(mImageView);



    }
}
