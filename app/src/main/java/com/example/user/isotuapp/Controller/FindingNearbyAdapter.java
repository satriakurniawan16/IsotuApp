package com.example.user.isotuapp.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.FindingFriend;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FindingNearbyAdapter  extends RecyclerView.Adapter<FindingNearbyAdapter.ViewHolder> {
    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<User> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    private double longitude;
    private double latitude;
    private FusedLocationProviderClient client;

    public String bestProvider;

    public LocationManager locationManager;
    public Criteria criteria;


    public FindingNearbyAdapter(Context context, ArrayList<User> data, ArrayList<String> dataId,
                             ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_nearbypoeple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        getLocation();
        User pet = mData.get(position);
        holder.nameTextView.setText(pet.getFullname());
        holder.keteranganTextView.setText(pet.getJurusan()+", "+pet.getFakultas());
        Picasso.get().load(pet.getImage()).into(holder.profilImageview);
        LatLng locationStart = new LatLng(latitude, longitude);
        LatLng locationEnd = new LatLng(pet.getLatitude(), pet.getLongitude());
        double distance = CalculationByDistance(locationStart,locationEnd);
        String tes = String.valueOf(distance);
        String lol = tes.substring(0,5);
        holder.distanceTextView.setText(lol + " meter");

//        holder.itemView.setSelected(mSelectedId.contains(mDataId.get(position)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void toggleSelection(String dataId) {
        if (mSelectedId.contains(dataId))
            mSelectedId.remove(dataId);
        else
            mSelectedId.add(dataId);
        notifyDataSetChanged();
    }

    public int selectionCount() {
        return mSelectedId.size();
    }

    public void resetSelection() {
        mSelectedId = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedId() {
        return mSelectedId;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {
        final TextView nameTextView;
        final TextView keteranganTextView;
        final TextView distanceTextView;
        final ImageView profilImageview;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nama_user);
            keteranganTextView = itemView.findViewById(R.id.detail_user);
            distanceTextView = itemView.findViewById(R.id.distance_user);
            profilImageview = itemView.findViewById(R.id.image_user);
            itemView.setFocusable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            mClickHandler.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mClickHandler.onItemLongClick(getAdapterPosition());
        }
    }

    public interface ClickHandler {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c *1000;
    }

    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }

    protected void getLocation() {
        if (isLocationEnabled(mContext)) {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
            }
            // Write you code here if permission already given.
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }
}

