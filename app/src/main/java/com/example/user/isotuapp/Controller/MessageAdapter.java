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
import com.example.user.isotuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    private ClickHandler mClickHandler;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl,ClickHandler handler){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        mClickHandler = handler;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        holder.nameUser.setVisibility(View.GONE);

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
        public TextView shareStatus ;
        public TextView nameUser ;



        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            createdby = itemView.findViewById(R.id.user_share);
            imagePost = itemView.findViewById(R.id.imageShare);
            nameUser = itemView.findViewById(R.id.name_user);
            shareStatus = itemView.findViewById(R.id.status_share);
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
