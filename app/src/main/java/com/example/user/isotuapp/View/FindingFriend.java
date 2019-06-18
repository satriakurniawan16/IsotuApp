package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
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

import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Controller.FindingNearbyAdapter;
import com.example.user.isotuapp.Controller.UserAdapter;
import com.example.user.isotuapp.Model.Chatlist;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.APIService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FindingFriend extends AppCompatActivity {

    private static final String TAG = "FindingFriend";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private RecyclerView recyclerView;
    FloatingActionButton fab;
    private FindingNearbyAdapter mAdapter;
    private ArrayList<User> mData;
    APIService apiService;
    Spinner spinnerText;
    private ArrayList<String> mDataId;
    private Double CurrentLatitude,CurrentLongitude,latitude,longitude;
    FirebaseUser fuser;
    LinearLayout emptyView;
    String myid;
    DatabaseReference reference,myreference,databasecontact;
    private ActionMode mActionMode;
    private List<User> usersList;


    private String[] distance = {
            "+3000 meter",
            "1500 - 3000 meter",
            "500 - 1500 meter",
            "< 500 meter"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cari Teman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(FindingFriend.this, SearchFriendActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        emptyView = (LinearLayout) findViewById(R.id.emptyview);
        spinnerText = (Spinner) findViewById(R.id.distance);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, distance);

        // mengeset Array Adapter tersebut ke Spinner
        spinnerText.setAdapter(adapter);
        spinnerText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                float dis = 0;
                switch (i){
                    case 0:
                        result(3000f,Float.MAX_VALUE);
                        break;
                    case 1:
                        result(1500f,3000f);
                        break;
                    case 2:
                        result(500f,1500f);
                        break;
                    case 3:
                        result(0f,500f);
                        break;

                }
                Toast.makeText(FindingFriend.this, "Selected "+ adapter.getItem(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.listfinded);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();
        mDataId = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.toMap);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServicesOK()){
                    init();
                }
            }
        });

        myreference = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
        myreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getLongitude() != null && user.getLongitude() != null) {
                    CurrentLatitude = user.getLatitude();
                    CurrentLongitude = user.getLongitude();
                }else {
                    CurrentLatitude = -6.9733085;
                    CurrentLongitude = 107.6328139;
                }
                myid = user.getUid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void result(final float mindistance, final float maxdistance) {
        mData = new ArrayList<>();
        mData.clear();
        reference = FirebaseDatabase.getInstance().getReference("user");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    Location currentLocation = new Location("current");
                    currentLocation.setLatitude(CurrentLatitude);
                    currentLocation.setLongitude(CurrentLongitude);
                    Location compareLoc = new Location("compare");
                    float minDist = mindistance;
                    float distance = Float.MAX_VALUE;
                    if(user.getUid() != null ){
                        if(user.getLatitude() != null && user.getLongitude() != null) {
                            compareLoc.setLatitude(user.getLatitude());
                            compareLoc.setLongitude(user.getLongitude());
                            distance = currentLocation.distanceTo(compareLoc);
                            if (!user.getUid().equals(fuser.getUid())) {
                                if (distance >= minDist && distance <= maxdistance && user.getUid() != fuser.getUid()) {
                                    mData.add(user);
                                }
                            }
                        }
                    }
                        if(mData == null) {
                            emptyView.setVisibility(View.VISIBLE);
                        }else {
                            emptyView.setVisibility(View.GONE);
                        }
                }
                mAdapter = new FindingNearbyAdapter(getApplicationContext(), mData, mDataId,
                        new FindingNearbyAdapter.ClickHandler() {
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
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(FindingFriend.this);

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
                                        Intent intent = new Intent(FindingFriend.this,FriendProfile.class);
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
                                        Intent intent = new Intent(FindingFriend.this, MessageActivity.class);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(FindingFriend.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(FindingFriend.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void init(){
                Intent intent = new Intent(FindingFriend.this, MapActivity.class);
                startActivity(intent);
    }

    private void addContact(final Contact contact) {
        databasecontact.child(contact.getUserid()).setValue(contact).
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
                                            Toast.makeText(FindingFriend.this, "Failed!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FindingFriend.this, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FindingFriend.this, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
