package com.example.user.isotuapp.View;

import android.app.AlertDialog;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.isotuapp.Model.Comment;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.fragment.APIService;
import com.example.user.isotuapp.utils.Constants;
import com.example.user.isotuapp.utils.FirebaseUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareDetailActivity extends AppCompatActivity {

    private static final String BUNDLE_COMMENT = "comment";
    ImageView imageuser,imagepostuser,imagepost,imagelike;
    TextView nameuser,nameuserpost,dateposting,datepostingpost,captionpost,captionposting,numlike,numcomment,likerText;
    String idpost;
    EditText writeText;
    APIService apiService;
    LinearLayout sendComent,likeLayout,postinganreal;
    RecyclerView recyclerView;
    DatabaseReference reference;
    private Comment mComment;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detail);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (savedInstanceState != null) {
            mComment = (Comment) savedInstanceState.getSerializable(BUNDLE_COMMENT);
        }




        Intent intent = getIntent();
        idpost = intent.getStringExtra("idpost");


        reference = FirebaseDatabase.getInstance().getReference("posting").child(idpost);

        initPost();
        initCommentSection();
        sendComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    private void initPost(){
        sendComent = (LinearLayout) findViewById(R.id.comment_send_share);
        postinganreal = (LinearLayout) findViewById(R.id.postinganasli);
        writeText = (EditText) findViewById(R.id.isi_comment_share);
        imageuser = (ImageView) findViewById(R.id.image_profile_detail_share);
        imagepostuser = (ImageView) findViewById(R.id.image_profile_detail_shared);
        imagepost = (ImageView) findViewById(R.id.image_posting_detail_shared);
        nameuser = (TextView) findViewById(R.id.name_profile_detail_share);
        nameuserpost = (TextView) findViewById(R.id.name_profile_detail_shared);
        dateposting = (TextView) findViewById(R.id.date_posting_detail_share);
        datepostingpost = (TextView) findViewById(R.id.date_posting_detail_shared);
        captionpost = (TextView) findViewById(R.id.caption_posting_detail_share);
        captionposting = (TextView) findViewById(R.id.caption_posting_detail_shared);
        numcomment = (TextView) findViewById(R.id.number_comment_detail);
        numlike = (TextView) findViewById(R.id.number_like_detail);
        likerText = (TextView) findViewById(R.id.userlikedetail);
        likeLayout = (LinearLayout) findViewById(R.id.like_posting_detail);
        imagelike = (ImageView) findViewById(R.id.status_like_detail);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dblike = FirebaseDatabase.getInstance().
                getReference().child("post_liked").child(mAuth.getUid());



        likerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareDetailActivity.this, DetailUserHobi.class);
                intent.putExtra("reference","userpostliked");
                intent.putExtra("child",idpost);
                startActivity(intent);
            }
        });
        dblike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean status = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().equals(idpost)){
                        status = true;
                        break;
                    }
                }
                if(status == true){
                    imagelike.setImageResource(R.mipmap.filllike);
                }else{
                    imagelike.setImageResource(R.mipmap.emptylike);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikeClick(idpost);
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Post post = dataSnapshot.getValue(Post.class);
                Picasso.get().load(post.getImageuser()).into(imageuser);
                Picasso.get().load(post.getUser().getImage()).into(imagepostuser);
                if(!post.getImage().equals("")){
                    Picasso.get().load(post.getImage()).into(imagepost);
                }else{
                    imagepost.setVisibility(View.GONE);
                }
                numlike.setText(String.valueOf(post.getNumlikes()));
                numcomment.setText(String.valueOf(post.getNumComment()));
                nameuser.setText(post.getNameUser());
                nameuserpost.setText(post.getUser().getFullname());
                dateposting.setText(DateUtils.getRelativeTimeSpanString(post.getNewtimeCreate()));
                datepostingpost.setText(DateUtils.getRelativeTimeSpanString(post.getTimeCreated()));
                captionpost.setText(post.getCaptionshare());
                captionposting.setText(post.getText());
                postinganreal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShareDetailActivity.this,PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST,post.getIdpost());
                        startActivity(intent);
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
//        outState.putSerializable(BUNDLE_COMMENT, mComment);
        super.onSaveInstanceState(outState);
    }



    private void sendComment() {
        final ProgressDialog progressDialog = new ProgressDialog(ShareDetailActivity.this);
        progressDialog.setMessage("Sending comment..");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mComment = new Comment();
        final String uid = FirebaseUtils.getUid();
        String strComment = writeText.getText().toString();

        mComment.setCommentId(uid);
        mComment.setComment(strComment);
        mComment.setTimeCreated(System.currentTimeMillis());
        FirebaseUtils.getUserRef(FirebaseUtils.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        mComment.setUser(user);
                        FirebaseUtils.getCommentRef(idpost)
                                .child(uid)
                                .setValue(mComment);

                        DatabaseReference fbuser = FirebaseDatabase.getInstance().getReference("posting").child(idpost);
                        fbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Post post = dataSnapshot.getValue(Post.class);
                                if(!post.getUser().getUid().equals(currentUser.getUid())){
                                    sendNotifiaction(post.getUser().getUid(),user.getFullname(),user.getFullname()+ " : Mengomentari postingan anda",post.getUser().getUid(),idpost);
                                    addNotification(post.getUser().getUid(),idpost,"mengomentari postingan anda");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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
                                        writeText.setText("");
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
        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview_share);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(ShareDetailActivity.this));

        FirebaseRecyclerAdapter<Comment, PostActivity.CommentHolder> commentAdapter = new FirebaseRecyclerAdapter<Comment, PostActivity.CommentHolder>(
                Comment.class,
                R.layout.list_comment,
                PostActivity.CommentHolder.class,
                FirebaseUtils.getCommentRef(idpost)
        ) {
            @Override
            protected void populateViewHolder(PostActivity.CommentHolder viewHolder, final Comment model, int position) {
                viewHolder.setUsername(model.getUser().getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                Glide.with(ShareDetailActivity.this)
                        .load(model.getUser().getImage())
                        .into(viewHolder.commentOwnerDisplay);

                viewHolder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShareDetailActivity.this);
                        final View mView = getLayoutInflater().inflate(R.layout.popup_message,
                                null);
                        DatabaseReference dbinitilize = FirebaseDatabase.getInstance().getReference("posting").child(idpost);
                        dbinitilize.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Post post = dataSnapshot.getValue(Post.class);
                                if(post.getType().equals("0")){
                                    if(post.getUser().getUid().equals(currentUser.getUid())){
                                        LinearLayout deleteComment =  mView.findViewById(R.id.hapus);
                                        mBuilder.setView(mView);
                                        final AlertDialog dialognya = mBuilder.create();
                                        dialognya.show();
                                        deleteComment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DatabaseReference dbcomment =  FirebaseDatabase.getInstance().getReference("comments").child(idpost).child(model.getCommentId());
                                                dbcomment.removeValue();
                                                decNumComment();
                                                dialognya.dismiss();
                                            }
                                        });
                                    }else if(model.getUser().getUid().equals(currentUser.getUid())){
                                        LinearLayout deleteComment =  mView.findViewById(R.id.hapus);
                                        mBuilder.setView(mView);
                                        final AlertDialog dialognya = mBuilder.create();
                                        dialognya.show();
                                        deleteComment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DatabaseReference dbcomment =  FirebaseDatabase.getInstance().getReference("comments").child(idpost).child(model.getCommentId());
                                                dbcomment.removeValue();
                                                decNumComment();
                                                dialognya.dismiss();
                                            }
                                        });
                                    }

                                }else if(post.getType().equals("1")){
                                    if(post.getIduser().equals(currentUser.getUid())){
                                        LinearLayout deleteComment =  mView.findViewById(R.id.hapus);
                                        mBuilder.setView(mView);
                                        final AlertDialog dialognya = mBuilder.create();
                                        dialognya.show();
                                        deleteComment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DatabaseReference dbcomment =  FirebaseDatabase.getInstance().getReference("comments").child(idpost).child(model.getCommentId());
                                                dbcomment.removeValue();
                                                decNumComment();
                                                dialognya.dismiss();
                                            }
                                        });
                                    }else if(model.getUser().getUid().equals(currentUser.getUid())){
                                        LinearLayout deleteComment =  mView.findViewById(R.id.hapus);
                                        mBuilder.setView(mView);
                                        final AlertDialog dialognya = mBuilder.create();
                                        dialognya.show();
                                        deleteComment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DatabaseReference dbcomment =  FirebaseDatabase.getInstance().getReference("comments").child(idpost).child(model.getCommentId());
                                                dbcomment.removeValue();
                                                decNumComment();
                                                dialognya.dismiss();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        return true;
                    }
                });
            }
        };

        commentRecyclerView.setAdapter(commentAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        outState.putSerializable(BUNDLE_COMMENT, mComment);
        super.onSaveInstanceState(outState);
    }

    private void onLikeClick(final String postId) {
        Log.d("tesketololan", "onLikeClick: " + postId);
        Toast.makeText(getApplicationContext(), "terclick", Toast.LENGTH_SHORT).show();
        final DatabaseReference dbuserliked = FirebaseDatabase.getInstance().getReference("userpostliked").child(postId);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(postId)
                                                    .setValue(null);
                                            dbuserliked.child(mAuth.getUid()).removeValue();
                                            initPost();
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
                                            Log.d("tesketololan2", "doTransaction: ");
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(postId)
                                                    .setValue(true);
                                            final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("user").child(mAuth.getUid());
                                            dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    final User usr = dataSnapshot.getValue(User.class);
                                                    final HashMap<String, Object> hobiuser= new HashMap<>();
                                                    hobiuser.put("iduser",usr.getUid());
                                                    hobiuser.put("fotoprofil",usr.getImage());
                                                    hobiuser.put("namaprofil", usr.getFullname());
                                                    dbuserliked.child(mAuth.getUid()).setValue(hobiuser);
                                                    DatabaseReference fbuser = FirebaseDatabase.getInstance().getReference("posting").child(idpost);
                                                    fbuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Post post = dataSnapshot.getValue(Post.class);
                                                            if(!post.getUser().getUid().equals(currentUser.getUid())){
                                                                sendNotifiaction(post.getUser().getUid(),usr.getFullname(),usr.getFullname()+ " : Mengomentari postingan anda",post.getUser().getUid(),idpost);
                                                                addNotification(post.getUser().getUid(),idpost,"mengomentari postingan anda");
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                    initPost();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public void onResume() {
        super.onResume();
        status("online");
    }

    private void status(final String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    public void decNumComment(){
        FirebaseUtils.getPostRef().child(idpost)
                .child(Constants.NUM_COMMENTS_KEY)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        mutableData.setValue(num - 1);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        initPost();
                    }
                });
    }

    private void sendNotifiaction(String receiver, final String username, final String message, final String userid, final String idpost){
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
                            userid,"post", idpost);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(ShareDetailActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("type", "0");
        hashMap.put("date", System.currentTimeMillis());
        reference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(PostActivity.this, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShareDetailActivity.this, "errorpak " , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNotifications(final String postid, final String userid){
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
                                        Toast.makeText(ShareDetailActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
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
}
