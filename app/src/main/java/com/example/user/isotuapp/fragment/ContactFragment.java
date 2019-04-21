package com.example.user.isotuapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Controller.HobiAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.DetailUserHobi;
import com.example.user.isotuapp.View.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.util.ArrayList;

public class ContactFragment extends Fragment {

    View rootView;
    private ArrayList<Contact> mData;
    private ArrayList<String> mDataId;
    private ContactAdapter mAdapter;

    private ActionMode mActionMode;
    FirebaseUser currentUser;
    HobiModel hobi;
    private DatabaseReference database;
    private FirebaseAuth mFirebaseAuth;


    LinearLayout emptyView;

    public ContactFragment() {
        // Required empty public constructor
    }


    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Contact.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
            mAdapter.updateEmptyView();
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
            mAdapter.updateEmptyView();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        emptyView = (LinearLayout) rootView.findViewById(R.id.emptyview);
        RecyclerView recyclerView = rootView.findViewById(R.id.listContact);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).child("contactadded");
        database.addChildEventListener(childEventListener);

        mAdapter = new ContactAdapter(getContext(), mData, mDataId,emptyView,
                new ContactAdapter.ClickHandler() {
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
                        Contact pet = mData.get(position);
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra("image",pet.getImageuser());
                        intent.putExtra("name",pet.getNameuser());
                        intent.putExtra("id",pet.getUserid());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.popup_message,
                                null);


                        mBuilder.setView(mView);
                        final AlertDialog dialognya = mBuilder.create();
                        dialognya.show();

                        return true;
                    }
                });
        recyclerView.setAdapter(mAdapter);
        return rootView;
    }


}
