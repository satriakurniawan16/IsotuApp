package com.example.user.isotuapp.Controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

public class ShareAdapter  extends RecyclerView.Adapter<ShareAdapter.ViewHolder> implements IMethodCaller {
    private Context mContext;
    private FragmentActivity mActivity;
    private ArrayList<Contact> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    APIService apiService;
    private String mIdpost;
    boolean notify = false;
    private DatabaseReference chatRef;
    String id;

    public ShareAdapter(Context context, ArrayList<Contact> data, ArrayList<String> dataId,String idpost) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mSelectedId = new ArrayList<>();
        mIdpost = idpost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_share, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Contact pet = mData.get(position);
        holder.nameTextView.setText(pet.getNameuser());
        holder.keteranganTextView.setText(pet.getMajoruser()+", "+pet.getFacultyuser());
        Picasso.get().load(pet.getImageuser()).into(holder.profilImageview);
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                final String sender =  currentUser.getUid();
                final String receiver = pet.getUserid();
                final Context context;
                context = mContext;
                final String idpost = mIdpost;
                DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("posting").child(mIdpost);
                DatabaseReference dbfnumbershare = FirebaseDatabase.getInstance().getReference("posting").child(mIdpost).child("numshare");
                dbfnumbershare.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        mutableData.setValue(num + 1);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });
                dbf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);
                        String message = post.getText();
                        String imagepost = "";
                        if(post.getImage() != null) {
                            imagepost = post.getImage();
                        }
                        String nameuser = post.getUser().getFullname();
                        sendMessage(sender,receiver,message,imagepost,nameuser,idpost);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                id = pet.getUserid();
                holder.shareButton.setText("Terkirim");
                holder.shareButton.setTextColor(Color.BLACK);
                Toast.makeText(mContext, "ini : " + mIdpost, Toast.LENGTH_SHORT).show();
                Log.d("plisboiii", "onClick: " + mIdpost);
                holder.shareButton.setClickable(false);
                holder.shareButton.setBackgroundResource(R.drawable.button_white);
            }
        });
        holder.itemView.setSelected(mSelectedId.contains(mDataId.get(position)));
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
        final Button shareButton;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nama_contact_share);
            keteranganTextView = itemView.findViewById(R.id.keterangan_contact_share);
            profilImageview = itemView.findViewById(R.id.image_contact_share);
            shareButton = itemView.findViewById(R.id.button_share_post);
            itemView.setFocusable(true);
        }
    }

    private void sendNotifiaction(String receiver, final String username, final String message){
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
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            id);

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
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fuser = auth.getCurrentUser();
        reference.child(key).setValue(hashMap);
         chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(id);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(id)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());

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