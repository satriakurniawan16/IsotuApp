package com.example.user.isotuapp.Controller;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.isotuapp.Model.Post;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final ClickHandler mClickHandler;
    private final Context mContext;
    private final ArrayList<Post> mData;
    private final ArrayList<Integer> mSelected;

    public PostAdapter(Context context, ArrayList<Post> data, ClickHandler handler){
        mContext = context;
        mData = data;
        mClickHandler = handler;
        mSelected = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Post post = mData.get(position);

        String postDate = (String) DateUtils.getRelativeTimeSpanString(post.getTimeCreated());
        String photoUser = post.getUser().getImage();
        Picasso.get().load(photoUser)
                .centerCrop()
                .into(holder.fotoUserImageView);
        holder.namaUserTextView.setText(post.getUser().getFullname());
        holder.tanggalTextView.setText(postDate);
        Picasso.get().load(post.getImage()).fit().centerInside().into(holder.fotoPostTextView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
//                holder.progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(mContext, "Cannot load the Images", Toast.LENGTH_SHORT).show();
            }
        });
        holder.keteranganTextView.setText(post.getText());
        if (selectionCount()==0)
            holder.itemView.setBackgroundColor(android.R.attr.selectableItemBackground);
        //Like
        DatabaseReference countLike = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostId()).child("like");
        countLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(holder.mCurrentUser.getUid())){
                    holder.likePostThumbs.setImageResource(R.drawable.fill_like);
                }
                if (dataSnapshot.exists()){
                    String likeCouunt = String.valueOf(dataSnapshot.getChildrenCount());
                    holder.jmlhLike.setText(likeCouunt);
                }else {
                    int kosong = 0;
                    holder.jmlhLike.setText(String.valueOf(kosong));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Jumlah Komment
        DatabaseReference countComment = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostId()).child("contribute");
        countComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    int kosong = 0;
                    holder.jmlhKoment.setText(String.valueOf(kosong));
                }else {
                    String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                    holder.jmlhKoment.setText(commentCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Jumlah Share
        DatabaseReference countShare = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostId()).child("contribute");
        countComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    int kosong = 0;
                    holder.jmlhKoment.setText(String.valueOf(kosong));
                }else {
                    String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                    holder.jmlhKoment.setText(commentCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private int selectionCount(){
        return mSelected.size();
    }

    public ArrayList<Integer> getmSelected() {
        return mSelected;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView fotoUserImageView;
        final TextView namaUserTextView;
        final TextView tanggalTextView;
        final ImageView fotoPostTextView;
        final ImageView likePostThumbs;
        final TextView keteranganTextView;
        final TextView jmlhLike;
        final TextView jmlhKoment;
        final TextView jmlhShare;
        FirebaseUser mCurrentUser;

        ViewHolder(View itemView) {
            super(itemView);
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            fotoUserImageView = itemView.findViewById(R.id.image_profile);
            namaUserTextView = itemView.findViewById(R.id.name_profile);
            tanggalTextView = itemView.findViewById(R.id.date_posting);
            fotoPostTextView = itemView.findViewById(R.id.image_posting);
            likePostThumbs = itemView.findViewById(R.id.status_like);
            keteranganTextView = itemView.findViewById(R.id.caption_posting);
            jmlhLike = itemView.findViewById(R.id.number_like);
            jmlhKoment = itemView.findViewById(R.id.number_comment);
            jmlhShare = itemView.findViewById(R.id.number_share);
            itemView.setFocusable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            if (selectionCount() > 0 && !mSelected.contains(getAdapterPosition()))
                itemView.setBackgroundColor(Color.LTGRAY);
            else itemView.setBackgroundColor(android.R.attr.selectableItemBackground);
            mClickHandler.onItemClick(getAdapterPosition());
        }
    }

//    private String convertTime(long time){
//        Date date = new Date(time);
//        Format format = new SimpleDateFormat("dd/MM/yyyy");
//        return format.format(date);
//    }

    interface ClickHandler {
        void onItemClick(int position);
    }
}
