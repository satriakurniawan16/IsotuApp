package com.example.user.isotuapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.ChatGroupAdapter;
import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Controller.UserAdapter;
import com.example.user.isotuapp.Model.Chatlist;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.FriendProfile;
import com.example.user.isotuapp.View.MessageActivity;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;



public class ChatFragment extends Fragment {

    private RecyclerView recyclerView,recyclerViewGrup;

    private ArrayList<Chatlist> mData;
    private ArrayList<String> mDataId;
    private UserAdapter userAdapter;
    private ChatGroupAdapter mAdapterGrup;
    private ActionMode mActionMode;
    private List<User> mUsers;
    private List<Chatlist> usersList;
    FirebaseUser fuser;
    boolean status = false ;
    boolean statusGrup = false ;

    DatabaseReference reference,referenceGrup;
    LinearLayout emptyview,emptyView,button_showgrup,show_group,button_showchat,show_chat;
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Chatlist.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapterGrup.notifyDataSetChanged();
            mAdapterGrup.updateEmptyView();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Chatlist.class));
            mAdapterGrup.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mDataId.remove(pos);
//            mData.remove(pos);
            mAdapterGrup.notifyDataSetChanged();
            mAdapterGrup.updateEmptyView();
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
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);


        mData = new ArrayList<>();
        mDataId = new ArrayList<>();

        button_showgrup = (LinearLayout) view.findViewById(R.id.button_show_group);
        button_showchat = (LinearLayout) view.findViewById(R.id.button_show_pc);
        show_group = (LinearLayout) view.findViewById(R.id.show_group);
        show_chat = (LinearLayout) view.findViewById(R.id.show_pc);

        button_showgrup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusGrup == false ){
                    statusGrup = true;
                    show_group.setVisibility(View.GONE);
                    ImageView image = view.findViewById(R.id.image_show_grup);
                    image.setImageResource(R.mipmap.rightarrow);
                }else {
                    statusGrup = false;
                    show_group.setVisibility(View.VISIBLE);
                    ImageView image = view.findViewById(R.id.image_show_grup);
                    image.setImageResource(R.mipmap.downarrow);
                }
            }
        });

        button_showchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status == false ){
                    status= true;
                    show_chat.setVisibility(View.GONE);
                    ImageView image = view.findViewById(R.id.image_show);
                    image.setImageResource(R.mipmap.rightarrow);
                }else {
                    status = false;
                    show_chat.setVisibility(View.VISIBLE);
                    ImageView image = view.findViewById(R.id.image_show);
                    image.setImageResource(R.mipmap.downarrow);
                }
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewGrup = view.findViewById(R.id.recycler_view_group);
        recyclerViewGrup.setHasFixedSize(true);
        recyclerViewGrup.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        emptyview = (LinearLayout) view.findViewById(R.id.emptyviewgrup);
        emptyView = (LinearLayout) view.findViewById(R.id.emptyview);
        referenceGrup = FirebaseDatabase.getInstance().getReference("ChatlistGrup").child(fuser.getUid());
        referenceGrup.addChildEventListener(childEventListener);

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAdapterGrup = new ChatGroupAdapter(getContext(), mData, mDataId,emptyview,true,
                new ChatGroupAdapter.ClickHandler() {
                    @Override
                    public void onItemClick(int position) {
                        if (mActionMode != null) {
                            mAdapterGrup.toggleSelection(mDataId.get(position));
                            if (mAdapterGrup.selectionCount() == 0)
                                mActionMode.finish();
                            else
                                mActionMode.invalidate();
                            return;
                        }

                    }

                    @Override
                    public boolean onItemLongClick(int position) {
                        if (mActionMode != null) return false;
                        return true;
                    }
                });
        recyclerViewGrup.setAdapter(mAdapterGrup);
//
        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }
//

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    try {
                        for (Chatlist chatlist : usersList){
                            if (user.getUid().equals(chatlist.getId())){
                                mUsers.add(user);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(mUsers.size() == 0){
                        emptyView.setVisibility(View.VISIBLE);
                    }else {
                        emptyView.setVisibility(View.GONE);
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        referenceGrup.removeEventListener(childEventListener);
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }


}