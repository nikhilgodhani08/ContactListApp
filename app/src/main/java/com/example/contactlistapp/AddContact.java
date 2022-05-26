package com.example.contactlistapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class AddContact extends AppCompatActivity {

    ImageView dp;
    TextView txtBrowser;
    Button btnSave, btnCancle;
    EditText edtName, edtPhone;
    public static Bitmap bitmap;
    Uri filepath;
    FirebaseAuth mAuth;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        getSupportActionBar().hide();
        dp = findViewById(R.id.dp);
        txtBrowser = findViewById(R.id.txtBrowser);
        btnSave = findViewById(R.id.btnSave);
        btnCancle = findViewById(R.id.btnCancel);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        mAuth = FirebaseAuth.getInstance();


        txtBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebrowser();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddContact.this, MainActivity.class));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtName.getText().toString().isEmpty()) {
                    edtName.setError("Please Enter Your Name");
                } else if (edtPhone.getText().toString().isEmpty()) {
                    edtPhone.setError("Please Enter Number");
                } else if (edtName.getText().toString().isEmpty() && edtPhone.getText().toString().isEmpty()) {
                    Toast.makeText(AddContact.this, "Please Enter All Deatil", Toast.LENGTH_SHORT).show();
                } else if (edtPhone.getText().toString().length() != 10) {
                    edtPhone.setError("Enter 10 Digit Valid Number");
                } else {
                    uploadtoFirebase();
                }
            }
        });

    }

    private void uploadtoFirebase() {

        dialog = new ProgressDialog(this);
        dialog.setTitle("Save Contact");
        dialog.setIcon(R.drawable.ic_baseline_save_24);
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("Image " + edtPhone.getText().toString());
        storageReference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        User user = new User(edtName.getText().toString(), edtPhone.getText().toString(), uri.toString());
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("Contacts");
                        databaseReference.push().setValue(user);
                        edtName.setText("");
                        edtPhone.setText("");
                        dp.setImageResource(R.drawable.ic_baseline_person_24);
                        Toast.makeText(AddContact.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        startActivity(new Intent(AddContact.this, MainActivity.class));


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(AddContact.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        edtName.setText("");
                        edtPhone.setText("");
                        dp.setImageResource(R.drawable.ic_baseline_person_24);

                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                dialog.setMessage("Saving " + percent + " %");


            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddContact.this, MainActivity.class));
        super.onBackPressed();
    }

    private void imagebrowser() {
        Dexter.withContext(AddContact.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        filepath = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                dp.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}