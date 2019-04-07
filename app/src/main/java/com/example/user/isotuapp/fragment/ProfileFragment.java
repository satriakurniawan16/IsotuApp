package com.example.user.isotuapp.fragment;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.HobiAdapter;
import com.example.user.isotuapp.Controller.OrganisasiAdapter;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.Organiasasi;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Model.UserHobi;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.DetailUserHobi;
import com.example.user.isotuapp.View.EditProfil;
import com.example.user.isotuapp.View.LoginScreen;
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
import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class ProfileFragment extends Fragment {

    ImageView fotoProfile;
    TextView namaProfile, emailProfile, nimProfile,FakultasProfile,ProdiProfile,noHpProfile,AsalProfile;
    View mRootView;
    EditText hobiField;
    LinearLayout edit,addhobibtn,addorganisasibtn,addhobi;

    private ArrayList<HobiModel> mData;
    private ArrayList<String> mDataId;
    private HobiAdapter mAdapter;

    private ArrayList<Organiasasi> mDataOrganisasi;
    private ArrayList<String> mDataIdOrganisasi;
    private OrganisasiAdapter mAdapterOrganisasi;

    Button logout;

    private ActionMode mActionMode;
    FirebaseUser currentUser;
    HobiModel hobi;
    String key ,keyorganiasasi;
    private DatabaseReference database,databaseuserhobi,databaseorganiasi;
    String Image;
    private FirebaseAuth mFirebaseAuth;

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
            Toast.makeText(getContext(), "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        mDataOrganisasi = new ArrayList<>();
        mDataIdOrganisasi = new ArrayList<>();




        edit = (LinearLayout) mRootView.findViewById(R.id.editprofile);
        namaProfile  = (TextView) mRootView.findViewById(R.id.namaprofil);
        emailProfile = (TextView) mRootView.findViewById(R.id.email);
        nimProfile= (TextView) mRootView.findViewById(R.id.nim);
        FakultasProfile = (TextView) mRootView.findViewById(R.id.fakultas);
        ProdiProfile = (TextView) mRootView.findViewById(R.id.jurusan);
        noHpProfile = (TextView) mRootView.findViewById(R.id.nohp);
        AsalProfile = (TextView) mRootView.findViewById(R.id.asal);
        fotoProfile = (ImageView) mRootView.findViewById(R.id.imageprofil);
        logout = (Button) mRootView.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        RecyclerView recyclerView = mRootView.findViewById(R.id.listhobi);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        RecyclerView recyclerViewOrganisasi = mRootView.findViewById(R.id.listorganisasi);
        recyclerViewOrganisasi.setHasFixedSize(true);
        LinearLayoutManager layoutManager2 =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewOrganisasi.setLayoutManager(layoutManager2);


        loadProfile();


        database = FirebaseDatabase.getInstance().getReference("hobi").child(currentUser.getUid());
        databaseorganiasi = FirebaseDatabase.getInstance().getReference("organisasi").child(currentUser.getUid());
        database.addChildEventListener(childEventListener);
        databaseorganiasi.addChildEventListener(childEventListenerOrgnanisasi);



        mAdapter = new HobiAdapter(getContext(), mData, mDataId,
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
                        Intent intent = new Intent(getContext(), DetailUserHobi.class);
                        intent.putExtra("reference","list_hobi_user");
                        intent.putExtra("child",pet.getHobi());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        HobiModel pet = mData.get(position);
                        final String editKey = pet.getIdHobi();

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

                        View mView = getActivity().getLayoutInflater().inflate(R.layout.edithobi_modal,
                                null);


                        mBuilder.setView(mView);
                        final AlertDialog dialognya = mBuilder.create();
                        dialognya.show();

                        final EditText textEdit = (EditText) mView.findViewById(R.id.edithobieditext);
                        LinearLayout edit = (LinearLayout) mView.findViewById(R.id.saveedit);
                        LinearLayout hapus = (LinearLayout) mView.findViewById(R.id.deletehobi);
                        textEdit.setText(pet.getHobi());
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.child(editKey).setValue(new HobiModel(
                                                editKey,
                                                textEdit.getText().toString()
                                        )
                                );
                                Toast.makeText(getContext(), "Berhasil disimpan", Toast.LENGTH_SHORT).show();
                                dialognya.hide();
                            }
                        });

                        hapus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                        .setMessage("Apakah anda yakin untuk menghapus ?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    database.child(editKey).removeValue();
                                                    dialognya.hide();
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

                        return true;
                    }
                });


        mAdapterOrganisasi = new OrganisasiAdapter(getContext(), mDataOrganisasi, mDataIdOrganisasi,
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
                        Intent intent = new Intent(getContext(), DetailUserHobi.class);
                        intent.putExtra("reference","list_user_organisasi");
                        intent.putExtra("child",pet.getNama());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        Organiasasi pet = mDataOrganisasi.get(position);
                        final String editKey = pet.getId();
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.edithobi_modal,
                                null);


                        mBuilder.setView(mView);
                        final AlertDialog dialognya = mBuilder.create();
                        dialognya.show();

                        final EditText textEdit = (EditText) mView.findViewById(R.id.edithobieditext);
                        LinearLayout edit = (LinearLayout) mView.findViewById(R.id.saveedit);
                        LinearLayout hapus = (LinearLayout) mView.findViewById(R.id.deletehobi);
                        textEdit.setText(pet.getNama());
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseorganiasi.child(editKey).setValue(new Organiasasi(
                                                editKey,
                                                textEdit.getText().toString()
                                        )
                                );
                                Toast.makeText(getContext(), "Berhasil disimpan", Toast.LENGTH_SHORT).show();
                                dialognya.hide();
                            }
                        });

                        hapus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                        .setMessage("Apakah anda yakin untuk menghapus ?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                    databaseorganiasi.child(editKey).removeValue();
                                                    dialognya.hide();
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

                        return true;
                    }
                });

        recyclerViewOrganisasi.setAdapter(mAdapterOrganisasi);
        recyclerView.setAdapter(mAdapter);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EditProfil.class);
                intent.putExtra("foto",Image);
                intent.putExtra("nama",namaProfile.getText().toString());
                intent.putExtra("email",emailProfile.getText().toString());
                intent.putExtra("nim",nimProfile.getText().toString());
                intent.putExtra("fakultas",FakultasProfile.getText().toString());
                intent.putExtra("jurusan",ProdiProfile.getText().toString());
                intent.putExtra("nohp",noHpProfile.getText().toString());
                intent.putExtra("asal",AsalProfile.getText().toString());
                startActivity(intent);
            }
        });
        addhobibtn = (LinearLayout) mRootView.findViewById(R.id.tambahhobi);

        addorganisasibtn = (LinearLayout) mRootView.findViewById(R.id.tambahorganisasi);

        addorganisasibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());

                View mView = getActivity().getLayoutInflater().inflate(R.layout.hobi_modal,
                        null);

                hobiField = (EditText) mView.findViewById(R.id.hobieditext);
                addhobi = (LinearLayout) mView.findViewById(R.id.addhobi);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                addhobi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = databaseorganiasi.push().getKey();
                        if(!isEmpty(hobiField.getText().toString()) ){
                            submitOrganisasi(new Organiasasi(key, hobiField.getText().toString()));
                            dialog.hide();
                        }else{
                            Toast.makeText(getContext(),
                                    "Form tidak boleh kosong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        addhobibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());

                View mView = getActivity().getLayoutInflater().inflate(R.layout.hobi_modal,
                        null);

                hobiField = (EditText) mView.findViewById(R.id.hobieditext);
                addhobi = (LinearLayout) mView.findViewById(R.id.addhobi);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                addhobi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      key = database.push().getKey();
                        if(!isEmpty(hobiField.getText().toString()) ){
                            submitHobi(new HobiModel(key, hobiField.getText().toString()));
                            dialog.hide();
                        }else{
                            Toast.makeText(getContext(),
                                    "Form tidak boleh kosong", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        return mRootView;
    }

    private void submitHobi(HobiModel word) {
        database.child(key).setValue(word).
                addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                    }
                });
        databaseuserhobi = FirebaseDatabase.getInstance().getReference("list_hobi_user").child(word.getHobi());
        final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);
                final HashMap<String, Object> hobiuser= new HashMap<>();
                hobiuser.put("iduser",currentUser.getUid());
                hobiuser.put("fotoprofil",usr.getImage());
                hobiuser.put("namaprofil", usr.getFullname());
                databaseuserhobi.child(currentUser.getUid()).setValue(hobiuser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void submitOrganisasi(Organiasasi word) {
        keyorganiasasi = databaseorganiasi.push().getKey();
        databaseorganiasi.child(key).setValue(word).
                addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                    }
                });
        databaseuserhobi = FirebaseDatabase.getInstance().getReference("list_user_organisasi").child(word.getNama());
        final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);
                final HashMap<String, Object> hobiuser= new HashMap<>();
                hobiuser.put("iduser",currentUser.getUid());
                hobiuser.put("fotoprofil",usr.getImage());
                hobiuser.put("namaprofil", usr.getFullname());
                databaseuserhobi.child(currentUser.getUid()).setValue(hobiuser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void goLogInScreen() {
        startActivity(new Intent(getContext(), LoginScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void logOut() {
                    FirebaseAuth.getInstance().signOut();
                    goLogInScreen();
    }


    private void loadProfile() {

        final DatabaseReference dbprofile = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
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
                    Image = usr.getImage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


}
