package com.example.jimenez.appmunitacna;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.jimenez.appmunitacna.Clases.Reporte;
import com.example.jimenez.appmunitacna.objects.FirebaseReferences;
import com.example.jimenez.appmunitacna.objects.Global;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RecordActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Action Bar
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Historial de reportes");
        //RecyclerView
        mRecyclerView=findViewById(R.id.recyclerViewRecord);
        mRecyclerView.setHasFixedSize(true);

        //set layout as Linear Layout

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //send Query to Firebase
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference(FirebaseReferences.REPORTES_REFERENCE);
        query=mRef.orderByChild("userId").equalTo(Global.getUserKey());
    }

    //Load data into recyclerView


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Reporte,recordAdapter> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Reporte, recordAdapter>(
                        Reporte.class,
                        R.layout.item_record,
                        recordAdapter.class,
                        query
                ) {
                    @Override
                    protected void populateViewHolder(recordAdapter viewHolder, Reporte model, int position) {
                        viewHolder.setDetails(getApplicationContext(),model.getTitulo(),model.getDescripcion(),model.getImgURL());

                    }
                };
        //Set adapter to recycler view
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
