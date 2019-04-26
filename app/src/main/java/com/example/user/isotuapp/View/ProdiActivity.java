package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Controller.SearchUserAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.APIService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdiActivity extends AppCompatActivity {

    private ArrayList<User> mData;
    private ArrayList<String> mDataId;
    private SearchUserAdapter mAdapter;
    Spinner spinnerText,spinnerProdi;
    RecyclerView recyclerView;
    FirebaseUser fuser;
    String type;
    APIService apiService;
    String ref;
    String myid;
    DatabaseReference reference,myreference,databasecontact;
    private ActionMode mActionMode;
    LinearLayout emptyView;


    private String[] fakultas = {
            "Fakultas Teknik Elektro",
            "Fakultas Informatika",
            "Fakultas Rekayasa Industri",
            "Fakultas Ekonomi dan Bisnis",
            "Fakultas Industri Kreatif",
            "Fakultas Ilmu Terapan",
            "Fakultas Komunikasi dan Bisnis"
    };

    private String[] jurusanfif = {
            "S1 Teknik Informatika",
            "S1 Teknik Informatika (International)",
            "S2 Teknik Informatika",
            "S1 Teknologi Informasi"
    };

    private String[] jurusanfte = {
            "S1 Teknik Telekomunikasi",
            "S1 Teknik Fisika",
            "S2 Teknik Telekomunikasi (International)",
            "S1 Sistem Komputer",
            "S1 Teknik Elektro",
            "S1 Teknik Elektro (International)",
            "S2 Teknik Telekomunikasi"
    };

    private String[] jurusanfeb = {
            "S1 International ICT Business",
            "S1 Akuntansi",
            "S1 MBTI",
            "S1 Manajemen"
    };

    private String[] jurusanfri = {
            "S1 Teknik Industri",
            "S1 Sistem Informasi",
            "S1 Teknik Industri (International)",
            "S2 Teknik Industri)"
    };

    private String[] jurusanfit = {
            "D3 Rekayasa Perangkat Lunak Aplikasi",
            "D3 Sistem Informasi",
            "D3 Sistem Informasi Akuntansi",
            "D3 Teknologi Telekomunikasi",
            "D3 Manajemen Pemasaran",
            "D3 Perhotelan",
            "D3 Teknik Komputer",
            "D4 Sistem Multimedia"
    };

    private String[] jurusanfik = {
            "S1 Desain Komunikasi Visual",
            "S1 Kriya dan Tekstil",
            "S1 Desain Komunikasi Visual (International)",
            "S1 Desain Interior",
            "S1 Creative Arts",

    };

    private String[] jurusanfkb = {
            "S1 Administrasi Bisnis",
            "S1 Ilmu Komunikasi",
            "S1 Administrasi Bisnis (International)",
            "S1 Digital Public Relations",
            "S1 Ilmu Komunikasi (International)",

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cari Teman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(ProdiActivity.this, SearchFriendActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        spinnerText = (Spinner) findViewById(R.id.distance);
        spinnerProdi = (Spinner) findViewById(R.id.prodi);


        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        emptyView = (LinearLayout) findViewById(R.id.emptyview);
        
        spinnerText = (Spinner) findViewById(R.id.distance);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, fakultas);
        spinnerText.setAdapter(adapter);

        recyclerView = (RecyclerView) findViewById(R.id.listfinded);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("user");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mData.add(user);
                }

                if(mData !=  null ) {
                    emptyView.setVisibility(View.GONE);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
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
                                final User pet = mData.get(position);
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProdiActivity.this);

                                View mView = getLayoutInflater().inflate(R.layout.modal_user,
                                        null);

                                ImageView profileImageView = (ImageView) mView.findViewById(R.id.profilepopup);
                                TextView nameTextView = (TextView) mView.findViewById(R.id.nameuserpopup);
                                TextView jurusanTextView = (TextView) mView.findViewById(R.id.jurusanpopup);
                                LinearLayout profileLayout = (LinearLayout) mView.findViewById(R.id.profilebutton);
                                LinearLayout chatLayout = (LinearLayout) mView.findViewById(R.id.chatbutton);
                                final LinearLayout addLayout = (LinearLayout) mView.findViewById(R.id.addbutton);

                                Picasso.get().load(pet.getImage()).into(profileImageView);
                                nameTextView.setText(pet.getFullname());
                                jurusanTextView.setText(pet.getJurusan());
                                profileLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ProdiActivity.this,FriendProfile.class);
                                        intent.putExtra("iduser",pet.getUid());
                                        startActivity(intent);
                                    }
                                });


                                databasecontact = FirebaseDatabase.getInstance().getReference("contact").child(fuser.getUid()).child("contactadded");
                                databasecontact.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d("singlestatus", "onDataChange: " + dataSnapshot);
                                        boolean status = false ;
                                        boolean mystatus = false ;
                                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                                            Log.d("Allstatus", "onDataChange: " + ds);
                                            if(pet.getUid().equals(ds.getKey())){
                                                status = true;
                                                break;
                                            }else{
                                                status = false;
                                            }
                                        }

                                        if(status == true){
                                            addLayout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                addLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addContact(new Contact(pet.getUid(),pet.getFullname(),pet.getImage(),pet.getJurusan().toString(),pet.getFakultas().toString(),pet.getSearch()));
                                        addLayout.setVisibility(View.GONE);
                                    }
                                });


                                chatLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ProdiActivity.this, MessageActivity.class);
                                        intent.putExtra("image",pet.getImage());
                                        intent.putExtra("name",pet.getFullname());
                                        intent.putExtra("id",pet.getUid());
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
                                return true;
                            }
                        });
                recyclerView.setAdapter(mAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final ArrayAdapter<String> adapterfit = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfit);

        final ArrayAdapter<String> adapterfik = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfik);

        final ArrayAdapter<String> adapterfkb = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfkb);

        final ArrayAdapter<String> adapterfeb = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfeb);

        final ArrayAdapter<String> adapterfri = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfri);

        final ArrayAdapter<String> adapterfte = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfte);

        final ArrayAdapter<String> adapterfif = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jurusanfif);

        spinnerText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                switch (i){
                    case 0 :
                        jurusan(adapterfte);
                        break;
                    case 1 :
                        jurusan(adapterfif);
                        break;
                    case 2 :
                        jurusan(adapterfri);
                        break;
                    case 3 :
                        jurusan(adapterfeb);
                        break;
                    case 4 :
                        jurusan(adapterfik);
                        break;
                    case 5 :
                        jurusan(adapterfit);
                        break;
                    case 6 :
                        jurusan(adapterfkb);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void searchUsers(String s) {
        mData.clear();
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("user").orderByChild("jurusan")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    mData.add(user);

                }


                if(mData !=  null ) {
                    emptyView.setVisibility(View.GONE);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
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

    public void jurusan (final ArrayAdapter<String> adapterjurusan ){
        spinnerProdi.setAdapter(adapterjurusan);
        spinnerProdi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // memunculkan toast + value Spinner yang dipilih (diambil dari adapter)
                 searchUsers(spinnerProdi.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initdropdown() {
    }

    private void addContact(final Contact contact) {
        databasecontact.child(contact.getUserid()).setValue(contact).
                addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),
                                "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                        addNotification(contact.getUserid(),contact.getUserid(),"Menambahkan anda sebagai teman");
                        sendNotifiaction(contact.getUserid(),contact.getNameuser(),"Menambahkan anda ke kontak",contact.getUserid(),contact.getUserid());
                    }
                });
    }

    private void sendNotifiaction(String receiver, final String username, final String message,final String userid,final String idpost){
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
                                            Toast.makeText(ProdiActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ProdiActivity.this, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProdiActivity.this, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
