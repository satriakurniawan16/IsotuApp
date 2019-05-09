package com.example.user.isotuapp.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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

import com.example.user.isotuapp.Model.Grup;
import com.example.user.isotuapp.Model.NotifModel;
import com.example.user.isotuapp.Model.Organiasasi;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.FriendProfile;
import com.example.user.isotuapp.View.NotificationActivity;
import com.example.user.isotuapp.View.PostActivity;
import com.example.user.isotuapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<NotifModel> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    LinearLayout emptyView;
    boolean join = false;

    public NotificationAdapter(Context context, ArrayList<NotifModel> data, ArrayList<String> dataId,
                             ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final NotifModel pet = mData.get(position);
        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(pet.getUserid());
        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImage()).into(holder.imageuserImageView);
                holder.nameUserTextView.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.notifLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "MANTAP : " + position, Toast.LENGTH_SHORT).show();
                if(pet.getType().equals("0")){
                    Intent intent =  new Intent(mContext,PostActivity.class);
                    intent.putExtra(Constants.EXTRA_POST,pet.getPostid());
                    mContext.startActivity(intent);
                }else if(pet.getType().equals("1")){
                    Intent intent =  new Intent(mContext,FriendProfile.class);
                    intent.putExtra("iduser",pet.getPostid());
                    mContext.startActivity(intent);
                }else if(pet.getType().equals("2")){

                    View mView = LayoutInflater.from(mContext).inflate(R.layout.confirmation_grup, null);
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                    mBuilder.setView(mView);

                    final ImageView imageGrup = mView.findViewById(R.id.group_image);
                    final TextView nameGrup = mView.findViewById(R.id.group_name);
                    final Button joinButton = (Button) mView.findViewById(R.id.join_grup);

                    final DatabaseReference mygrup = FirebaseDatabase.getInstance().getReference("group").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mygrup.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                Log.d("grupexist", "onDataChange: " + ds);
                                if(pet.getPostid().equals(ds.getKey())){
                                    Drawable d = mContext.getResources().getDrawable(R.drawable.button_white);
                                    joinButton.setBackground(d);
                                    joinButton.setText("Telah bergabung");
                                    joinButton.setTextColor(Color.parseColor("#000000"));
                                    join = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    DatabaseReference dbgroup = FirebaseDatabase.getInstance().getReference("group").child(pet.getUserid()).child(pet.getPostid());
                    dbgroup.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Grup grup = dataSnapshot.getValue(Grup.class);
                            Picasso.get().load(grup.getImagegrup()).into(imageGrup);
                            nameGrup.setText(grup.getNamagrup());

                            joinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(join == false){
                                        Drawable d = mContext.getResources().getDrawable(R.drawable.button_white);
                                        joinButton.setBackground(d);
                                        joinButton.setText("Telah bergabung");
                                        joinButton.setTextColor(Color.parseColor("#000000"));
                                        join = true;
                                        mygrup.child(pet.getPostid()).setValue(grup);
                                        FirebaseMessaging.getInstance().subscribeToTopic(pet.getPostid());
                                        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                DatabaseReference dbmember = FirebaseDatabase.getInstance().getReference("groupmember").child(pet.getPostid());
                                                final HashMap<String, Object> member= new HashMap<>();
                                                member.put("iduser",user.getUid());
                                                member.put("fotoprofil",user.getImage());
                                                member.put("namaprofil", user.getFullname());
                                                dbmember.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(member);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    final AlertDialog dialoglol = mBuilder.create();
                    dialoglol.show();
                }
            }
        });
        Log.d("testesan", "onBindViewHolder: " +  pet.isIspost());
        if(pet.isIspost() == true ){
            holder.notifLayout.setBackgroundColor(Color.parseColor("#eeeeee"));
        }

        DatabaseReference dbnotif  =  FirebaseDatabase.getInstance().getReference("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pet.getId());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ispost", false);
        dbnotif.updateChildren(hashMap);

        holder.textNotifTextView.setText(pet.getText());
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

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {
        final ImageView imageuserImageView;
        final TextView nameUserTextView;
        final TextView textNotifTextView;
        final LinearLayout notifLayout;

        ViewHolder(View itemView) {
            super(itemView);
            imageuserImageView= (ImageView) itemView.findViewById(R.id.userimagenotif);
            nameUserTextView = (TextView) itemView.findViewById(R.id.namenotif);
            textNotifTextView = (TextView) itemView.findViewById(R.id.textnotif);
            notifLayout = (LinearLayout) itemView.findViewById(R.id.rootnotif);
        }

        @Override
        public void onClick(View itemView) {
            mClickHandler.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mClickHandler.onItemLongClick(getAdapterPosition());
        }
    }

    public interface ClickHandler {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }
}