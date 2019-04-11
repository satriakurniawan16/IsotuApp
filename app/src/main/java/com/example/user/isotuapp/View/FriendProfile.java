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
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.HobiAdapter;
import com.example.user.isotuapp.Controller.OrganisasiAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.Organiasasi;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendProfile extends AppCompatActivity {

    ImageView fotoProfile;
    TextView namaProfile, emailProfile, nimProfile,FakultasProfile,ProdiProfile,noHpProfile,AsalProfile;
    View mRootView;
    private ActionMode mActionMode;
    FirebaseUser currentUser;
    HobiModel hobi;
    String key ,keyorganiasasi;
    private DatabaseReference database,databaseorganiasi,databasecontact;
    String Image;
    Button addFriend;
    private FirebaseAuth mFirebaseAuth;
    LinearLayout hapusContact;
    private ArrayList<HobiModel> mData;
    private ArrayList<String> mDataId;
    private HobiAdapter mAdapter;

    private ArrayList<Organiasasi> mDataOrganisasi;
    private ArrayList<String> mDataIdOrganisasi;
    private OrganisasiAdapter mAdapterOrganisasi;
    String iduser;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(HobiModel.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(HobiModel.class));
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

    private ChildEventListener childEventListenerOrgnanisasi = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mDataOrganisasi.add(dataSnapshot.getValue(Organiasasi.class));
            mDataIdOrganisasi.add(dataSnapshot.getKey());
            mAdapterOrganisasi.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataIdOrganisasi.indexOf(dataSnapshot.getKey());
            mDataOrganisasi.set(pos, dataSnapshot.getValue(Organiasasi.class));
            mAdapterOrganisasi.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataIdOrganisasi.indexOf(dataSnapshot.getKey());
            mDataIdOrganisasi.remove(pos);
            mDataOrganisasi.remove(pos);
            mAdapterOrganisasi.notifyDataSetChanged();
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
        setContentView(R.layout.activity_friend_profile);


        Intent intent = getIntent();
        iduser = intent.getStringExtra("iduser") ;

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        mDataOrganisasi = new ArrayList<>();
        mDataIdOrganisasi = new ArrayList<>();

        namaProfile  = (TextView)findViewById(R.id.friendnamaprofil);
        emailProfile = (TextView) findViewById(R.id.friendemail);
        nimProfile= (TextView) findViewById(R.id.friendnim);
        FakultasProfile = (TextView) findViewById(R.id.friendfakultas);
        ProdiProfile = (TextView) findViewById(R.id.friendjurusan);
        noHpProfile = (TextView) findViewById(R.id.friendnohp);
        AsalProfile = (TextView) findViewById(R.id.friendasal);
        fotoProfile = (ImageView) findViewById(R.id.friendimageprofil);
        addFriend = (Button) findViewById(R.id.addfriend);
        hapusContact = (LinearLayout) findViewById(R.id.deleteContact);


        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference dbprofile = FirebaseDatabase.getInstance().getReference("user").child(iduser);
                dbprofile.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User usr = dataSnapshot.getValue(User.class);
                        addContact(new Contact(iduser,namaProfile.getText().toString(),usr.getImage(),ProdiProfile.getText().toString(),FakultasProfile.getText().toString(),usr.getSearch()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        RecyclerView recyclerView = findViewById(R.id.friendlisthobi);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        RecyclerView recyclerViewOrganisasi = findViewById(R.id.friendlistorganisasi);
        recyclerViewOrganisasi.setHasFixedSize(true);
        LinearLayoutManager layoutManager2 =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewOrganisasi.setLayoutManager(layoutManager2);


        loaddata();

        database = FirebaseDatabase.getInstance().getReference("hobi").child(iduser);
        databaseorganiasi = FirebaseDatabase.getInstance().getReference("organisasi").child(iduser);
        databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid());
        hapusContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendProfile.this)
                        .setMessage("Apakah anda yakin untuk menghapus kontak ini ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databasecontact.child(iduser).removeValue();
                                addFriend.setText("Tambakan sebagai teman +");
                                hapusContact.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });


        database.addChildEventListener(childEventListener);
        databaseorganiasi.addChildEventListener(childEventListenerOrgnanisasi);



        mAdapter = new HobiAdapter(getApplicationContext(), mData, mDataId,
                new HobiAdapter.ClickHandler() {
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
                        HobiModel pet = mData.get(position);
                        Intent intent = new Intent(getApplicationContext(), DetailUserHobi.class);
                        intent.putExtra("reference","list_hobi_user");
                        intent.putExtra("child",pet.getHobi());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        return true;
                    }
                });


        mAdapterOrganisasi = new OrganisasiAdapter(getApplicationContext(), mDataOrganisasi, mDataIdOrganisasi,
                new OrganisasiAdapter.ClickHandler() {
                    @Override
                    public void onItemClick(int position) {
                        if (mActionMode != null) {
                            mAdapterOrganisasi.toggleSelection(mDataId.get(position));
                            if (mAdapterOrganisasi.selectionCount() == 0)
                                mActionMode.finish();
                            else
                                mActionMode.invalidate();
                            return;
                        }
                        Organiasasi pet = mDataOrganisasi.get(position);
                        Intent intent = new Intent(FriendProfile.this, DetailUserHobi.class);
                        intent.putExtra("reference",pet.getNama());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        return true;
                    }
                });

        recyclerViewOrganisasi.setAdapter(mAdapterOrganisasi);
        recyclerView.setAdapter(mAdapter);


    }


    private void addContact(Contact contact) {
        databasecontact.child(iduser).setValue(contact).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                    }
                });
        addFriend.setText("Sudah berteman");
        hapusContact.setVisibility(View.VISIBLE);
    }

    private void loaddata() {
        databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid());
        databasecontact.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("singlestatus", "onDataChange: " + dataSnapshot);
                boolean status = false ;
                boolean mystatus = false ;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d("Allstatus", "onDataChange: " + ds);
                    if(iduser.equals(ds.getKey())){
                        status = true;
                        break;
                    }
                    if(iduser.equals(currentUser.getUid())){
                        mystatus = true;
                        break;
                    }
                }

                if(mystatus == true){
                    hapusContact.setVisibility(View.GONE);
                    addFriend.setVisibility(View.GONE);
                }
                if(status == true){
                    addFriend.setText("Sudah Berteman");
                    hapusContact.setVisibility(View.VISIBLE);
                }else{
                    hapusContact.setVisibility(View.GONE);
                    addFriend.setText("Tambahkan sebagai teman +");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference dbprofile = FirebaseDatabase.getInstance().getReference("user").child(iduser);
        dbprofile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);
                Picasso.get().load(usr.getImage()).fit()
                        .into(fotoProfile);
                namaProfile.setText(usr.getFullname());
                emailProfile.setText(usr.getEmail());
                nimProfile.setText(usr.getNim());
                FakultasProfile.setText(usr.getFakultas());
                ProdiProfile.setText(usr.getJurusan());
                noHpProfile.setText(usr.getNohp());
                AsalProfile.setText(usr.getAsal());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
