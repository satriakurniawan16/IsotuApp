package com.example.user.isotuapp.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.isotuapp.Model.Comment;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.utils.Constants;
import com.example.user.isotuapp.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostActivity extends AppCompatActivity {

    private static final String BUNDLE_COMMENT = "comment";
    private Post mPost;
    private EditText mCommentEditTextView;
    private Comment mComment;
    private LinearLayout sent;
    String idpost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if (savedInstanceState != null) {
            mComment = (Comment) savedInstanceState.getSerializable(BUNDLE_COMMENT);
        }

        Intent intent = getIntent();
        idpost = intent.getStringExtra(Constants.EXTRA_POST);
        Log.d("lol", "idpost: " + idpost);
        init();
        initPost();
        initCommentSection();
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    private void initPost() {
        final ImageView postOwnerDisplayImageView = (ImageView) findViewById(R.id.image_profile_detail);
        final TextView postOwnerUsernameTextView = (TextView) findViewById(R.id.name_profile_detail);
        final TextView postTimeCreatedTextView = (TextView) findViewById(R.id.date_posting_detail);
        final ImageView postDisplayImageView = (ImageView) findViewById(R.id.image_posting_detail);
        final LinearLayout postLikeLayout = (LinearLayout) findViewById(R.id.like_posting_detail);
        final LinearLayout postCommentLayout = (LinearLayout) findViewById(R.id.comment_posting_detail);
        final TextView postNumLikesTextView = (TextView) findViewById(R.id.number_like_detail);
        final TextView postNumCommentsTextView = (TextView) findViewById(R.id.number_comment_detail);
        final TextView postTextTextView = (TextView) findViewById(R.id.caption_posting_detail);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbs = FirebaseDatabase.getInstance().getReference("posting").child(idpost);
        dbs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post.getImage() != null) {
                    postDisplayImageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(post.getImage()).fit()
                            .into(postDisplayImageView);
                } else {
                    postDisplayImageView.setImageBitmap(null);
                    postDisplayImageView.setVisibility(View.GONE);
                 }
                Picasso.get().load(post.getUser().getImage()).fit()
                        .into(postOwnerDisplayImageView);
                postOwnerUsernameTextView.setText(post.getUser().getUsername());
                postTimeCreatedTextView.setText(DateUtils.getRelativeTimeSpanString(post.getTimeCreated()));
                 postTextTextView.setText(post.getText());
                 postNumLikesTextView.setText(String.valueOf(post.getNumlikes()));
                 postNumCommentsTextView.setText(String.valueOf(post.getNumComment()));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        mCommentEditTextView = (EditText) findViewById(R.id.isi_comment);
        sent = (LinearLayout) findViewById(R.id.comment_send);
    }


    public static class CommentHolder extends RecyclerView.ViewHolder {
        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;

        public CommentHolder(View itemView) {
            super(itemView);
            commentOwnerDisplay = (ImageView) itemView.findViewById(R.id.image_comment);
            usernameTextView = (TextView) itemView.findViewById(R.id.username_comment);
            timeTextView = (TextView) itemView.findViewById(R.id.date_comment);
            commentTextView = (TextView) itemView.findViewById(R.id.comment_text);
        }

        public void setUsername(String username) {
            usernameTextView.setText(username);
        }

        public void setTime(CharSequence time) {
            timeTextView.setText(time);
        }

        public void setComment(String comment) {
            commentTextView.setText(comment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_COMMENT, mComment);
        super.onSaveInstanceState(outState);
    }

    private void sendComment() {
        final ProgressDialog progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setMessage("Sending comment..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mComment = new Comment();
        final String uid = FirebaseUtils.getUid();
        String strComment = mCommentEditTextView.getText().toString();

        mComment.setCommentId(uid);
        mComment.setComment(strComment);
        mComment.setTimeCreated(System.currentTimeMillis());
        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mComment.setUser(user);
                        FirebaseUtils.getCommentRef(idpost)
                                .child(uid)
                                .setValue(mComment);

                        FirebaseUtils.getPostRef().child(idpost)
                                .child(Constants.NUM_COMMENTS_KEY)
                                .runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        long num = (long) mutableData.getValue();
                                        mutableData.setValue(num + 1);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                        progressDialog.dismiss();
                                        FirebaseUtils.addToMyRecord(Constants.COMMENTS_KEY, uid);
                                        initPost();
                                        mCommentEditTextView.setText("");
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void initCommentSection() {
        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));

        FirebaseRecyclerAdapter<Comment, CommentHolder> commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class,
                R.layout.list_comment,
                CommentHolder.class,
                FirebaseUtils.getCommentRef(idpost)
        ) {
            @Override
            protected void populateViewHolder(CommentHolder viewHolder, Comment model, int position) {
                viewHolder.setUsername(model.getUser().getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                Glide.with(PostActivity.this)
                        .load(model.getUser().getImage())
                        .into(viewHolder.commentOwnerDisplay);
            }
        };

        commentRecyclerView.setAdapter(commentAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putSerializable(BUNDLE_COMMENT, mComment);
        super.onSaveInstanceState(outState);
    }
}
