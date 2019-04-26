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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.HobiAdapter;
import com.example.user.isotuapp.Controller.HobiUserAdapter;
import com.example.user.isotuapp.Controller.OrganisasiAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.Organiasasi;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Model.UserHobi;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.APIService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class DetailUserHobi extends AppCompatActivity {

    private ArrayList<UserHobi> mData;
    private ArrayList<String> mDataId;
    private HobiUserAdapter mAdapter;
    private String link;
    private String child;
    TextView titleTextView;
    String title;
    APIService apiService;
    FirebaseUser currentUser;
    private DatabaseReference database,databasecontact;
    private ActionMode mActionMode;
    LinearLayout emptyView;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(UserHobi.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
            mAdapter.updateEmptyView();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(UserHobi.class));
            mAdapter.updateEmptyView();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mDataId.remove(pos);
            mData.remove(pos);
            mAdapter.updateEmptyView();
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
        child = intent.getStringExtra("child");
        title = intent.getStringExtra("title");
        titleTextView = (TextView) findViewById(R.id.titleTexttoolbar);
        titleTextView.setText(title);
        database = FirebaseDatabase.getInstance().getReference(link).child(child);
        emptyView = (LinearLayout) findViewById(R.id.emptyview);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listUser);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        database.addChildEventListener(childEventListener);

        mAdapter = new HobiUserAdapter(getApplicationContext(), mData, mDataId,emptyView,
                new HobiUserAdapter.ClickHandler() {
                    @Override
                    public void onItemClick(int position) {
                        final UserHobi pet = mData.get(position);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailUserHobi.this);

                        View mView = getLayoutInflater().inflate(R.layout.modal_user,
                                null);

                        ImageView profileImageView = (ImageView) mView.findViewById(R.id.profilepopup);
                        TextView nameTextView = (TextView) mView.findViewById(R.id.nameuserpopup);
                        final TextView jurusanTextView = (TextView) mView.findViewById(R.id.jurusanpopup);
                        LinearLayout profileLayout = (LinearLayout) mView.findViewById(R.id.profilebutton);
                        LinearLayout chatLayout = (LinearLayout) mView.findViewById(R.id.chatbutton);
                        final LinearLayout addLayout = (LinearLayout) mView.findViewById(R.id.addbutton);

                        Picasso.get().load(pet.getFotoprofil()).into(profileImageView);
                        nameTextView.setText(pet.getNamaprofil());
                        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(pet.getIduser());
                        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final User usr = dataSnapshot.getValue(User.class);
                                jurusanTextView.setText(usr.getJurusan());

                                addLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addContact(new Contact(usr.getUid(),usr.getFullname(),usr.getImage(),usr.getJurusan(),
                                                usr.getFakultas().toString(),usr.getSearch()));
                                        addLayout.setVisibility(View.GONE);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        profileLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DetailUserHobi.this,FriendProfile.class);
                                intent.putExtra("iduser",pet.getIduser());
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
                                    if(pet.getIduser().equals(ds.getKey())){
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



                        chatLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DetailUserHobi.this, MessageActivity.class);
                                intent.putExtra("image",pet.getFotoprofil());
                                intent.putExtra("name",pet.getNamaprofil());
                                intent.putExtra("id",pet.getIduser());
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
                                            Toast.makeText(DetailUserHobi.this, "Failed!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DetailUserHobi.this, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailUserHobi.this, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
