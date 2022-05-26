package com.example.contactlistapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firestore.v1.Value;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.Objects;

public class ManageContact extends AppCompatActivity {

    ImageButton btncall;
    ImageButton btnedit,btnshare,btndelete,btnmsg;
    ImageView displayimg;
    TextView displayName,displayPhone;
    Dialog dialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contact);
        getSupportActionBar().hide();
        btncall = findViewById(R.id.btncall);
        btnedit = findViewById(R.id.btnedit);
        btndelete = findViewById(R.id.btndelete);
        btnshare = findViewById(R.id.btnshare);
        btnmsg=findViewById(R.id.btnmsg);
        displayimg = findViewById(R.id.displayImg);
        displayName = findViewById(R.id.displayName);
        displayPhone = findViewById(R.id.displayNumber);
        dialog=new Dialog(ManageContact.this);
        dialog.setContentView(R.layout.image_layout);
        ImageView img=dialog.findViewById(R.id.showimage);





        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("Contacts");
        String key=getIntent().getStringExtra("key");

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(key).removeValue();
                Toast.makeText(ManageContact.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ManageContact.this,MainActivity.class));
            }
        });



        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String name = snapshot.child("name").getValue().toString();
                    String number = snapshot.child("number").getValue().toString();
                    String url = snapshot.child("pfurl").getValue().toString();

                    Glide.with(ManageContact.this).load(url).into(displayimg);


                    displayName.setText(name);
                    displayPhone.setText(number);
                    displayimg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Glide.with(ManageContact.this).load(url).into(img);
                            dialog.show();
                        }
                    });

                    btncall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dexter.withContext(ManageContact.this)
                                    .withPermission(Manifest.permission.CALL_PHONE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
                                            startActivity(intent);

                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                            permissionToken.continuePermissionRequest();

                                        }
                                    })
                                    .check();

                        }
                    });

                    btnmsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+number));
                            startActivity(intent);

                        }
                    });

                    btnedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DialogPlus dialogPlus=DialogPlus.newDialog(ManageContact.this)
                                    .setContentHolder(new ViewHolder(R.layout.edit_contact))
                                    .setHeader(R.layout.header_editcontact)
                                    .setExpanded(true,600)
                                    .create();
                            EditText changeName= (EditText) dialogPlus.findViewById(R.id.changeName);
                            EditText changeNumber=(EditText) dialogPlus.findViewById(R.id.changeNumber);
                            Button changesave= (Button) dialogPlus.findViewById(R.id.changeSave);
                            changeName.setText(name);
                            changeNumber.setText(number);
                            dialogPlus.show();

                            changesave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HashMap<String,Object> map=new HashMap<>();
                                    map.put("name",changeName.getText().toString());
                                    map.put("number",changeNumber.getText().toString());
                                    FirebaseDatabase.getInstance().getReference("Contacts").child(key).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ManageContact.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                            startActivity(new Intent(ManageContact.this,MainActivity.class));

                                        }
                                    });

                                }
                            });






                        }
                    });

                    btnshare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,"Name :- "+name+"\nNumber :- "+number);
                            intent.setType("text/plain");
                            startActivity(Intent.createChooser(intent,"Send Contact"));
                        }
                    });




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }







    @Override
    public void onBackPressed() {
        startActivity(new Intent(ManageContact.this,MainActivity.class));
        super.onBackPressed();
    }
}