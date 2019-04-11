package com.example.user.isotuapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.user.isotuapp.Controller.UserAdapter;
import com.example.user.isotuapp.Model.Chatlist;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;



public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;
    LinearLayout emptyview;
    private List<Chatlist> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        emptyview = (LinearLayout) view.findViewById(R.id.emptyview);
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            final String TAG = ChatFragment.class.getSimpleName() ;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                    Log.d(TAG, "onDataChange: userlist");
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//
        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }
//
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("user");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            final String TAG = ChatFragment.class.getSimpleName() ;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    Log.d(TAG, "datanyasnap: " + snapshot);
                    Log.d(TAG, "datanyasnap: " + user);
                    for (Chatlist chatlist : usersList){
                        String idchat = "";
                        String chatlistid = "";
                        if(user.getUid() != null) {
                            idchat = user.getUid();
                            chatlistid = chatlist.getId();
                            Log.d("tolol1", "onDataChange: " + chatlistid);
                            Log.d("tolol2", "onDataChange: " + idchat);
                            Log.d(TAG, "data tolol" + user);
                            if (idchat.equalsIgnoreCase(chatlistid)) {
                                mUsers.add(user);
                            }
                        }
                    }
                    if(mUsers.size() == 0) {
                        emptyview.setVisibility(View.VISIBLE);
                    }else {
                        emptyview.setVisibility(View.GONE);
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

}