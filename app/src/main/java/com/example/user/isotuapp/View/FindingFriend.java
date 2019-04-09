package com.example.user.isotuapp.View;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Controller.FindingNearbyAdapter;
import com.example.user.isotuapp.Controller.UserAdapter;
import com.example.user.isotuapp.Model.Chatlist;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class FindingFriend extends AppCompatActivity {

    private static final String TAG = "FindingFriend";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private RecyclerView recyclerView;
    FloatingActionButton fab;
    private FindingNearbyAdapter mAdapter;
    private ArrayList<User> mData;
    Spinner spinnerText;
    private ArrayList<String> mDataId;
    private Double CurrentLatitude,CurrentLongitude,latitude,longitude;
    FirebaseUser fuser;
    String myid;
    DatabaseReference reference,myreference;
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
                CurrentLatitude = user.getLatitude();
                CurrentLongitude = user.getLongitude();
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
                        compareLoc.setLatitude(user.getLatitude());
                        compareLoc.setLongitude(user.getLongitude());
                        distance = currentLocation.distanceTo(compareLoc);
                        if(user.getUid() != fuser.getUid()) {
                            if (distance >= minDist && distance<= maxdistance  ) {
                                mData.add(user);
                            }
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

}
