package com.example.user.isotuapp.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.utils.Constants;
import com.example.user.isotuapp.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.UUID;

public class Posting extends AppCompatActivity {

    EditText text_posting;
    ImageView image_posting;
    FloatingActionButton button_sent,button_camera;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseUser currentUser;
    private FirebaseAuth mFirebaseAuth;
    DatabaseReference dbs;
    private Uri filepath;
    private static int IMG_CAMERA = 2;
    private Post mPost;
    private ProgressDialog pDialog;
    private Uri mSelectedUri;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        text_posting = (EditText) findViewById(R.id.textPosting);
        image_posting = (ImageView) findViewById(R.id.imagePosting);
        button_camera = (FloatingActionButton) findViewById(R.id.open_camera);
        button_sent = (FloatingActionButton) findViewById(R.id.sentPost);
        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();;

        image_posting.setVisibility(View.GONE);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dbs = FirebaseDatabase.getInstance().getReference("posting");


        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(Posting.this);
            }
        });

        button_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPost();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    filepath = result.getUri();
                    image_posting.setVisibility(View.VISIBLE);
                    image_posting.setImageURI(filepath);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }

            }
            else if (requestCode == IMG_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                image_posting.setVisibility(View.VISIBLE);
                image_posting.setImageBitmap(thumbnail);
            }
        }catch (Exception e){
            Log.d("ClaimProduct", "onActivityResult: "+e.toString());
            Toast.makeText(Posting.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
        }
    }

    private void sendPost() {
        displayLoader();
        final StorageReference ref = storageReference.child("post/"+ UUID.randomUUID().toString());
        if(filepath != null) {
            UploadTask uploadTask = ref.putFile(filepath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlGambar = uri.toString();
                            key = dbs.push().getKey();
                            final String text = text_posting.getText().toString();
                            final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                            final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User usr = dataSnapshot.getValue(User.class);
                                    submitPosting(new Post(usr, key, urlGambar, text, "0",0,0,System.currentTimeMillis() ));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pDialog.dismiss();
                    Toast.makeText(Posting.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    pDialog.setMessage("Uploading "+(int)progress+"%");
                }
            });
        }else{
            key = dbs.push().getKey();
            final String text = text_posting.getText().toString();
            final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
            final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User usr = dataSnapshot.getValue(User.class);
                    submitPosting(new Post(usr, key, text, "0",0,0,System.currentTimeMillis() ));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void addToMyPostList(String postId) {
        FirebaseUtils.getPostRef().child(postId)
                .setValue(mPost);
        FirebaseUtils.getMyPostRef().child(postId).setValue(true)
                .addOnCompleteListener((Activity) getApplicationContext(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialog.dismiss();
                    }
                });

        FirebaseUtils.addToMyRecord(Constants.POST_KEY, postId);
    }
    private void displayLoader() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sending post...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        int d = R.drawable.custom_progress_dialog;
        Drawable drawable = getResources().getDrawable(d);
        pDialog.setIndeterminateDrawable(drawable);
        pDialog.show();
    }

    private void submitPosting(Post barang) {
        Log.d("lol", "submitBarang: disini");
        dbs.child(key).setValue(barang).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("lo", "berhasil");
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        Intent intent=new Intent(Posting.this,Dashboard.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }


}
