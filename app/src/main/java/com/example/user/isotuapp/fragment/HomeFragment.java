package com.example.user.isotuapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.CompleteProfile;
import com.example.user.isotuapp.View.PostActivity;
import com.example.user.isotuapp.View.Posting;
import com.example.user.isotuapp.utils.Constants;
import com.example.user.isotuapp.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;


public class HomeFragment extends Fragment implements Serializable {

    View rootView;
    private View mRootVIew;
    private FirebaseRecyclerAdapter<Post, PostHolder> mPostAdapter;
    private RecyclerView mPostRecyclerView;
    private boolean statuslike = false;
    private Parcelable recyclerViewState;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootVIew = inflater.inflate(R.layout.fragment_home, container, false);
        init();

        return mRootVIew;
    }

    private void init() {
        mPostRecyclerView = (RecyclerView) mRootVIew.findViewById(R.id.listPosting);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        mPostRecyclerView.setLayoutManager(linearLayoutManager);
        setupAdapter();
        mPostRecyclerView.setAdapter(mPostAdapter);
    }

    private void setupAdapter() {
        mPostAdapter = new FirebaseRecyclerAdapter<Post, PostHolder>(
                Post.class,
                R.layout.list_item_post,
                PostHolder.class,
                FirebaseUtils.getPostRef()
        ) {
            @Override
            protected void populateViewHolder(final PostHolder viewHolder, final Post model, int position) {
                viewHolder.setNumCOmments(String.valueOf(model.getNumComment()));
                viewHolder.setNumLikes(String.valueOf(model.getNumlikes()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                viewHolder.setUsername(model.getUser().getUsername());
                viewHolder.setPostText(model.getText());


                Glide.with(getActivity())
                        .load(model.getUser().getImage())
                        .into(viewHolder.postOwnerDisplayImageView);

                if (model.getImage() != null) {
                    viewHolder.postDisplayImageVIew.setVisibility(View.VISIBLE);
                    Picasso.get().load(model.getImage()).fit()
                            .into(viewHolder.postDisplayImageVIew);

                } else {
                    viewHolder.postDisplayImageVIew.setImageBitmap(null);
                    viewHolder.postDisplayImageVIew.setVisibility(View.GONE);
                }
                viewHolder.postLikeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.postLikeLayout.setEnabled(false);

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // This method will be executed once the timer is over
                                viewHolder.postLikeLayout.setEnabled(true);
                            }
                        },3000);
                        onLikeClick(model.getPostId());
                    }
                });

                viewHolder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, model.getPostId());
                        startActivity(intent);
                    }
                });
            }
        };
    }

    //During the tutorial I think I messed up this code. Make sure your's aligns to this, or just
    //check out the github code
    private void onLikeClick(final String postId) {
        Toast.makeText(getContext(), "terclick", Toast.LENGTH_SHORT).show();
        FirebaseUtils.getPostLikedRef(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            //User liked
                            FirebaseUtils.getPostRef()
                                    .child(postId)
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            long num = (long) mutableData.getValue();
                                            mutableData.setValue(num - 1);
                                            statuslike = false;
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(postId)
                                                    .setValue(null);
                                        }
                                    });
                        } else {
                            FirebaseUtils.getPostRef()
                                    .child(postId)
                                    .child(Constants.NUM_LIKES_KEY)
                                    .runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            long num = (long) mutableData.getValue();
                                            mutableData.setValue(num + 1);
                                            statuslike = true ;
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(postId)
                                                    .setValue(true);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static class PostHolder extends RecyclerView.ViewHolder {
        ImageView postOwnerDisplayImageView;
        TextView postOwnerUsernameTextView;
        TextView postTimeCreatedTextView;
        ImageView postDisplayImageVIew;
        ImageView imagelike;
        TextView postTextTextView;
        LinearLayout postLikeLayout;
        LinearLayout postCommentLayout;
        TextView postNumLikesTextView;
        TextView postNumCommentsTextView;


        public PostHolder(View itemView) {
            super(itemView);
            postOwnerDisplayImageView = (ImageView) itemView.findViewById(R.id.image_profile);
            postOwnerUsernameTextView = (TextView) itemView.findViewById(R.id.name_profile);
            postTimeCreatedTextView = (TextView) itemView.findViewById(R.id.date_posting);
            postDisplayImageVIew = (ImageView) itemView.findViewById(R.id.image_posting);
            postLikeLayout = (LinearLayout) itemView.findViewById(R.id.like_posting);
            postCommentLayout = (LinearLayout) itemView.findViewById(R.id.comment_posting);
            postNumLikesTextView = (TextView) itemView.findViewById(R.id.number_like);
            postNumCommentsTextView = (TextView) itemView.findViewById(R.id.number_comment);
            postTextTextView = (TextView) itemView.findViewById(R.id.caption_posting);
            imagelike = (ImageView) itemView.findViewById(R.id.status_like);
        }


        public void setUsername(String username) {
            postOwnerUsernameTextView.setText(username);
        }

        public void setTIme(CharSequence time) {
            postTimeCreatedTextView.setText(time);
        }

        public void setNumLikes(String numLikes) {
            postNumLikesTextView.setText(numLikes);
        }

        public void setNumCOmments(String numComments) {
            postNumCommentsTextView.setText(numComments);
        }

        public void setPostText(String text) {
            postTextTextView.setText(text);
        }

    }
}
