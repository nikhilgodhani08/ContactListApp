package com.example.contactlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    RecyclerView rcvview;
    FloatingActionButton btnAdd;
    MyAdapter adapter;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        rcvview=findViewById(R.id.rcvview);
        btnAdd=findViewById(R.id.btnadd);
        txt=findViewById(R.id.txt);


        rcvview.setLayoutManager(new LinearLayoutManager(this));



        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Contacts"), User.class)
                .build();

        if(rcvview.getChildCount()<0){
            rcvview.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
        }else{
            rcvview.setVisibility(View.VISIBLE);
            txt.setVisibility(View.GONE);

        }



        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddContact.class));
            }
        });

        adapter=new MyAdapter(options);
        rcvview.setAdapter(adapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}