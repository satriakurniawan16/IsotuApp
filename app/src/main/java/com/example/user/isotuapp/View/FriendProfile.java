package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.HobiAdapter;
import com.example.user.isotuapp.Controller.OrganisasiAdapter;
import com.example.user.isotuapp.Controller.PostAdapter;
import com.example.user.isotuapp.Controller.ProfilePostAdapter;
import com.example.user.isotuapp.Controller.ShareAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.Organiasasi;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.APIService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendProfile extends AppCompatActivity {

    ImageView fotoProfile;
    TextView namaProfile, emailProfile, nimProfile,FakultasProfile,ProdiProfile,noHpProfile,AsalProfile;
    View mRootView;
    private ActionMode mActionMode;
    FirebaseUser currentUser;
    String idpost;
    APIService apiService;
    HobiModel hobi;
    String key ,keyorganiasasi;
    EditText searchUser;
    boolean statusButt = false;
    private DatabaseReference database,databaseorganiasi,databasecontact;
    String Image;
    Button addFriend;
    private LinearLayout see_detai_profile;
    private FirebaseAuth mFirebaseAuth;
    LinearLayout hapusContact;
    private ArrayList<HobiModel> mData;
    private ArrayList<String> mDataId;
    private HobiAdapter mAdapter;

    private RecyclerView recyclerView;
    private ProfilePostAdapter postAdapter;
    private List<Post> postList;
    ProgressBar progress_circular;
    LinearLayout emptyview;


    private SlidingUpPanelLayout mLayout;

    private ArrayList<Organiasasi> mDataOrganisasi;
    private ArrayList<String> mDataIdOrganisasi;
    private OrganisasiAdapter mAdapterOrganisasi;
    String iduser;
    View progressOverlay;


    private ArrayList<Contact> mDataShare;
    private ArrayList<String> mDataIdShare;
    private ShareAdapter mAdapterShare;
    private ChildEventListener childEventListenerShare = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mDataShare.add(dataSnapshot.getValue(Contact.class));
            mDataIdShare.add(dataSnapshot.getKey());
            mAdapterShare.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mDataShare.set(pos, dataSnapshot.getValue(Contact.class));
            mAdapterShare.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mDataIdShare.remove(pos);
            mDataShare.remove(pos);
            mAdapterShare.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
        }
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
        if(intent.getStringExtra("iduser") != null) {
            iduser = intent.getStringExtra("iduser");
        }else{
            iduser = intent.getStringExtra("id");
        }
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

        recyclerView = findViewById(R.id.listPosting);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new ProfilePostAdapter(FriendProfile.this, postList);
        recyclerView.setAdapter(postAdapter);
        emptyview = findViewById(R.id.emptyview);

        progress_circular = findViewById(R.id.progress_circular);

        final LinearLayout profile_detail = findViewById(R.id.profile_detail);
        see_detai_profile = findViewById(R.id.see_detail_prfile);
        see_detai_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusButt == false){
                    profile_detail.setVisibility(View.VISIBLE);
                    ImageView image = findViewById(R.id.imagearrow);
                    image.setImageResource(R.mipmap.downarrow);
                    LinearLayout background = findViewById(R.id.background);
                    background.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    statusButt = true;
                }else{
                    profile_detail.setVisibility(View.GONE);
                    ImageView image = findViewById(R.id.imagearrow);
                    image.setImageResource(R.mipmap.rightarrow);
                    LinearLayout background = findViewById(R.id.background);
                    background.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    statusButt = false;
                }
            }
        });

        readPosts();

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_share);
        progressOverlay = (FrameLayout) findViewById(R.id.progress_overlay);

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        searchUser = (EditText) findViewById(R.id.searchuser_topost);


        recyclerView = findViewById(R.id.listusertoshare);
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
        LinearLayoutManager mlayoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mlayoutManager);


        RecyclerView recyclerViewOrganisasi = findViewById(R.id.friendlistorganisasi);
        recyclerViewOrganisasi.setHasFixedSize(true);
        LinearLayoutManager layoutManager2 =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewOrganisasi.setLayoutManager(layoutManager2);


        loaddata();

        database = FirebaseDatabase.getInstance().getReference("hobi").child(iduser);
        databaseorganiasi = FirebaseDatabase.getInstance().getReference("organisasi").child(iduser);
        databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).child("contactadded");
        hapusContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendProfile.this)
                        .setMessage("Apakah anda yakin untuk menghapus kontak ini ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databasecontact.child(iduser).removeValue();
                                deleteNotifications(iduser,iduser);
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
                        Intent intent = new Intent(FriendProfile.this, DetailUserHobi.class);
                        intent.putExtra("reference","list_hobi_user");
                        intent.putExtra("child",pet.getHobi());
                        intent.putExtra("title","Pengguna dengan hobi yang sama");
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
                        intent.putExtra("reference","list_user_organisasi");
                        intent.putExtra("child",pet.getNama());
                        intent.putExtra("title","Pengguna dengan organisasi yang sama");
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


    private void addContact(final Contact contact) {
        databasecontact.child(iduser).setValue(contact).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        DatabaseReference dbuser =  FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                addNotification(contact.getUserid(),user.getFullname(),user.getFullname() + " Menambahkan anda sebagai teman");
                                sendNotifiaction(contact.getUserid(),user.getFullname(),"Menambahkan anda ke kontak",contact.getUserid(),user.getUid());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
        addFriend.setText("Sudah berteman");
        hapusContact.setVisibility(View.VISIBLE);
    }

    private void loaddata() {
        databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).child("contactadded");
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

                if(iduser.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                   addFriend.setVisibility(View.GONE);
                   hapusContact.setVisibility(View.GONE);
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

    private void addNotification(String userid, String postid,String text){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        FirebaseAuth authuser = FirebaseAuth.getInstance();
        FirebaseUser mUser = authuser.getCurrentUser();
        String key;
        key = reference.push().getKey();
        hashMap.put("id",key);
        hashMap.put("userid", mUser.getUid());
        hashMap.put("text", text);
        hashMap.put("postid", mUser.getUid());
        hashMap.put("date", System.currentTimeMillis());
        hashMap.put("ispost", true);
        hashMap.put("type", "1");
        reference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(FriendProfile.this, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FriendProfile.this, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNotifications(final String postid, final String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(FriendProfile.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(String receiver, final String username, final String message,final String userid,final String idpost){
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mauth.getCurrentUser();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+" "+message, username,
                            userid,"profile", fuser.getUid());

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(FriendProfile.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
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
                        if (post.getIduser().equals(iduser) ) {
                            postList.add(post);
                        }
                    }else if(post.getType().equals("0")){
                        if (post.getUser().getUid().equals(iduser) ) {
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

    private void searchUsers(String s) {
        mData.clear();
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        if (s.equals("")) {
            database = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid());
            database.addChildEventListener(childEventListener);
        } else {
            query.addChildEventListener(childEventListener);
        }
        mAdapterShare = new ShareAdapter(FriendProfile.this, mDataShare, mDataIdShare, "");
        recyclerView.setAdapter(mAdapter);
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
