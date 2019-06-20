package com.example.user.isotuapp.View;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.MainActivity;
import com.example.user.isotuapp.Model.MarkerData;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.utils.BubleTransformation;
import com.example.user.isotuapp.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    FloatingActionButton fab;
    MarkerOptions markerOption;
    Utils utils;
    List<Target> targets;
    HashMap<Marker, MarkerData> mMarkersHashMap;
    private SlidingUpPanelLayout mLayout;
    FirebaseAuth aut;
    FirebaseUser  cuser;
    private static int TIME_OUT = 2000;
    public View rootView, progressOverlay;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    getUserLocation();
                }
            }, 100);
            final Utils utils = new Utils(getApplicationContext());

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final FirebaseUser fuser = mAuth.getCurrentUser();
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    final double vlat = marker.getPosition().latitude;
                    final double vlong = marker.getPosition().latitude;
                    if(!marker.getSnippet().equals(fuser.getUid())){
                        utils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                                utils.animateView(progressOverlay, View.GONE, 0, 200);
                                final TextView Fullname = (TextView) findViewById(R.id.nameMarker);
                                Fullname.setText(marker.getTitle());
                                displayInfo(marker.getSnippet());
                            }
                        }, TIME_OUT);
                    }

                    final Button buttonDirection = (Button) findViewById(R.id.getdirec);
                    buttonDirection.setVisibility(View.GONE);
                    buttonDirection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("geo:"+vlat+","+vlong+"?q="+marker.getTitle());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });
                    return true;
                }
            });

        }

    }

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        mLayout = (SlidingUpPanelLayout ) findViewById(R.id.sliding_layout);
        progressOverlay = (FrameLayout) findViewById(R.id.progress_overlay);
        mMarkersHashMap = new HashMap<>();
        targets = new ArrayList<>();

        aut = FirebaseAuth.getInstance();
        cuser = aut.getCurrentUser();
        fab = (FloatingActionButton) findViewById(R.id.getmy);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            getUserLocation();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                     }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }



    public void getUserLocation(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    ArrayList<MarkerData> markers = new ArrayList<MarkerData>();
                    markers.clear();
                    Log.d("lolllluser", "onDataChange: " + cuser.getUid());
                    if(user.getLongitude() != null && user.getLongitude() != null && user.getUid() != cuser.getUid()) {
                        markers.add(new MarkerData(user.getUid(), user.getImage(), user.getLatitude(), user.getLongitude(), user.getFullname()));
                        plotMarkers(markers);
                    }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void plotMarkers(ArrayList<MarkerData> markers) {
        if (markers.size() > 0) {
            for (MarkerData myMarker : markers) {

                markerOption = new MarkerOptions().title(myMarker.getName()).position(new LatLng(myMarker.getLat(), myMarker.getLng())).snippet(myMarker.getId());
                Marker location_marker = mMap.addMarker(markerOption);

                Target target = new PicassoMarker(location_marker);
                targets.add(target);
                Picasso.get().load(myMarker.getImageUrl()).resize(200, 200).centerCrop().transform(new BubleTransformation(20)).
                into(target);

                mMarkersHashMap.put(location_marker, myMarker);

//                i = getIntent();
//                if (i.getBooleanExtra("maps", true)) {
//                    // buttonNavigasi.setVisibility(View.VISIBLE);

                    location_marker.setTitle(myMarker.getName());
//                    LatLng dest = new LatLng(myMarker.getLat(), myMarker.getLng());
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, DEFAULT_ZOOM));
//                } else {
//                    Log.d(MapsActivity.class.getSimpleName(), "In else{}");
                    // mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
//                }
            }
        }
    }

    protected Marker createMarker(final double latitude, final double longitude, final String title,final String image) {

        Marker mymarker;

        mymarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.0f, 0.1f)
                .title(title));

        Target target = new PicassoMarker(mymarker);
        Picasso.get().load(image).resize(200,200).transform(new BubleTransformation(20)).into(target);

        return mymarker;
    }

    public class PicassoMarker implements Target {
        Marker mMarker;

        PicassoMarker(Marker marker) {
            mMarker = marker;
        }

        @Override
        public int hashCode() {
            return mMarker.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof PicassoMarker) {
                Marker marker = ((PicassoMarker) o).mMarker;
                return mMarker.equals(marker);
            } else {
                return false;
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }


        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

}

    @Override
    protected void onResume() {
        super.onResume();
        getUserLocation();
        status("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }

    private void status(final String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserLocation();
    }

    public void displayInfo(final String id) {
        final TextView major = (TextView) findViewById(R.id.majorMarker);
        final ImageView ProfileImage = (ImageView) findViewById(R.id.imageMarker);
        final Button buttonChat = (Button) findViewById(R.id.to_chatfriend);
        final Button buttonProfile = (Button) findViewById(R.id.toshowProfile);
        final Button buttonDirection = (Button) findViewById(R.id.getdirec);


        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this,MessageActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this,FriendProfile.class);
                intent.putExtra("iduser",id);
                startActivity(intent);
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImage()).into(ProfileImage);
                major.setText(user.getJurusan()+", "+user.getFakultas());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


