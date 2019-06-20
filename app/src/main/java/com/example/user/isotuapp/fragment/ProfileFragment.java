package com.example.user.isotuapp.fragment;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.HobiAdapter;
import com.example.user.isotuapp.Controller.OrganisasiAdapter;
import com.example.user.isotuapp.Controller.PostAdapter;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.Organiasasi;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Model.UserHobi;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.DetailUserHobi;
import com.example.user.isotuapp.View.EditProfil;
import com.example.user.isotuapp.View.LoginScreen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Line;
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
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class ProfileFragment extends Fragment {

    ImageView fotoProfile;
    TextView namaProfile, emailProfile, nimProfile,FakultasProfile,ProdiProfile,noHpProfile,AsalProfile;
    View mRootView;
    Spinner hobiField;
    LinearLayout edit,addhobibtn,addorganisasibtn,addhobi;
    int posfakultas,postjurusan,posprovinsi;
    private ArrayList<HobiModel> mData;
    private ArrayList<String> mDataId;
    private HobiAdapter mAdapter;
    boolean status = false ;
    private LinearLayout see_detai_profile;

    private ArrayList<Organiasasi> mDataOrganisasi;
    private ArrayList<String> mDataIdOrganisasi;
    private OrganisasiAdapter mAdapterOrganisasi;

    Button logout;

    private ActionMode mActionMode;
    FirebaseUser currentUser;
    HobiModel hobi;
    int poshobi,posorganisasi;
    String key ,keyorganiasasi;
    private DatabaseReference database,databaseuserhobi,databaseorganiasi;
    String Image;
    private FirebaseAuth mFirebaseAuth;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private RecyclerView recyclerView_story;

    private List<String> followingList;


    ProgressBar progress_circular;
    LinearLayout emptyview;


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

        recyclerView = mRootView.findViewById(R.id.listPosting);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        emptyview = mRootView.findViewById(R.id.emptyview);

        progress_circular = mRootView.findViewById(R.id.progress_circular);

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
        final LinearLayout profile_detail = mRootView.findViewById(R.id.profile_detail);
        see_detai_profile = mRootView.findViewById(R.id.see_detail_prfile);
        see_detai_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status == false){
                    profile_detail.setVisibility(View.VISIBLE);
                    ImageView image = mRootView.findViewById(R.id.imagearrow);
                    image.setImageResource(R.mipmap.downarrow);
                    LinearLayout background = mRootView.findViewById(R.id.background);
                    background.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    status = true;
                }else{
                    profile_detail.setVisibility(View.GONE);
                    ImageView image = mRootView.findViewById(R.id.imagearrow);
                    image.setImageResource(R.mipmap.rightarrow);
                    LinearLayout background = mRootView.findViewById(R.id.background);
                    background.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    status = false;
                }
            }
        });

        readPosts();

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
                        intent.putExtra("title","Pengguna dengan hobi yang sama");
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        final HobiModel pet = mData.get(position);
                        final String editKey = pet.getIdHobi();

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

                        View mView = getActivity().getLayoutInflater().inflate(R.layout.edithobi_modal,
                                null);


                        mBuilder.setView(mView);
                        final AlertDialog dialognya = mBuilder.create();
                        dialognya.show();

                        final Spinner textEdit = (Spinner) mView.findViewById(R.id.edithobieditext);
                        final ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_dropdown_item, daftarhobi);
                        textEdit.setAdapter(adapter);
                        textEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                poshobi = position;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        LinearLayout edit = (LinearLayout) mView.findViewById(R.id.saveedit);
                        LinearLayout hapus = (LinearLayout) mView.findViewById(R.id.deletehobi);
                        textEdit.setSelection(pet.getPos());
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.child(editKey).setValue(new HobiModel(
                                                editKey,
                                                textEdit.getSelectedItem().toString(),poshobi
                                        )
                                );

                                databaseuserhobi = FirebaseDatabase.getInstance().getReference("list_hobi_user").child(pet.getHobi());
                                final DatabaseReference databaseuserhobinew = FirebaseDatabase.getInstance().getReference("list_hobi_user").child(textEdit.getSelectedItem().toString());
                                final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
                                dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User usr = dataSnapshot.getValue(User.class);
                                        final HashMap<String, Object> hobiuser= new HashMap<>();
                                        hobiuser.put("iduser",currentUser.getUid());
                                        hobiuser.put("fotoprofil",usr.getImage());
                                        hobiuser.put("namaprofil", usr.getFullname());
                                        databaseuserhobinew.child(currentUser.getUid()).setValue(hobiuser);
                                        if(!textEdit.getSelectedItem().toString().equals(pet.getHobi())) {
                                            databaseuserhobi.child(currentUser.getUid()).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


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
                                                final DatabaseReference databaseuserhobinew = FirebaseDatabase.getInstance().getReference("list_hobi_user").child(pet.getHobi());
                                                databaseuserhobinew.child(currentUser.getUid()).removeValue();
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
                        intent.putExtra("title","Pengguna dengan organisasi yang sama");
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        final Organiasasi pet = mDataOrganisasi.get(position);
                        final String editKey = pet.getId();
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.editorganisasi_modal,
                                null);


                        mBuilder.setView(mView);
                        final AlertDialog dialognya = mBuilder.create();
                        dialognya.show();

                        final Spinner textEdit = (Spinner) mView.findViewById(R.id.edithobieditext);
                        final ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_dropdown_item, organisasispinner);
                        textEdit.setAdapter(adapter);
                        textEdit.setSelection(pet.getPos());
                        textEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                posorganisasi = position ;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        LinearLayout edit = (LinearLayout) mView.findViewById(R.id.saveedit);
                        LinearLayout hapus = (LinearLayout) mView.findViewById(R.id.deletehobi);


                        textEdit.setSelection(pet.getPos());
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseorganiasi.child(editKey).setValue(new Organiasasi(
                                                editKey,
                                                textEdit.getSelectedItem().toString(),posorganisasi
                                        )
                                );
                                final DatabaseReference databaseuserorganisasi= FirebaseDatabase.getInstance().getReference("organisasi").child(pet.getNama());
                                final DatabaseReference databaseuserorganisasinew = FirebaseDatabase.getInstance().getReference("organisasi").child(textEdit.getSelectedItem().toString());
                                final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
                                dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User usr = dataSnapshot.getValue(User.class);
                                        final HashMap<String, Object> hobiuser= new HashMap<>();
                                        hobiuser.put("iduser",currentUser.getUid());
                                        hobiuser.put("fotoprofil",usr.getImage());
                                        hobiuser.put("namaprofil", usr.getFullname());
                                        databaseuserorganisasinew.child(currentUser.getUid()).setValue(hobiuser);
                                        if(!textEdit.getSelectedItem().toString().equals(pet.getNama())) {
                                            databaseuserorganisasi.child(currentUser.getUid()).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


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
                                                final DatabaseReference databaseuserorganisasinew = FirebaseDatabase.getInstance().getReference("organisasi").child(pet.getNama());
                                                databaseuserorganisasinew.child(currentUser.getUid()).removeValue();
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
                String a = AsalProfile.getText().toString();
                String output = a.substring(a.indexOf(',') +1);
                intent.putExtra("asal",output);
                intent.putExtra("posfakultas",posfakultas);
                intent.putExtra("posjurusan",postjurusan);
                intent.putExtra("posprovinsi",posprovinsi);
                startActivity(intent);
            }
        });
        addhobibtn = (LinearLayout) mRootView.findViewById(R.id.tambahhobi);

        addorganisasibtn = (LinearLayout) mRootView.findViewById(R.id.tambahorganisasi);

        addorganisasibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());

                View mView = getActivity().getLayoutInflater().inflate(R.layout.organisasi_modal,
                        null);
                final Spinner organisasiField = (Spinner) mView.findViewById(R.id.organisasieditext);
                addhobi = (LinearLayout) mView.findViewById(R.id.addhobi);

                final ArrayAdapter<String> adapterorganisasi = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, organisasispinner);
                organisasiField.setAdapter(adapterorganisasi);
                organisasiField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        posorganisasi = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                addhobi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = databaseorganiasi.push().getKey();
                            submitOrganisasi(new Organiasasi(key,organisasiField.getSelectedItem().toString(),posorganisasi));
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

                hobiField = (Spinner) mView.findViewById(R.id.hobieditext);
                addhobi = (LinearLayout) mView.findViewById(R.id.addhobi);


                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, daftarhobi);
                hobiField.setAdapter(adapter);
                hobiField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    poshobi = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                addhobi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      key = database.push().getKey();
                            submitHobi(new HobiModel(key, hobiField.getSelectedItem().toString(),poshobi));
                            dialog.hide();
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
                    status("offline");
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
                    FakultasProfile.setText(usr.getJurusan());
                    ProdiProfile.setText(usr.getFakultas());
                    noHpProfile.setText(usr.getNohp());
                    AsalProfile.setText(usr.getAsal());
                    Image = usr.getImage();
                    posfakultas = usr.getPositionfakultas();
                    postjurusan = usr.getPositionjurusan();
                    posprovinsi = usr.getPositionprovinsi();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posting");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                        if(post.getType().equals("1")){
                            if (post.getIduser().equals(currentUser.getUid()) ) {
                                postList.add(post);
                            }
                        }else if(post.getType().equals("0")){
                            if (post.getUser().getUid().equals(currentUser.getUid()) ) {
                                postList.add(post);
                            }
                        }
                }
                Log.d("duplicatedata", "onDataChange: " + postList);
                if(postList.size() == 0 ){
                    emptyview.setVisibility(View.VISIBLE);
                }else {
                    emptyview.setVisibility(View.GONE);
                }
                postAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        databaseorganiasi.removeEventListener(childEventListenerOrgnanisasi);
        database.removeEventListener(childEventListener);
    }


    private void status(final String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }


}
