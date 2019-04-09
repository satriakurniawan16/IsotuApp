package com.example.user.isotuapp.View;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class ProdiActivity extends AppCompatActivity {

    private ArrayList<User> mData;
    private ArrayList<String> mDataId;
    private SearchUserAdapter mAdapter;
    Spinner spinnerText,spinnerProdi;
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

    private String[] jurusanfif = {
            "S1 Teknik Informatika",
            "S1 Teknik Informatika (International)",
            "S2 Teknik Informatika",
            "S1 Teknologi Informasi"
    };

    private String[] jurusanfte = {
            "S1 Teknik Telekomunikasi",
            "S1 Teknik Fisika",
            "S2 Teknik Telekomunikasi (International)",
            "S1 Sistem Komputer",
            "S1 Teknik Elektro",
            "S1 Teknik Elektro (International)",
            "S2 Teknik Telekomunikasi"
    };

    private String[] jurusanfeb = {
            "S1 International ICT Business",
            "S1 Akuntansi",
            "S1 MBTI",
            "S1 Manajemen"
    };

    private String[] jurusanfri = {
            "S1 Teknik Industri",
            "S1 Sistem Informasi",
            "S1 Teknik Industri (International)",
            "S2 Teknik Industri)"
    };

    private String[] jurusanfit = {
            "D3 Rekayasa Perangkat Lunak Aplikasi",
            "D3 Sistem Informasi",
            "D3 Sistem Informasi Akuntansi",
            "D3 Teknologi Telekomunikasi",
            "D3 Manajemen Pemasaran",
            "D3 Perhotelan",
            "D3 Teknik Komputer",
            "D4 Sistem Multimedia"
    };

    private String[] jurusanfik = {
            "S1 Desain Komunikasi Visual",
            "S1 Kriya dan Tekstil",
            "S1 Desain Komunikasi Visual (International)",
            "S1 Desain Interior",
            "S1 Creative Arts",

    };

    private String[] jurusanfkb = {
            "S1 Administrasi Bisnis",
            "S1 Ilmu Komunikasi",
            "S1 Administrasi Bisnis (International)",
            "S1 Digital Public Relations",
            "S1 Ilmu Komunikasi (International)",

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cari Teman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(ProdiActivity.this, SearchFriendActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        spinnerText = (Spinner) findViewById(R.id.distance);
        spinnerProdi = (Spinner) findViewById(R.id.prodi);

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        
        spinnerText = (Spinner) findViewById(R.id.distance);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, fakultas);
        spinnerText.setAdapter(adapter);

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

        final ArrayAdapter<String> adapterfit = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfit);

        final ArrayAdapter<String> adapterfik = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfik);

        final ArrayAdapter<String> adapterfkb = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfkb);

        final ArrayAdapter<String> adapterfeb = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfeb);

        final ArrayAdapter<String> adapterfri = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfri);

        final ArrayAdapter<String> adapterfte = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfte);

        final ArrayAdapter<String> adapterfif = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfif);

        spinnerText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                switch (i){
                    case 0 :
                        jurusan(adapterfte);
                        break;
                    case 1 :
                        jurusan(adapterfif);
                        break;
                    case 2 :
                        jurusan(adapterfri);
                        break;
                    case 3 :
                        jurusan(adapterfeb);
                        break;
                    case 4 :
                        jurusan(adapterfik);
                        break;
                    case 5 :
                        jurusan(adapterfit);
                        break;
                    case 6 :
                        jurusan(adapterfkb);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void searchUsers(String s) {
        mData.clear();
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("user").orderByChild("jurusan")
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

    public void jurusan (final ArrayAdapter<String> adapterjurusan ){
        spinnerProdi.setAdapter(adapterjurusan);
        spinnerProdi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                 searchUsers(spinnerProdi.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initdropdown() {



    }
}
