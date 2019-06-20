package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.FindingNearbyAdapter;
import com.example.user.isotuapp.Controller.SearchUserAdapter;
import com.example.user.isotuapp.Model.Contact;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacultyProvince extends AppCompatActivity {


    private ArrayList<User> mData;
    private ArrayList<String> mDataId;
    private SearchUserAdapter mAdapter;
    Spinner spinnerText;
    RecyclerView recyclerView;
    FirebaseUser fuser;
    String type;
    APIService apiService;
    String ref;
    String myid;
    DatabaseReference reference,myreference,databasecontact;
    private ActionMode mActionMode;
    LinearLayout emptyView;

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

        emptyView = (LinearLayout) findViewById(R.id.emptyview);

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
            Toast.makeText(this, "ref : " +ref , Toast.LENGTH_SHORT).show();
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
                        if(user.getUid()!= null ){
                            mData.add(user);
                        }
                    }


                if(mData !=  null ) {
                    emptyView.setVisibility(View.GONE);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
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
                                    if(!pet.getUid().equals(fuser.getUid())){
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(FacultyProvince.this);

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
                                                Intent intent = new Intent(FacultyProvince.this,FriendProfile.class);
                                                intent.putExtra("iduser",pet.getUid());
                                                startActivity(intent);
                                            }
                                        });


                                        databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(fuser.getUid()).child("contactadded");
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
                                                Intent intent = new Intent(FacultyProvince.this, MessageActivity.class);
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
                if(ref.equals("searchbyprovince")){
                    searchUsers(spinnerText.getSelectedItem().toString().toLowerCase());
                }else {
                    searchUsers(spinnerText.getSelectedItem().toString());
                }

//                Toast.makeText(FacultyProvince.this, "selected : " +  spinnerText.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
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


                if(mData !=  null ) {
                    emptyView.setVisibility(View.GONE);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
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

    private void addContact(final Contact contact) {
        databasecontact.child(contact.getUserid()).setValue(contact).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                                            Toast.makeText(FacultyProvince.this, "Failed!", Toast.LENGTH_SHORT).show();
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
        hashMap.put("date", System.currentTimeMillis());
        reference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(FacultyProvince.this, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(FacultyProvince.this, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
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
