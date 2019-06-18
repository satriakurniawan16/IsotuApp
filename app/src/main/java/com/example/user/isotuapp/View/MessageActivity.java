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
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.isotuapp.Controller.MessageAdapter;
import com.example.user.isotuapp.Model.Chat;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.APIService;
import com.example.user.isotuapp.utils.Constants;
import com.example.user.isotuapp.utils.IMethodCaller;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity implements IMethodCaller {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    LinearLayout btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    String userid;

    APIService apiService;

    private ActionMode mActionMode;

    private ArrayList<String> mDataId;

    boolean notify = false;

    LinearLayout addFriendLayout;

    TextView nameUser;

    DatabaseReference databasecontact;

    Button addFriendContact;

    LinearLayout addPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cari Teman");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(MessageActivity.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

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
        addPicture = findViewById(R.id.addPict);

        intent = getIntent();
        userid = intent.getStringExtra("id");
        addFriendContact = findViewById(R.id.add_contact_lol);
        addFriendLayout = findViewById(R.id.add_friend);
        nameUser = findViewById(R.id.name_user);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("user").child(userid);

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        loaddata();

        addFriendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessageActivity.this, "LOLL", Toast.LENGTH_SHORT).show();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                username.setText(user.getFullname());
                nameUser.setText("Anda belum menambahkan "+user.getFullname()+" ke kontak");
                if (user.getImage().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImage()).into(profile_image);
                }

                readMesagges(fuser.getUid(), userid, user.getImage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(), userid, msg,"","","");
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
    }

    private void loaddata() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = auth.getCurrentUser();
        databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).child("contactadded");
        databasecontact.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("singlestatus", "onDataChange: " + dataSnapshot);
                boolean status = false;
                boolean mystatus = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("Allstatus", "onDataChange: " + ds);
                    if (userid.equals(ds.getKey())) {
                        status = true;
                        break;
                    }
                    if (userid.equals(currentUser.getUid())) {
                        mystatus = true;
                        break;
                    }
                }

                if (status == true) {
                    addFriendLayout.setVisibility(View.GONE);
                } else {
                    addFriendLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "Pesan baru",
                            userid,"message",fuser.getUid());

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void addContact(final Contact contact) {
        databasecontact.child(userid).setValue(contact).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
                        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                addNotification(contact.getUserid(),contact.getUserid(),"Menambahkan anda sebagai teman");
                                sendNotifiactionFriend(userid,user.getFullname(),"Menambahkan anda ke kontak",userid,userid);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                            }
                });
        addFriendLayout.setVisibility(View.GONE);
    }

    private void addNotification(String userid, String postid,String text){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        FirebaseAuth authuser = FirebaseAuth.getInstance();
        FirebaseUser mUser = authuser.getCurrentUser();
        String key;
        key = reference.push().getKey();
        hashMap.put("id",key);
        hashMap.put("userid", mUser.getUid());
        hashMap.put("text", text);
        hashMap.put("postid", mUser.getUid());
        hashMap.put("ispost", true);
        hashMap.put("type", "1");
        reference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MessageActivity.this, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotifiactionFriend(String receiver, final String username, final String message,final String userid,final String idpost){
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mauth.getCurrentUser();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+" "+message, username,
                            userid,"profile", idpost);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
//                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void readMesagges(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl, new MessageAdapter.ClickHandler() {
                        @Override
                        public void onItemClick(int position) {
                            Chat chat1 = mchat.get(position);
                            if(!chat1.getIdpost().equals("")){
                                Intent intent = new Intent(MessageActivity.this,PostActivity.class);
                                intent.putExtra(Constants.EXTRA_POST,chat1.getIdpost());
                                startActivity(intent);
                            }
                        }

                        @Override
                        public boolean onItemLongClick(int position) {
                            if (mActionMode != null) return false;
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MessageActivity.this);

                            View mView = getLayoutInflater().inflate(R.layout.popup_message,
                                    null);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
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
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    final HashMap<String, Object> user= new HashMap<>();
                    user.put("id",userid);
                    user.put("type","message");
                    user.put("date",ServerValue.TIMESTAMP);
                    chatRef.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
                final HashMap<String, Object> userlol= new HashMap<>();
        userlol.put("id",userid);
        userlol.put("type","message");
        userlol.put("date",ServerValue.TIMESTAMP);
        chatRefReceiver.setValue(userlol);

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

    public void addContact(View view) {
        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(userid);
        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                        addContact(new Contact(userid,user.getFullname(),user.getImage(),user.getJurusan(),user.getFakultas(),user.getSearch()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}