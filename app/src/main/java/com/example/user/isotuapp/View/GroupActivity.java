package com.example.user.isotuapp.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Model.Event;
import com.example.user.isotuapp.Model.Grup;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseExceptionMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.UUID;

public class GroupActivity extends AppCompatActivity {

    ImageView groupImage;
    EditText nameGroup;
    FloatingActionButton toInviteFriend;
    LinearLayout uploadImage;
    ProgressDialog pDialog;

    private static int IMG_CAMERA = 2;
    private Uri filepath;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    DatabaseReference dbs;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Grup Baru");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(GroupActivity.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        /* takePhoto.setVisibility(View.GONE);*/
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        groupImage = findViewById(R.id.group_image);
        nameGroup = findViewById(R.id.group_name);
        toInviteFriend = findViewById(R.id.to_invite);
        uploadImage = findViewById(R.id.upload_image);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dbs = FirebaseDatabase.getInstance().getReference("group");


        toInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPost();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(GroupActivity.this);
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
                    groupImage.setImageURI(filepath);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
            else if (requestCode == IMG_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                groupImage.setImageBitmap(thumbnail);
            }
        }catch (Exception e){
            Log.d("ClaimProduct", "onActivityResult: "+e.toString());
            Toast.makeText(GroupActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
        }
    }

    private void sendPost() {
        displayLoader();
        final StorageReference ref = storageReference.child("grup/"+ UUID.randomUUID().toString());
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
                            final String namegroupString = nameGroup.getText().toString();
                            final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                            final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
                                    submitGrup(new Grup(key, urlGambar, namegroupString));
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pDialog.dismiss();
                    Toast.makeText(GroupActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            final String namegroupString = nameGroup.getText().toString();
            submitGrup(new Grup(key,"https://firebasestorage.googleapis.com/v0/b/isocialfinal.appspot.com/o/walpaper.jpg?alt=media&token=bdbe37e3-cff6-4f80-a3c8-baf75e6e9465", namegroupString));
        }
    }

    private void submitGrup(final Grup barang) {
        Log.d("lol", "submitBarang: disini");
        dbs.child(currentUser.getUid()).child(key).setValue(barang);
        dbs.child(key).setValue(barang).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("lo", "berhasil");
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                DatabaseReference dbmember = FirebaseDatabase.getInstance().getReference("groupmember").child(barang.getIdgrup());
                                final HashMap<String, Object> member= new HashMap<>();
                                member.put("iduser",user.getUid());
                                member.put("fotoprofil",user.getImage());
                                member.put("namaprofil", user.getFullname());
                                dbmember.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(member);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        FirebaseMessaging.getInstance().subscribeToTopic(barang.getIdgrup());
                        Intent intent=new Intent(GroupActivity.this,InviteFriendActivity.class);
                        intent.putExtra("idgrup",barang.getIdgrup());
                        intent.putExtra("namagrup",barang.getNamagrup());
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Membuat grup");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        int d = R.drawable.custom_progress_dialog;
        Drawable drawable = getResources().getDrawable(d);
        pDialog.setIndeterminateDrawable(drawable);
        pDialog.show();
    }
}
