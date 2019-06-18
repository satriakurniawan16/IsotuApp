package com.example.user.isotuapp.Controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.Dashboard;
import com.example.user.isotuapp.View.MessageActivity;
import com.example.user.isotuapp.fragment.APIService;
import com.example.user.isotuapp.utils.IMethodCaller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteAdapter  extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private Context mContext;
    private FragmentActivity mActivity;
    private ArrayList<Contact> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    APIService apiService;
    private String mIdgrup,namagrup;
    boolean notify = false;
    boolean condition = false;
    private DatabaseReference chatRef;
    String id;

    public InviteAdapter(Context mContext, ArrayList<Contact> mData, ArrayList<String> mDataId, String mIdgrup, String namagrup) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataId = mDataId;
        this.mIdgrup = mIdgrup;
        this.namagrup = namagrup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_invite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Contact pet = mData.get(position);
        holder.nameTextView.setText(pet.getNameuser());
        holder.keteranganTextView.setText(pet.getMajoruser()+", "+pet.getFacultyuser());
        Picasso.get().load(pet.getImageuser()).into(holder.profilImageview);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(pet.getUserid().equals(currentUser.getUid())){
            holder.rootLayout.setVisibility(View.GONE);
        }
        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if(condition == false){
                            addNotification(pet.getUserid(),mIdgrup);
                            sendNotifiaction(pet.getUserid(),user.getFullname(),user.getFullname() + "Mengundang anda ke grup");
                            sendMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), mIdgrup, user.getFullname()+" mengundang "+ pet.getNameuser() + " ke grup" ,"","","");
                            condition = true;
                            Drawable d = mContext.getResources().getDrawable(R.drawable.button_white);
                            holder.inviteButton.setText("Invited");
                            holder.inviteButton.setBackground(d);
                            holder.inviteButton.setTextColor(Color.parseColor("#000000"));
                        }else{
                            deleteNotifications(mIdgrup,pet.getUserid());
                            condition = false;
                            Drawable d = mContext.getResources().getDrawable(R.drawable.button_main);
                            holder.inviteButton.setText("Invite");
                            holder.inviteButton.setBackground(d);
                            holder.inviteButton.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void toggleSelection(String dataId) {
        if (mSelectedId.contains(dataId))
            mSelectedId.remove(dataId);
        else
            mSelectedId.add(dataId);
        notifyDataSetChanged();
    }

    public int selectionCount() {
        return mSelectedId.size();
    }

    public void resetSelection() {
        mSelectedId = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedId() {
        return mSelectedId;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView keteranganTextView;
        final ImageView profilImageview;
        final Button inviteButton;
        final LinearLayout rootLayout;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nama_contact_invite);
            keteranganTextView = itemView.findViewById(R.id.keterangan_contact_invite);
            profilImageview = itemView.findViewById(R.id.image_contact_invite);
            inviteButton = itemView.findViewById(R.id.button_invite);
            rootLayout = itemView.findViewById(R.id.rootlayout);
            itemView.setFocusable(true);
        }
    }

    private void sendNotifiaction(final String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Query query = tokens.orderByKey().equalTo(receiver);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = auth.getCurrentUser();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher,  message , username,
                            receiver,"grup","");

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        FirebaseAuth authuser = FirebaseAuth.getInstance();
        FirebaseUser mUser = authuser.getCurrentUser();
        String key;
        key = reference.push().getKey();
        hashMap.put("id",key);
        hashMap.put("userid", mUser.getUid());
        hashMap.put("text", "mengundang anda ke grup");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("type", "2");
        hashMap.put("date", System.currentTimeMillis());
        reference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(String sender, final String receiver, String message, String imagePost, String nameUser, String idpost) {

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
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(receiver);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    final HashMap<String, Object> user= new HashMap<>();
                    user.put("id",receiver);
                    user.put("type","grup");
                    user.put("subtype","1");
                    chatRef.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        chatRefReceiver.child("id").setValue(FirebaseAuth.getInstance().getCurrentUser());

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
//                    sendNotifiaction(receiver, user.getFullname(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}