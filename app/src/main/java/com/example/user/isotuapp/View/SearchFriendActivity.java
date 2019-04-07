package com.example.user.isotuapp.View;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Controller.SearchUserAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity {

    private ArrayList<User> mData;
    private ArrayList<String> mDataId;
    private SearchUserAdapter mAdapter;
    private EditText SearchUser;
    RecyclerView recyclerView;

    private ActionMode mActionMode;
    FirebaseUser currentUser;
    private DatabaseReference database;
    private FirebaseAuth mFirebaseAuth;
    LinearLayout toFindingFriend;
    private BroadcastReceiver broadcastReceiver;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters


    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cari Teman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(SearchFriendActivity.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();

        toFindingFriend = (LinearLayout) findViewById(R.id.to_findingnearby);
        toFindingFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this,FindingFriend.class);
                startActivity(intent);
            }
        });



        recyclerView = findViewById(R.id.listSearch);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        SearchUser = (EditText) findViewById(R.id.searchuser);
        SearchUser.addTextChangedListener(new TextWatcher() {
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

        database = FirebaseDatabase.getInstance().getReference("user");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (SearchUser.getText().toString().equals("")) {
                    mData.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (!user.getUid().equals(currentUser.getUid())) {
                            mData.add(user);
                        }

                    }
                    mAdapter = new SearchUserAdapter(getApplicationContext(), mData, mDataId,
                            new SearchUserAdapter.ClickHandler() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("user").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getUid().equals(fuser.getUid())){
                        mData.add(user);
                    }
                }
                mAdapter = new SearchUserAdapter(getApplicationContext(), mData, mDataId,
                        new SearchUserAdapter.ClickHandler() {
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

}
