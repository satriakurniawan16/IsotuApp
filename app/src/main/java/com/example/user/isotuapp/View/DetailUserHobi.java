package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.HobiAdapter;
import com.example.user.isotuapp.Controller.HobiUserAdapter;
import com.example.user.isotuapp.Controller.OrganisasiAdapter;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.Organiasasi;
import com.example.user.isotuapp.Model.UserHobi;
import com.example.user.isotuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DetailUserHobi extends AppCompatActivity {

    private ArrayList<UserHobi> mData;
    private ArrayList<String> mDataId;
    private HobiUserAdapter mAdapter;
    private String link;
    FirebaseUser currentUser;
    private DatabaseReference database;
    private ActionMode mActionMode;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(UserHobi.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(UserHobi.class));
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
        setContentView(R.layout.activity_detail_user_hobi);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();

        Intent intent = getIntent();
        link = intent.getStringExtra("reference");
        database = FirebaseDatabase.getInstance().getReference(link);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listUser);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        database.addChildEventListener(childEventListener);

        mAdapter = new HobiUserAdapter(getApplicationContext(), mData, mDataId,
                new HobiUserAdapter.ClickHandler() {
                    @Override
                    public void onItemClick(int position) {

//                        HobiModel pet = mData.get(position);
//                        Intent intent = new Intent(getContext(), DetailUserHobi.class);
//                        intent.putExtra("reference",pet.getHobi());
//                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        UserHobi pet = mData.get(position);
                        final String editKey = pet.getIduser();



                        return true;
                    }
                });
        recyclerView.setAdapter(mAdapter);

    }
}
