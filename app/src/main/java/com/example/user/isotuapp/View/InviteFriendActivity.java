package com.example.user.isotuapp.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.InviteAdapter;
import com.example.user.isotuapp.Controller.ShareAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class InviteFriendActivity extends AppCompatActivity {

    DatabaseReference databasecontact;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    EditText searchUser;
    private ArrayList<Contact> mData;
    private ArrayList<String> mDataId;
    private InviteAdapter mAdapter;
    String idgrup,namagrup;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Contact.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Contact.class));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mDataId.remove(pos);
            mData.remove(pos);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        searchUser = (EditText) findViewById(R.id.searchuser_topost);

        Intent intent = getIntent();
        idgrup = intent.getStringExtra("idgrup");
        namagrup =  intent.getStringExtra("namagrup");
        recyclerView = findViewById(R.id.listusertoinvite);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(firebaseUser.getUid()).child("contactadded");
        databasecontact.addChildEventListener(childEventListener);
        mAdapter = new InviteAdapter(InviteFriendActivity.this, mData , mDataId,idgrup,namagrup);
        recyclerView.setAdapter(mAdapter);

    }

    private void searchUsers(String s) {
        mData.clear();
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("contact").child(firebaseUser.getUid()).child("contactadded").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        if(s.equals("")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("contact").child(firebaseUser.getUid()).child("contactadded");
            database.addChildEventListener(childEventListener);
        }else{
            query.addChildEventListener(childEventListener);
        }
        mAdapter = new InviteAdapter(InviteFriendActivity.this, mData , mDataId,idgrup,namagrup);
        recyclerView.setAdapter(mAdapter);
    }



}
