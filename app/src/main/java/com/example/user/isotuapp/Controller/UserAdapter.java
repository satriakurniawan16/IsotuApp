package com.example.user.isotuapp.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.isotuapp.Model.Chat;
import com.example.user.isotuapp.Model.Chatlist;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.Grup;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.GrupMessageActivity;
import com.example.user.isotuapp.View.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private ClickHandler mClickHandler;
    private ArrayList<Chatlist> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    private View mEmptyView;
    private boolean ischat;

    String theLastMessage,nameUser,fullname;

    public UserAdapter(Context context, ArrayList<Chatlist> data, ArrayList<String> dataId,View emptyView,boolean ischat,
                          UserAdapter.ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
        mEmptyView = emptyView;
        this.ischat = ischat;
    }

    public void updateEmptyView() {
        if (mData.size() == 0)
            mEmptyView.setVisibility(View.VISIBLE);
        else
            mEmptyView.setVisibility(View.GONE);
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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Chatlist chatlist = mData.get(position);
        if(chatlist.getType().equals("message")){
            DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(chatlist.getId());
            dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class);
                    holder.username.setText(user.getFullname());
                    Log.d("tolo", "isi"+user.getEmail());
                    if (user.getImage().equals("default")){
                        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(mContext).load(user.getImage()).into(holder.profile_image);
                    }

                    if (ischat){
                        lastMessage(user.getUid(), holder.last_msg,"message","");
                    } else {
                        holder.last_msg.setVisibility(View.GONE);
                    }

                    if (ischat) {
                        if (user.getStatus() != null) {
                            if (user.getStatus().equals("online")) {
                                holder.img_on.setVisibility(View.VISIBLE);
                                holder.img_off.setVisibility(View.GONE);
                            } else {
                                holder.img_on.setVisibility(View.GONE);
                                holder.img_off.setVisibility(View.VISIBLE);
                            }
                        } else {
                            holder.img_on.setVisibility(View.GONE);
                            holder.img_off.setVisibility(View.GONE);
                        }
                    }
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("tolol", "onClick: ");
                            Intent intent = new Intent(mContext, MessageActivity.class);
                            intent.putExtra("id",user.getUid());
                            mContext.startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(chatlist.getType().equals("grup")){
            final Chatlist grup = mData.get(position);
            DatabaseReference dbgrup = FirebaseDatabase.getInstance().getReference("group").child(grup.getId());
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
            dbgrup.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final Grup newGrup = dataSnapshot.getValue(Grup.class);
                    holder.username.setText(newGrup.getNamagrup());
                    if (newGrup.getImagegrup().equals("default")){
                        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(mContext).load(newGrup.getImagegrup()).into(holder.profile_image);
                    }

                    if (ischat){
                        lastMessage(newGrup.getIdgrup(), holder.last_msg,"grup",grup.getSubtype());
                    } else {
                        holder.last_msg.setVisibility(View.GONE);
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("tolol", "onClick: ");
                            Intent intent = new Intent(mContext, GrupMessageActivity.class);
                            intent.putExtra("id",newGrup.getIdgrup());
                            mContext.startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
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

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg,final String type,final String subtype){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            nameUser = chat.getSender();
                        }

                        if (chat.getReceiver().equals(userid) && !chat.getSender().equals(firebaseUser.getUid()) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            nameUser = chat.getSender();
                        }


                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        if(type.equalsIgnoreCase("message")) {
                            last_msg.setText(theLastMessage);
                        }else if(type.equalsIgnoreCase("grup")){
                            final String message = theLastMessage;
                            DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(nameUser);
                            dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if(subtype.equals("0")){
                                        last_msg.setText(user.getFullname() +" : "+message);
                                    }else{
                                        last_msg.setText(message);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface ClickHandler {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

}