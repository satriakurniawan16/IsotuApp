package com.example.user.isotuapp.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.HobiUserAdapter;
import com.example.user.isotuapp.Controller.SearchUserAdapter;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Model.UserHobi;
import com.example.user.isotuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HobiOrganisasi extends AppCompatActivity {


    private String[] daftarhobi = {
            "Membaca",
            "Menyanyi",
            "Menari",
            "Olahraga",
            "Futsal",
            "Sepak bola",
            "Beladiri",
            "Menulis",
            "Programming",
            "Badminton",
            "Lari"
    };

    private String[] organisasispinner = {
            "BEM Fakultas Ilmu Terapan",
            "BEM Telkom University",
            "HIMADIF",
            "ALfath FIT",
            "UKM Daerah"
    };

    String child;
    private ArrayList<UserHobi> mData;
    private ArrayList<String> mDataId;
    private HobiUserAdapter mAdapter;
    Spinner spinnerText;
    RecyclerView recyclerView;
    FirebaseUser fuser;
    String type;
    String ref;
    String myid;
    DatabaseReference reference,myreference;
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
        setContentView(R.layout.activity_hobi_organisasi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cari Teman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(HobiOrganisasi.this, SearchFriendActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();


        spinnerText = (Spinner) findViewById(R.id.distance);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, daftarhobi);
        final ArrayAdapter<String> adapterprovinsi = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, organisasispinner);

        // mengeset Array Adapter tersebut ke Spinner
        if(type.equals("0")){
            spinnerText.setAdapter(adapter);
            ref = "list_hobi_user";
        }else{
            spinnerText.setAdapter(adapterprovinsi);
            ref = "list_user_organisasi";
        }



        recyclerView = (RecyclerView) findViewById(R.id.listfinded);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();



        spinnerText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                searchUsers(spinnerText.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void searchUsers(String s) {
        mData.clear();
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference query = FirebaseDatabase.getInstance().getReference(ref).child(s);
        query.addChildEventListener(childEventListener);
        mAdapter = new HobiUserAdapter(getApplicationContext(), mData, mDataId,
                new HobiUserAdapter.ClickHandler() {
                    @Override
                    public void onItemClick(int position) {

                        UserHobi pet = mData.get(position);
                        Intent intent = new Intent(HobiOrganisasi.this, FriendProfile.class);
                        intent.putExtra("iduser",pet.getIduser());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        return true;
                    }
                });
        recyclerView.setAdapter(mAdapter);

    }
}