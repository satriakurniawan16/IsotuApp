package com.example.user.isotuapp.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.isotuapp.BuildConfig;
import com.example.user.isotuapp.Controller.SearchUserAdapter;
import com.example.user.isotuapp.Controller.ShareAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.ChatFragment;
import com.example.user.isotuapp.fragment.ContactFragment;
import com.example.user.isotuapp.fragment.EventFragment;
import com.example.user.isotuapp.fragment.HomeFragment;
import com.example.user.isotuapp.fragment.ProfileFragment;
import com.example.user.isotuapp.utils.GPS_Service;
import com.example.user.isotuapp.utils.LocationService;
import com.example.user.isotuapp.utils.Utils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.Dash;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.OnClick;

public class Dashboard extends AppCompatActivity  {
    private static final String TAG = Dashboard.class.getSimpleName();
    private Toolbar toolbar;
    double latitudeDouble,longitudeDouble;
    private TabLayout tabLayout;
    FrameLayout addGroup,notification;
    private ViewPager viewPager;
    View progressOverlay;
    String idpost= "";
    Context context;
    RecyclerView recyclerView;
    private BroadcastReceiver broadcastReceiver;
    FloatingActionButton fab,fabevent,fab_addfriend;
    private int[] tabIcons = {
            R.mipmap.dashboard,
            R.mipmap.contact,
            R.mipmap.chat,
            R.mipmap.news,
            R.mipmap.profile
    };

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    // location last updated time
    private String mLastUpdateTime;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    FirebaseUser currentUser;
    EditText searchUser;
    private DatabaseReference database;
    private FirebaseAuth mFirebaseAuth;


    private SlidingUpPanelLayout mLayout;
    private ArrayList<Contact> mData;
    private ArrayList<String> mDataId;
    private ShareAdapter mAdapter;

    private static int TIME_OUT = 1000;

    LocationService mService;
    boolean mBound = false;


    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Contact.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Contact.class));
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


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    private Boolean mRequestingLocationUpdates;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//
//        notification = (FrameLayout) findViewById(R.id.notif);
//        addGroup = (FrameLayout)findViewById(R.id.grupadd);
//        addGroup.setVisibility(View.GONE);

        mLayout = (SlidingUpPanelLayout ) findViewById(R.id.sliding_layout_share);
        progressOverlay = (FrameLayout) findViewById(R.id.progress_overlay);

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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



        init();
        startLocationButtonClick();
        initService();
        Intent servIntent = new Intent(Dashboard.this,LocationService.class);
        startService(servIntent);

        restoreValuesFromBundle(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("user").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);
                if(usr.getCompleteProfile() == 0){
                    Intent intent = new Intent(Dashboard.this,CompleteProfile.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.to_posting);
        fabevent = (FloatingActionButton) findViewById(R.id.to_addevent);
        fab_addfriend = (FloatingActionButton) findViewById(R.id.to_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Dashboard.this,Posting.class);
                startActivity(intent);
            }
        });

        fabevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Dashboard.this,EventActivity.class);
                startActivity(intent);
            }
        });

        fab_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,SearchFriendActivity.class);
                startActivity(intent);
            }
        });

        fab_addfriend.setVisibility(View.GONE);


        fabevent.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        toolbar.setTitle("Home");
                        fab.setVisibility(View.VISIBLE);
                        fab_addfriend.setVisibility(View.GONE);
                        fabevent.setVisibility(View.GONE);
//                        addGroup.setVisibility(View.GONE);
                        break;
                    case 1:
                        toolbar.setTitle("Contact");
                        fab.setVisibility(View.GONE);
                        fabevent.setVisibility(View.GONE);
                        fab_addfriend.setVisibility(View.VISIBLE);
//                        addGroup.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        toolbar.setTitle("Chat");
                        fab.setVisibility(View.GONE);
                        fabevent.setVisibility(View.GONE);
                        fab_addfriend.setVisibility(View.GONE);
//                        addGroup.setVisibility(View.GONE);
                        break;
                    case 3:
                        toolbar.setTitle("Event");
                        fab.setVisibility(View.GONE);
                        fab_addfriend.setVisibility(View.GONE);
                        fabevent.setVisibility(View.VISIBLE);
//                        addGroup.setVisibility(View.GONE);
                        break;
                    case 4:
                        toolbar.setTitle("Profile");
                        fab.setVisibility(View.GONE);
                        fabevent.setVisibility(View.GONE);
                        fab_addfriend.setVisibility(View.GONE);
//                        addGroup.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == 3) {
                    ActionBar actionBar = getSupportActionBar();
                    actionBar.hide();
                }
            }
        });


        setupTabIcons();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new ContactFragment(), "Contact");
        adapter.addFragment(new ChatFragment(), "Chatting");
        adapter.addFragment(new EventFragment(), "Event");
        adapter.addFragment(new ProfileFragment(), "Profile");
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }


    @SuppressLint("RestrictedApi")
    public void fabgone(){
        fab.setVisibility(View.GONE);
    }
    @SuppressLint("RestrictedApi")
    public void fabvisible(){
        fab.setVisibility(View.VISIBLE);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }



    private void status(final String status){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status", status);
                    reference.updateChildren(hashMap);
            }
        };
    }





    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.d(TAG, "onReceive+Baru: " + "\n" +intent.getExtras().get("coordinates"));

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public void onBackPressed() {
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

//                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

    }


    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

//    private void toggleButtons() {
//        if (mRequestingLocationUpdates) {
//            btnStartUpdates.setEnabled(false);
//            btnStopUpdates.setEnabled(true);
//        } else {
//            btnStartUpdates.setEnabled(true);
//            btnStopUpdates.setEnabled(false);
//        }
//    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */

    public void initService() {
        if (mBound)
            mService.getLocation();
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Dashboard.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(Dashboard.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

//                        updateLocationUI();
                    }
                });
    }

    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
//                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void stopLocationButtonClick() {
        mRequestingLocationUpdates = false;
        stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
//                        toggleButtons();
                    }
                });
    }

    public void showLastKnownLocation() {
        if (mCurrentLocation != null) {
            Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
                    + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    public class LongOperation extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(1000); // no need for a loop
            } catch (InterruptedException e) {
                Log.e("LongOperation", "Interrupted", e);
                return "Interrupted";
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            startLocationButtonClick();
        }
    }
    final Utils utils = new Utils(this);
    public void getSliding(final String idpostny){
        idpost = idpostny;
        Toast.makeText(getApplicationContext(), "thissssss", Toast.LENGTH_SHORT).show();
        mData.clear();
        database = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid());
        database.addChildEventListener(childEventListener);
        mAdapter = new ShareAdapter(Dashboard.this, mData , mDataId, idpost);
        recyclerView.setAdapter(mAdapter);
        utils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                utils.animateView(progressOverlay, View.GONE, 0, 200);
            }
        }, TIME_OUT);

    }



    private void searchUsers(String s) {
        mData.clear();
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        if(s.equals("")){
            database = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid());
            database.addChildEventListener(childEventListener);
        }else{
            query.addChildEventListener(childEventListener);
        }
        mAdapter = new ShareAdapter(Dashboard.this, mData , mDataId,idpost);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
}