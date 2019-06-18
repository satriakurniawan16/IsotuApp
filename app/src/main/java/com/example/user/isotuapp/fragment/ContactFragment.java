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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Controller.GrupAdapter;
import com.example.user.isotuapp.Controller.HobiAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.Grup;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.DetailUserHobi;
import com.example.user.isotuapp.View.FriendProfile;
import com.example.user.isotuapp.View.GrupMessageActivity;
import com.example.user.isotuapp.View.MessageActivity;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.util.ArrayList;

public class ContactFragment extends Fragment {

    View rootView;
    private ArrayList<Contact> mData;
    private ArrayList<String> mDataId;
    private ContactAdapter mAdapter;

    private ArrayList<Grup> mDatagrup;
    private ArrayList<String> mDataIdgrup;
    private GrupAdapter mAdaptergrup;

    private ActionMode mActionMode;
    private ActionMode mActionModeGrup;
    FirebaseUser currentUser;
    HobiModel hobi;
    private DatabaseReference database,databaseGrup;
    private FirebaseAuth mFirebaseAuth;


    LinearLayout emptyView,emptyViewGrup;

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


    private ChildEventListener childEventListenerGrup = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mDatagrup.add(dataSnapshot.getValue(Grup.class));
            mDataIdgrup.add(dataSnapshot.getKey());
            mAdaptergrup.notifyDataSetChanged();
            mAdaptergrup.updateEmptyView();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataIdgrup.indexOf(dataSnapshot.getKey());
            mDatagrup.set(pos, dataSnapshot.getValue(Grup.class));
            mAdaptergrup.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataIdgrup.indexOf(dataSnapshot.getKey());
            mDataIdgrup.remove(pos);
            mDatagrup.remove(pos);
            mAdaptergrup.notifyDataSetChanged();
            mAdaptergrup.updateEmptyView();
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
        mDatagrup = new ArrayList<>();
        mDataIdgrup = new ArrayList<>();
        emptyView = (LinearLayout) rootView.findViewById(R.id.emptyview);
        emptyViewGrup = (LinearLayout) rootView.findViewById(R.id.emptyviewgrup);
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
                        final Contact pet = mData.get(position);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.modal_user,
                                null);
                        ImageView profileImageView = (ImageView) mView.findViewById(R.id.profilepopup);
                        TextView nameTextView = (TextView) mView.findViewById(R.id.nameuserpopup);
                        TextView jurusanTextView = (TextView) mView.findViewById(R.id.jurusanpopup);
                        LinearLayout profileLayout = (LinearLayout) mView.findViewById(R.id.profilebutton);
                        LinearLayout chatLayout = (LinearLayout) mView.findViewById(R.id.chatbutton);
                        LinearLayout addLayout = (LinearLayout) mView.findViewById(R.id.addbutton);

                        addLayout.setVisibility(View.GONE);
                        Picasso.get().load(pet.getImageuser()).into(profileImageView);
                        nameTextView.setText(pet.getNameuser());
                        jurusanTextView.setText(pet.getMajoruser());
                        profileLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(),FriendProfile.class);
                                intent.putExtra("iduser",pet.getUserid());
                                startActivity(intent);
                            }
                        });

                        chatLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), MessageActivity.class);
                                intent.putExtra("type","0");
                                intent.putExtra("image",pet.getImageuser());
                                intent.putExtra("name",pet.getNameuser());
                                intent.putExtra("id",pet.getUserid());
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
                        final Contact pet = mData.get(position);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.popup_message,
                                null);

                        LinearLayout hapus = (LinearLayout) mView.findViewById(R.id.hapus);


                        mBuilder.setView(mView);
                        final AlertDialog dialognya = mBuilder.create();
                        dialognya.show();

                        hapus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.child(pet.getUserid()).removeValue();
                                dialognya.dismiss();
                            }
                        });


                        return true;
                    }
                });
        recyclerView.setAdapter(mAdapter);


        RecyclerView recyclerViewGrup = rootView.findViewById(R.id.listGrup);
        recyclerViewGrup.setHasFixedSize(true);
        LinearLayoutManager layoutManagerGrup =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewGrup.setLayoutManager(layoutManagerGrup);

        databaseGrup = FirebaseDatabase.getInstance().getReference("group").child(currentUser.getUid());
        databaseGrup.addChildEventListener(childEventListenerGrup);

        mAdaptergrup = new GrupAdapter(getContext(), mDatagrup, mDataIdgrup,emptyViewGrup,
                new GrupAdapter.ClickHandler() {
                    @Override
                    public void onItemClick(int position) {
                        if (mActionModeGrup != null) {
                            mAdapter.toggleSelection(mDataIdgrup.get(position));
                            if (mAdaptergrup.selectionCount() == 0)
                                mActionModeGrup.finish();
                            else
                                mActionModeGrup.invalidate();
                            return;
                        }
                        Grup grup = mDatagrup.get(position);
                        Intent intent = new Intent(getContext(), GrupMessageActivity.class);
                        intent.putExtra("type","1");
                        intent.putExtra("image",grup.getImagegrup());
                        intent.putExtra("name",grup.getNamagrup());
                        intent.putExtra("id",grup.getIdgrup());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionModeGrup != null) return false;


                        return true;
                    }
                });

        recyclerViewGrup.setAdapter(mAdaptergrup);

        return rootView;
    }


}
