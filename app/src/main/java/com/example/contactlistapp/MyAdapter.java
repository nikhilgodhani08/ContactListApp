package com.example.contactlistapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends FirebaseRecyclerAdapter<User,MyAdapter.MyViewHolder> {

    public MyAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull User model) {
        holder.txtName.setText(model.getName());

        holder.txtPhone.setText(model.getNumber());
        Glide.with(holder.imageDP.getContext()).load(model.getPfurl()).into(holder.imageDP);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.imageDP.getContext(),ManageContact.class);
                intent.putExtra("key",getRef(position).getKey());

                holder.imageDP.getContext().startActivity(intent);

            }
        });


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageDP;
        TextView txtName,txtPhone;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageDP=itemView.findViewById(R.id.imgDp);
            txtName=itemView.findViewById(R.id.txtName);
            txtPhone=itemView.findViewById(R.id.txtNumber);

        }
    }
}
