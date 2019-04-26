package com.example.user.isotuapp.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.NotificationAdapter;
import com.example.user.isotuapp.Controller.SearchUserAdapter;
import com.example.user.isotuapp.Model.NotifModel;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {


    private ArrayList<NotifModel> mData;
    private ArrayList<String> mDataId;
    private NotificationAdapter mAdapter;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private ActionMode mActionMode;
    LinearLayout emptyView;
    DatabaseReference databasenotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifikasi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(NotificationActivity.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        emptyView = (LinearLayout) findViewById(R.id.emptyview);

        recyclerView = findViewById(R.id.listNotification);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onResume() {
        super.onResume();
        display();
    }

    private void display () {
        mData.clear();
        databasenotif = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUser.getUid());
        databasenotif.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                NotifModel notifModel = snapshot.getValue(NotifModel.class);
                mData.add(notifModel);
                }
                mAdapter = new NotificationAdapter(getApplicationContext(), mData, mDataId,
                        new NotificationAdapter.ClickHandler() {
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
                                NotifModel pet = mData.get(position);
                                Toast.makeText(NotificationActivity.this, "LOLLLL", Toast.LENGTH_SHORT).show();
                                if(pet.getType().equals("0")){
                                    Intent intent =  new Intent(NotificationActivity.this,PostActivity.class);
                                    intent.putExtra(Constants.EXTRA_POST,pet.getPostid());
                                    startActivity(intent);
                                }else if(pet.getType().equals("1")){
                                    Intent intent =  new Intent(NotificationActivity.this,FriendProfile.class);
                                    intent.putExtra("iduser",pet.getPostid());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public boolean onItemLongClick(int position) {
                                if (mActionMode != null) return false;
                                return true;
                            }
                        });
                recyclerView.setAdapter(mAdapter);
                if(mData.size()> 0){
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
