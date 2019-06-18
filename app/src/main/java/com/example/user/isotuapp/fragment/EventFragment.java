package com.example.user.isotuapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Controller.EventAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.Event;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.DetailEventActivity;
import com.example.user.isotuapp.View.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class EventFragment extends Fragment {

    private ArrayList<Event> mData;
    private ArrayList<String> mDataId;
    private EventAdapter mAdapter;
    View rootView;
    private ActionMode mActionMode;
    FirebaseUser currentUser;
    Event event;
    private DatabaseReference database;
    LinearLayout emptyview;
    private FirebaseAuth mFirebaseAuth;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Event.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
            mAdapter.updateEmptyView();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Event.class));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
//            mDataId.remove(pos);
            mData.remove(pos);
            mAdapter.updateEmptyView();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
        }
    };



    public EventFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_event, container, false);

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();

        emptyview = rootView.findViewById(R.id.emptyview);
        RecyclerView recyclerView = rootView.findViewById(R.id.listEvent);
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance().getReference("events");
        database.addChildEventListener(childEventListener);

        mAdapter = new EventAdapter(getContext(), mData, mDataId,emptyview,
                new EventAdapter.ClickHandler() {
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
                        Event pet = mData.get(position);
                        Intent intent = new Intent(getContext(), DetailEventActivity.class);
                        intent.putExtra("id",pet.getEventId());
                        intent.putExtra("judul",pet.getJudulEvent());
                        intent.putExtra("uid",currentUser.getUid());
                        intent.putExtra("uidevent",pet.getIduser());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        return true;
                    }
                });
        recyclerView.setAdapter(mAdapter);
        return rootView;

    }

}
