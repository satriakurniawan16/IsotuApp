package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.isotuapp.Controller.GrupMessageAdapter;
import com.example.user.isotuapp.Controller.MessageAdapter;
import com.example.user.isotuapp.Model.Chat;
import com.example.user.isotuapp.Model.Grup;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Model.UserHobi;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.APIService;
import com.example.user.isotuapp.utils.Constants;
import com.example.user.isotuapp.utils.IMethodCaller;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GrupMessageActivity extends AppCompatActivity implements IMethodCaller {

    CircleImageView profile_image;
    TextView username;

    ImageView setting;
    FirebaseUser fuser;
    DatabaseReference reference;

    LinearLayout btn_send;
    EditText text_send;

    GrupMessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    String userid;

    APIService apiService;

    private ActionMode mActionMode;

    private ArrayList<String> mDataId;

    String namuser = "";
    boolean notify = false;
    String namegrup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cari Teman");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(GrupMessageActivity .this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Chats");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userbro = dataSnapshot.getValue(User.class);
                namegrup = userbro.getFullname();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(GrupMessageActivity.this);

                View mView = getLayoutInflater().inflate(R.layout.modal_setting_grup,
                        null);

                TextView settingEdit = mView.findViewById(R.id.settingedit);
                TextView settingMember = mView.findViewById(R.id.settingmember);
                TextView settingInvite = mView.findViewById(R.id.settinginvite);
                TextView settingExit = mView.findViewById(R.id.settingexit);


                settingInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GrupMessageActivity.this,InviteFriendActivity.class);
                        intent.putExtra("idgrup",userid);
                        startActivity(intent);
                    }
                });

                settingExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("group").child(fuser.getUid());
                        db.child(userid).removeValue();
                        DatabaseReference dbexit = FirebaseDatabase.getInstance().getReference("ChatlistGrup").child(fuser.getUid());
                        dbexit.child(userid).removeValue();
                        db.child(userid).removeValue();

                        DatabaseReference member = FirebaseDatabase.getInstance().getReference("groupmember").child(userid);
                        member.child(fuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                MessageExit(FirebaseAuth.getInstance().getCurrentUser().getUid(), userid, user.getFullname()+" keluar grup" ,"","","");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(GrupMessageActivity.this,Dashboard.class);
                        startActivity(intent);
                    }
                });

                settingMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GrupMessageActivity.this,DetailUserHobi.class);
                        intent.putExtra("reference", "groupmember");
                        intent.putExtra("child", userid);
                        intent.putExtra("title", "Daftar Anggota");
                        startActivity(intent);
                    }
                });

                settingEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GrupMessageActivity.this,EditGroupActivity.class);
                        intent.putExtra("id",userid);
                        startActivity(intent);
                    }
                });



                mBuilder.setView(mView);
                final AlertDialog dialognya = mBuilder.create();
                dialognya.show();
            }
        });

        intent = getIntent();
        userid = intent.getStringExtra("id");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("group").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Grup user = dataSnapshot.getValue(Grup.class);
                username.setText(user.getNamagrup());
                namegrup = user.getNamagrup();
                if (user.getImagegrup().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImagegrup()).into(profile_image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        readMesagges(fuser.getUid(), userid, "");
        seenMessage(userid);



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(), userid, msg,"","","");
                } else {
                    Toast.makeText(GrupMessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
    }




    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, namegrup,
                            userid,"newgrup",userid);

                    Sender sender = new Sender(data, "topics/"+userid);

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(GrupMessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(userid)){
                        mchat.add(chat);
                    }

                    messageAdapter = new GrupMessageAdapter(GrupMessageActivity.this, mchat, new GrupMessageAdapter.ClickHandler() {
                        @Override
                        public void onItemClick(int position) {
                            Chat chat1 = mchat.get(position);
                            if(!chat1.getIdpost().equals("")){
                                Intent intent = new Intent(GrupMessageActivity.this,PostActivity.class);
                                intent.putExtra(Constants.EXTRA_POST,chat1.getIdpost());
                                startActivity(intent);
                            }
                        }

                        @Override
                        public boolean onItemLongClick(int position) {
                            if (mActionMode != null) return false;
                            final Chat chat1 = mchat.get(position);
                            if(chat1.getSender().equals(fuser.getUid())){
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(GrupMessageActivity.this);
                                View mView = getLayoutInflater().inflate(R.layout.popup_message,
                                        null);
                                LinearLayout hapus = mView.findViewById(R.id.hapus);

                                mBuilder.setView(mView);
                                final AlertDialog dialog = mBuilder.create();
                                dialog.show();
                                hapus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        Toast.makeText(GrupMessageActivity.this, "lol : " + chat1.getId(), Toast.LENGTH_SHORT).show();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
                                        ref.child(chat1.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                                messageAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });
                            }
                            return true;
                        }
                    });
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void sendMessage(String sender, final String receiver, String message, String imagePost,String nameUser,String idpost) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        String key = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", key);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("idpost",idpost);
        hashMap.put("imagepost",imagePost);
        hashMap.put("userpost",nameUser);
        hashMap.put("isseen", false);
        hashMap.put("type", "0");

        reference.child(key).setValue(hashMap);
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatlistGrup")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    final HashMap<String, Object> user= new HashMap<>();
                    user.put("id",userid);
                    user.put("type","grup");
                    user.put("subtype","1");
                    chatRef.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatlistGrup");

        DatabaseReference mygrup = FirebaseDatabase.getInstance().getReference("groupmember").child(userid);
        mygrup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserHobi userHobi = snapshot.getValue(UserHobi.class);
                    final HashMap<String, Object> userdung= new HashMap<>();
                    userdung.put("id",userid);
                    userdung.put("type","grup");
                    userdung.put("subtype","0");
                    if(userHobi.getIduser() != fuser.getUid()){
                        chatRefReceiver.child(userHobi.getIduser())
                                .child(userid).setValue(userdung);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getFullname(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void MessageExit(String sender, final String receiver, String message, String imagePost, String nameUser, String idpost) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        String key = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", key);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("idpost",idpost);
        hashMap.put("imagepost",imagePost);
        hashMap.put("userpost",nameUser);
        hashMap.put("isseen", false);
        hashMap.put("type", "1");

        reference.child(key).setValue(hashMap);


        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatlistGrup");

        DatabaseReference mygrup = FirebaseDatabase.getInstance().getReference("groupmember").child(userid);
        mygrup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserHobi userHobi = snapshot.getValue(UserHobi.class);
                    final HashMap<String, Object> userdung= new HashMap<>();
                    userdung.put("id",userid);
                    userdung.put("type","grup");
                    userdung.put("subtype","0");
                    if(userHobi.getIduser() != fuser.getUid()){
                        chatRefReceiver.child(userHobi.getIduser())
                                .child(userid).setValue(userdung);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getFullname(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }
    }