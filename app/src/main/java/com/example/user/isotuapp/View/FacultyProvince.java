package com.example.user.isotuapp.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.example.user.isotuapp.Controller.FindingNearbyAdapter;
import com.example.user.isotuapp.Controller.SearchUserAdapter;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FacultyProvince extends AppCompatActivity {


    private ArrayList<User> mData;
    private ArrayList<String> mDataId;
    private SearchUserAdapter mAdapter;
    Spinner spinnerText;
    RecyclerView recyclerView;
    FirebaseUser fuser;
    String type;
    String ref;
    String myid;
    DatabaseReference reference,myreference;
    private ActionMode mActionMode;

    private String[] fakultas = {
            "Fakultas Teknik Elektro",
            "Fakultas Informatika",
            "Fakultas Rekayasa Industri",
            "Fakultas Ekonomi dan Bisnis",
            "Fakultas Industri Kreatif",
            "Fakultas Ilmu Terapan",
            "Fakultas Komunikasi dan Bisnis"
    };

    private String[] Provinsi = {
            "Aceh",
            "Bali",
            "Banten",
            "Bengkulu",
            "Gorontalo",
            "Jakarta",
            "Jawa Tengah",
            "Jawa Timur",
            "Kalimantan Barat",
            "Kalimantan Timur",
            "Kepulauan Riau",
            "Lampung",
            "Maluku",
            "Sulawesi Tengah",
            "Sulawesi Tenggara",
            "Sulawesi Utara",
            "Sumatera Barat",
            "Sumatera Selatan",
            "Riau",
            "Yogyakarta"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_province);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cari Teman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(FacultyProvince.this, SearchFriendActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();


        spinnerText = (Spinner) findViewById(R.id.distance);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, fakultas);
        final ArrayAdapter<String> adapterprovinsi = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Provinsi);

        // mengeset Array Adapter tersebut ke Spinner
        if(type.equals("0")){
            spinnerText.setAdapter(adapter);
            ref = "fakultas";
        }else{
            spinnerText.setAdapter(adapterprovinsi);
            ref = "searchbyprovince";
        }



        recyclerView = (RecyclerView) findViewById(R.id.listfinded);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("user");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                            mData.add(user);

                    }
                    mAdapter = new SearchUserAdapter(getApplicationContext(), mData, mDataId,
                            new SearchUserAdapter.ClickHandler() {
                                @Override
                                public void onItemClick(int position) {
                                    if (mActionMode != null) {
                                        mAdapter.toggleSelection(mDataId.get(position));
                                        if (mAdapter.selectionCount() == 0)
                                            mActionMode.finish();
                                        else
                                            mActionMode.invalidate();
                                        return;
                                    }
                                    User pet = mData.get(position);
                                    Intent intent = new Intent(getApplicationContext(), FriendProfile.class);
                                    intent.putExtra("iduser",pet.getUid());
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


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




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
        Query query = FirebaseDatabase.getInstance().getReference("user").orderByChild(ref)
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                        mData.add(user);

                }
                mAdapter = new SearchUserAdapter(getApplicationContext(), mData, mDataId,
                        new SearchUserAdapter.ClickHandler() {
                            @Override
                            public void onItemClick(int position) {
                                if (mActionMode != null) {
                                    mAdapter.toggleSelection(mDataId.get(position));
                                    if (mAdapter.selectionCount() == 0)
                                        mActionMode.finish();
                                    else
                                        mActionMode.invalidate();
                                    return;
                                }
                                User pet = mData.get(position);
                                Intent intent = new Intent(getApplicationContext(), FriendProfile.class);
                                intent.putExtra("iduser",pet.getUid());
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
