package com.example.user.isotuapp.View;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.user.isotuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditPost extends AppCompatActivity {

    EditText EditCaption;
    FloatingActionButton fab;
    String idpost,textpost,type;
    FirebaseAuth aut ;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(EditPost.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        aut = FirebaseAuth.getInstance();
        mUser = aut.getCurrentUser();
        EditCaption = (EditText) findViewById(R.id.edittextPosting);
        fab = (FloatingActionButton) findViewById(R.id.sentPost);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        idpost = intent.getStringExtra("idpost");

        if(type.equals("0")){
            textpost = intent.getStringExtra("textpost");
        }else{
            textpost = intent.getStringExtra("textshare");
        }

        EditCaption.setText(textpost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("0")){
                    editpost();
                }else{
                    editpostshare();
                }
            }
        });
    }

    public void editpost (){
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("posting").child(idpost);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("text", EditCaption.getText().toString());
        dbf.updateChildren(hashMap);
        Intent intent = new Intent(EditPost.this,Dashboard.class);
        startActivity(intent);
    }

    public void editpostshare (){
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("posting").child(idpost);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("captionshare", EditCaption.getText().toString());
        dbf.updateChildren(hashMap);
        Intent intent = new Intent(EditPost.this,Dashboard.class);
        startActivity(intent);
    }
}
