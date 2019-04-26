package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Controller.SearchUserAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.APIService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFriendActivity extends AppCompatActivity {

    private ArrayList<User> mData;
    private ArrayList<String> mDataId;
    private SearchUserAdapter mAdapter;
    private EditText SearchUser;
    RecyclerView recyclerView;
    APIService apiService;

    private ActionMode mActionMode;
    FirebaseUser currentUser;
    private DatabaseReference database,databasecontact;
    private FirebaseAuth mFirebaseAuth;
    LinearLayout emptyView;
    LinearLayout toFindingFriend, tofindingFaculty, tofindingProvince, ToFindingMajor, toFindingHobi, toFindingOrganisasi;
    private BroadcastReceiver broadcastReceiver;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters


    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daftar pengguna");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(SearchFriendActivity.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();

        emptyView = (LinearLayout) findViewById(R.id.emptyview);
        toFindingFriend = (LinearLayout) findViewById(R.id.to_findingnearby);
        toFindingFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this, FindingFriend.class);
                startActivity(intent);
            }
        });
        tofindingFaculty = (LinearLayout) findViewById(R.id.to_findingfaculty);
        tofindingFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this, FacultyProvince.class);
                intent.putExtra("type", "0");
                startActivity(intent);
            }
        });
        ToFindingMajor = (LinearLayout) findViewById(R.id.to_findingmajor);
        ToFindingMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this, ProdiActivity.class);
                startActivity(intent);
            }
        });

        tofindingProvince = (LinearLayout) findViewById(R.id.to_findingaddress);
        tofindingProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this, FacultyProvince.class);
                intent.putExtra("type", "1");
                startActivity(intent);
            }
        });

        toFindingHobi = (LinearLayout) findViewById(R.id.to_findinghobbies);
        toFindingHobi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this, HobiOrganisasi.class);
                intent.putExtra("type", "0");
                startActivity(intent);
            }
        });

        toFindingOrganisasi = (LinearLayout) findViewById(R.id.to_findingorganization);
        toFindingOrganisasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this, HobiOrganisasi.class);
                intent.putExtra("type", "1");
                startActivity(intent);
            }
        });

        databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).child("contactadded");

        recyclerView = findViewById(R.id.listSearch);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        SearchUser = (EditText) findViewById(R.id.searchuser);
        SearchUser.addTextChangedListener(new TextWatcher() {
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

        database = FirebaseDatabase.getInstance().getReference("user");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (SearchUser.getText().toString().equals("")) {
                    mData.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (!user.getUid().equals(currentUser.getUid())) {
                            mData.add(user);
                        }

                        if(mData !=  null ) {
                            emptyView.setVisibility(View.GONE);
                        }else {
                            emptyView.setVisibility(View.VISIBLE);
                        }

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
                                    final User pet = mData.get(position);
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchFriendActivity.this);

                                    View mView = getLayoutInflater().inflate(R.layout.modal_user,
                                            null);

                                    ImageView profileImageView = (ImageView) mView.findViewById(R.id.profilepopup);
                                    TextView nameTextView = (TextView) mView.findViewById(R.id.nameuserpopup);
                                    TextView jurusanTextView = (TextView) mView.findViewById(R.id.jurusanpopup);
                                    LinearLayout profileLayout = (LinearLayout) mView.findViewById(R.id.profilebutton);
                                    LinearLayout chatLayout = (LinearLayout) mView.findViewById(R.id.chatbutton);
                                    final LinearLayout addLayout = (LinearLayout) mView.findViewById(R.id.addbutton);

                                    Picasso.get().load(pet.getImage()).into(profileImageView);
                                    nameTextView.setText(pet.getFullname());
                                    jurusanTextView.setText(pet.getJurusan());
                                    profileLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(SearchFriendActivity.this,FriendProfile.class);
                                            intent.putExtra("iduser",pet.getUid());
                                            startActivity(intent);
                                        }
                                    });


                                    databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).child("contactadded");
                                    databasecontact.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("singlestatus", "onDataChange: " + dataSnapshot);
                                            boolean status = false ;
                                            boolean mystatus = false ;
                                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                                Log.d("Allstatus", "onDataChange: " + ds);
                                                if(pet.getUid().equals(ds.getKey())){
                                                    status = true;
                                                    break;
                                                }else{
                                                    status = false;
                                                }
                                            }

                                            if(status == true){
                                                addLayout.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    addLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            addContact(new Contact(pet.getUid(),pet.getFullname(),pet.getImage(),pet.getJurusan().toString(),pet.getFakultas().toString(),pet.getSearch()));
                                            addLayout.setVisibility(View.GONE);
                                        }
                                    });


                                    chatLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(SearchFriendActivity.this, MessageActivity.class);
                                            intent.putExtra("image",pet.getImage());
                                            intent.putExtra("name",pet.getFullname());
                                            intent.putExtra("id",pet.getUid());
                                            startActivity(intent);
                                        }
                                    });

                                    mBuilder.setView(mView);
                                    final AlertDialog dialognya = mBuilder.create();
                                    dialognya.show();

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("user").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getUid().equals(fuser.getUid())) {
                        mData.add(user);
                    }

                    if(mData !=  null ) {
                        emptyView.setVisibility(View.GONE);
                    }else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
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
                                intent.putExtra("iduser", pet.getUid());
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

    private void addContact(final Contact contact) {
        databasecontact.child(contact.getUserid()).setValue(contact).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        addNotification(contact.getUserid(),contact.getUserid(),"Menambahkan anda sebagai teman");
                        sendNotifiaction(contact.getUserid(),contact.getNameuser(),"Menambahkan anda ke kontak",contact.getUserid(),contact.getUserid());
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
                            userid,"profile", idpost);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(SearchFriendActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
        hashMap.put("ispost", true);
        hashMap.put("type", "1");
        reference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SearchFriendActivity.this, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SearchFriendActivity.this, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
