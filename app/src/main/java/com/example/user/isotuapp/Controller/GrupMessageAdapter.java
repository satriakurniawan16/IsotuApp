package com.example.user.isotuapp.Controller;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class GrupMessageAdapter extends RecyclerView.Adapter<GrupMessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private ClickHandler mClickHandler;

    FirebaseUser fuser;

    public GrupMessageAdapter (Context mContext, List<Chat> mChat,ClickHandler handler){
        this.mChat = mChat;
        this.mContext = mContext;
        mClickHandler = handler;
    }

    @NonNull
    @Override
    public GrupMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new GrupMessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new GrupMessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GrupMessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(chat.getSender());
        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImage().equals("default")){
                    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(mContext).load(user.getImage()).into(holder.profile_image);
                }
                holder.nameUser.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(!chat.getImagepost().equals("")){
            holder.imagePost.setVisibility(View.VISIBLE);
            Picasso.get().load(chat.getImagepost()).into(holder.imagePost);
        }else{
            holder.imagePost.setVisibility(View.GONE);
        }

        if(!chat.getUserpost().equals("")){
            holder.createdby.setVisibility(View.VISIBLE);
            holder.shareStatus.setVisibility(View.VISIBLE);
            holder.createdby.setText("by : "+chat.getUserpost());
        }else{
            holder.shareStatus.setVisibility(View.GONE);
            holder.createdby.setVisibility(View.GONE);
        }


        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Dilihat");
            } else {
                holder.txt_seen.setText("Terkirim");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }



    public  class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView createdby;
        public ImageView imagePost;
        public TextView nameUser;
        public TextView shareStatus ;



        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            createdby = itemView.findViewById(R.id.user_share);
            imagePost = itemView.findViewById(R.id.imageShare);
            shareStatus = itemView.findViewById(R.id.status_share);
            nameUser = itemView.findViewById(R.id.name_user);
            itemView.setFocusable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
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

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public interface ClickHandler {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }
}
