package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.isotuapp.Model.Event;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DetailEventActivity extends AppCompatActivity {

    ImageView imageEvent;
    TextView titleEvent,scheduleEvent,locationEvent,descriptionEvent,userEvent;
    Button seeAttendance,attendent,unAttendant,editButton,deleteButton;
    String id,uid,judul;
    FirebaseUser currentUser;
    private DatabaseReference database,databasehadir;
    private FirebaseAuth mFirebaseAuth;
    String eventuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(DetailEventActivity.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        imageEvent = (ImageView) findViewById(R.id.image_detail_event);
        editButton = (Button) findViewById(R.id.edit_event);
        deleteButton = (Button) findViewById(R.id.delete_event);
        titleEvent = (TextView) findViewById(R.id.title_event);
        scheduleEvent = (TextView) findViewById(R.id.schedule_event);
        locationEvent = (TextView) findViewById(R.id.location_event);
        descriptionEvent = (TextView) findViewById(R.id.event_desc);
        userEvent = (TextView) findViewById(R.id.user_event);
        attendent = (Button) findViewById(R.id.follow);
        unAttendant = (Button) findViewById(R.id.unfollow);
        final Intent intent = getIntent();
        id =  intent.getStringExtra("id");

        eventuid = intent.getStringExtra("uidevent");
        if(eventuid.equals(currentUser.getUid())){
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }else {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentedit = new Intent(DetailEventActivity.this,EditEventActivity.class);
                intentedit.putExtra("idevent",id);
                startActivity(intentedit);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailEventActivity.this)
                        .setMessage("Apakah anda yakin untuk menghapus ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference dbfpost = FirebaseDatabase.getInstance().getReference("events");
                                dbfpost.child(id).removeValue();
                                Intent intent1 = new Intent(DetailEventActivity.this,Dashboard.class);
                                startActivity(intent1);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        });
                builder.create().show();
            }
        });
        uid =  intent.getStringExtra("uid");
        judul =  intent.getStringExtra("judul");
        database = FirebaseDatabase.getInstance().getReference("events").child(id);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                Picasso.get().load(event.getImage()).into(imageEvent);
                titleEvent.setText(event.getJudulEvent());
                scheduleEvent.setText(event.getTanggal()+"\n"+event.getJamMulai()+" - "+ event.getJamBerakhir());
                locationEvent.setText(event.getLokasi());
                userEvent.setText("Ditambahkan Oleh : "+ event.getUser());
                descriptionEvent.setText(event.getDeskripsi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        databasehadir = FirebaseDatabase.getInstance().getReference("list_event_atender").child(judul);
        databasehadir.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(currentUser.getUid().equals(ds.getKey())){
                        unAttendant.setVisibility(View.VISIBLE);
                        attendent.setVisibility(View.GONE);
                    }else{
                        unAttendant.setVisibility(View.GONE);
                        attendent.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        unAttendant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailEventActivity.this)
                        .setMessage("Apakah anda yakin batal menghadiri acara ini ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databasehadir.child(uid).removeValue();
                                unAttendant.setVisibility(View.GONE);
                                attendent.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });

        seeAttendance = (Button) findViewById(R.id.see_attendance);
        seeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentlol = new Intent(DetailEventActivity.this,DetailUserHobi.class);
                intentlol.putExtra("reference","list_event_atender");
                intentlol.putExtra("child",titleEvent.getText().toString());
                intent.putExtra("title","Calon peserta");
                startActivity(intentlol);
            }
        });

        attendent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(currentUser.getUid());
                dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User usr = dataSnapshot.getValue(User.class);
                        final HashMap<String, Object> hobiuser= new HashMap<>();
                        hobiuser.put("iduser",currentUser.getUid());
                        hobiuser.put("fotoprofil",usr.getImage());
                        hobiuser.put("namaprofil", usr.getFullname());
                        databasehadir.child(currentUser.getUid()).setValue(hobiuser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                attendent.setVisibility(View.GONE);
                unAttendant.setVisibility(View.VISIBLE);
            }
        });

    }
}
