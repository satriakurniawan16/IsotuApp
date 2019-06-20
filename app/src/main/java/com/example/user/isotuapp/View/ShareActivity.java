package com.example.user.isotuapp.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ShareActivity extends AppCompatActivity {

    TextView nameText,captionText;
    ImageView imagePost;
    EditText writeText;
    FloatingActionButton fab;
    ProgressDialog pDialog;
    String key;
    FirebaseAuth auth;
    String currentUserId,currentImageUser,currentFullname;
    FirebaseUser currentUser;
    String iduser,image,name,text,imageuser,idpost,timecreted;
    DatabaseReference reference,referenceUser,referenceUserPOST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        auth =FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        nameText = (TextView) findViewById(R.id.fullname_share);
        captionText = (TextView) findViewById(R.id.caption_post_share);
        imagePost = (ImageView) findViewById(R.id.image_post_share);
        writeText = (EditText) findViewById(R.id.write_text);
        fab = (FloatingActionButton) findViewById(R.id.btn_share_post);

        Intent intent = getIntent();
        image = intent.getStringExtra("imagepost");
        name = intent.getStringExtra("userpost");
        text = intent.getStringExtra("captionpost");
        imageuser = intent.getStringExtra("imageuserpost");
        idpost = intent.getStringExtra("idpost");
        iduser = intent.getStringExtra("iduser");
        timecreted = intent.getStringExtra("timecreated");
        Log.d("pantekgau", "onCreate: " + timecreted + image +name+text+imageuser+idpost);
        reference = FirebaseDatabase.getInstance().getReference("posting");
        referenceUser = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
        referenceUserPOST = FirebaseDatabase.getInstance().getReference("user").child(iduser);
        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                currentUserId = user.getUid();
                currentFullname = user.getFullname();
                currentImageUser = user.getImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        referenceUserPOST = FirebaseDatabase.getInstance().getReference("user").child(iduser);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = reference.push().getKey();
                final long lol = Long.parseLong(timecreted);
                referenceUserPOST.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User newuser = dataSnapshot.getValue(User.class);
                        submitPosting(new Post(newuser, key, image, text, "1",0, 0, 0, lol, idpost, currentUserId, currentImageUser, currentFullname, writeText.getText().toString(),System.currentTimeMillis()));
                    } 

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        if(!image.equals("")){
            Picasso.get().load(image).into(imagePost);
        }else{
            imagePost.setVisibility(View.GONE);
        }
        nameText.setText(name);
        captionText.setText(text);

    }

    private void submitPosting(Post barang) {
        displayLoader();
        Log.d("lol", "submitBarang: disini");
        reference.child(barang.getPostId()).setValue(barang).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("lo", "berhasil");
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        Intent intent = new Intent(ShareActivity.this,Dashboard.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Membagikan Unggahan...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        int d = R.drawable.custom_progress_dialog;
        Drawable drawable = getResources().getDrawable(d);
        pDialog.setIndeterminateDrawable(drawable);
        pDialog.show();
    }
    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public void onResume() {
        super.onResume();
        status("online");
    }

    private void status(final String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

}
