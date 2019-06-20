package com.example.user.isotuapp.View;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.isotuapp.Controller.ShareAdapter;
import com.example.user.isotuapp.Model.Comment;
import com.example.user.isotuapp.Model.Contact;
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
import com.example.user.isotuapp.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    private static final String BUNDLE_COMMENT = "comment";
    private static int TIME_OUT = 1000;
    String idpost;
    FirebaseUser currentUser;
    View progressOverlay;
    RecyclerView recyclerView;
    EditText searchUser;
    APIService apiService;
    private Post mPost;
    private EditText mCommentEditTextView;
    private Comment mComment;
    private LinearLayout sent;
    private DatabaseReference database;
    private FirebaseAuth mFirebaseAuth;
    private SlidingUpPanelLayout mLayout;
    String iduserpost,fullname;
    private ArrayList<Contact> mData;
    private ArrayList<String> mDataId;
    private ShareAdapter mAdapter;
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Contact.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Contact.class));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mDataId.remove(pos);
            mData.remove(pos);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), "Tidak Ada Koneksi Internet", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if (savedInstanceState != null) {
            mComment = (Comment) savedInstanceState.getSerializable(BUNDLE_COMMENT);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(PostActivity.this, Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        Intent intent = getIntent();
        if(intent.getStringExtra(Constants.EXTRA_POST )!= null){
            idpost = intent.getStringExtra(Constants.EXTRA_POST);
        }else{
            idpost = intent.getStringExtra("id");
        }

        Log.d("lol", "idpost: " + idpost);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_share);
        progressOverlay = (FrameLayout) findViewById(R.id.progress_overlay);

        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        searchUser = (EditText) findViewById(R.id.searchuser_topost);


        recyclerView = findViewById(R.id.listusertoshare);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
        final LinearLayout postShareLayout = (LinearLayout) findViewById(R.id.share_posting_detail);
        final TextView postNumLikesTextView = (TextView) findViewById(R.id.number_like_detail);
        final TextView postNumCommentsTextView = (TextView) findViewById(R.id.number_comment_detail);
        final TextView postNumShareTextView = (TextView) findViewById(R.id.number_share_detail);
        final ImageView statusImageView = (ImageView) findViewById(R.id.status_like_detail);
        final TextView postTextTextView = (TextView) findViewById(R.id.caption_posting_detail);
        final TextView liker = (TextView) findViewById(R.id.userlikedetail);
        liker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, DetailUserHobi.class);
                intent.putExtra("reference", "userpostliked");
                intent.putExtra("child", idpost);
                startActivity(intent);
            }
        });
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        final DatabaseReference dbs = FirebaseDatabase.getInstance().getReference("posting").child(idpost);
        if(dbs == null){
        LinearLayout linearLayout = findViewById(R.id.kondisi);
        linearLayout.setVisibility(View.GONE);
        LinearLayout emptyView =  findViewById(R.id.emptyview);
        emptyView.setVisibility(View.VISIBLE);
        }else{
            dbs.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final Post post = dataSnapshot.getValue(Post.class);
                    try {

                        postLikeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onLikeClick(idpost,post.getUser().getUid());
                            }
                        });

                        if (post.getImage() != null) {
                            postDisplayImageView.setVisibility(View.VISIBLE);
                            Picasso.get().load(post.getImage()).fit()
                                    .into(postDisplayImageView);
                        } else {
                            postDisplayImageView.setImageBitmap(null);
                            postDisplayImageView.setVisibility(View.GONE);
                        }


                        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(post.getUser().getUid());
                        dbuser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);


                                Picasso.get().load(user.getImage()).fit()
                                        .into(postOwnerDisplayImageView);


                                postOwnerUsernameTextView.setText(user.getFullname());

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        postOwnerDisplayImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PostActivity.this, FriendProfile.class);
                                intent.putExtra("iduser", post.getUser().getUid());
                                startActivity(intent);
                            }
                        });


                        LinearLayout empty = findViewById(R.id.emptyview);
                        empty.setVisibility(View.GONE);
                        postOwnerUsernameTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PostActivity.this, FriendProfile.class);
                                intent.putExtra("iduser", post.getUser().getUid());
                                startActivity(intent);
                            }
                        });
                        postTimeCreatedTextView.setText(DateUtils.getRelativeTimeSpanString(post.getTimeCreated()));
                        postTextTextView.setText(post.getText());
                        postNumLikesTextView.setText(String.valueOf(post.getNumlikes()));
                        postNumCommentsTextView.setText(String.valueOf(post.getNumComment()));
                        postNumShareTextView.setText(String.valueOf(post.getNumshare()));

                        postShareLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PostActivity.this);

                                View mView = getLayoutInflater().inflate(R.layout.pop_up_share,
                                        null);
                                TextView shareFriend = (TextView) mView.findViewById(R.id.share_to_friend);

                                TextView shareHome = (TextView) mView.findViewById(R.id.share_to_home);
                                mBuilder.setView(mView);

                                final AlertDialog dialog = mBuilder.create();
                                dialog.show();
                                shareFriend.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        getSliding(idpost);
                                    }
                                });
                                shareHome.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(PostActivity.this, ShareActivity.class);
                                        if (post.getImage() != null) {
                                            intent.putExtra("imagepost", post.getImage());
                                        } else {
                                            intent.putExtra("imagepost", "");
                                        }
                                        intent.putExtra("userpost", post.getUser().getFullname());
                                        intent.putExtra("idpost", post.getPostId());
                                        intent.putExtra("imageuserpost", post.getUser().getImage());
                                        intent.putExtra("captionpost", post.getText());
                                        intent.putExtra("iduser", post.getUser().getUid());
                                        String lol = String.valueOf(post.getTimeCreated());
                                        intent.putExtra("timecreated", lol);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        LinearLayout linearLayout = findViewById(R.id.rootView);
                        LinearLayout empty = findViewById(R.id.emptyview);
                        LinearLayout commentField = findViewById(R.id.rootComment);
                        commentField.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dblike = FirebaseDatabase.getInstance().
                getReference().child("post_liked").child(mAuth.getUid());

        dblike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean status = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(idpost)) {
                        status = true;
                        break;
                    }
                }
                if (status == true) {
                    statusImageView.setImageResource(R.mipmap.filllike);
                } else {
                    statusImageView.setImageResource(R.mipmap.emptylike);
                }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putSerializable(BUNDLE_COMMENT, mComment);
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
        final RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));

        FirebaseRecyclerAdapter<Comment, CommentHolder> commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentHolder>(
                Comment.class,
                R.layout.list_comment,
                CommentHolder.class,
                FirebaseUtils.getCommentRef(idpost)
        ) {
            @Override
            protected void populateViewHolder(final CommentHolder viewHolder, final Comment model, int position) {
                viewHolder.setUsername(model.getUser().getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));


                commentRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PostActivity.this, FriendProfile.class);
                        intent.putExtra("iduser", model.getUser().getUid());
                        startActivity(intent);
                    }
                });

                final DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(model.getUser().getUid());
                dbuser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Picasso.get().load(user.getImage()).into(viewHolder.commentOwnerDisplay);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(PostActivity.this);
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

    private void onLikeClick(final String postId,final String IdUser) {
        Log.d("tesketololan", "onLikeClick: " + postId);
//        Toast.makeText(getApplicationContext(), "terclick", Toast.LENGTH_SHORT).show();
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
                                            deleteNotifications(postId,IdUser);
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
                                                    final HashMap<String, Object> hobiuser = new HashMap<>();
                                                    hobiuser.put("iduser", usr.getUid());
                                                    hobiuser.put("fotoprofil", usr.getImage());
                                                    hobiuser.put("namaprofil", usr.getFullname());
                                                    dbuserliked.child(mAuth.getUid()).setValue(hobiuser);
                                                    final DatabaseReference dbpost = FirebaseDatabase.getInstance().getReference("posting").child(postId);
                                                    dbpost.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Post post = dataSnapshot.getValue(Post.class);
                                                            if(!post.getUser().getUid().equals(currentUser.getUid())) {
                                                                addNotification(IdUser, postId, "menyukai postingan anda");
                                                                sendNotifiaction(IdUser, usr.getFullname(), "Menyukai postingan anda", IdUser, postId);
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

    public void getSliding(final String idpostny) {
        final Utils utils = new Utils(this);
        Toast.makeText(getApplicationContext(), "thissssss", Toast.LENGTH_SHORT).show();
        mData.clear();
        database = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).child("contactadded");
        database.addChildEventListener(childEventListener);
        mAdapter = new ShareAdapter(PostActivity.this, mData, mDataId, idpost);
        recyclerView.setAdapter(mAdapter);
        utils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                utils.animateView(progressOverlay, View.GONE, 0, 200);
            }
        }, TIME_OUT);

    }

    private void searchUsers(String s) {
        mData.clear();
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid()).orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        if (s.equals("")) {
            database = FirebaseDatabase.getInstance().getReference("contact").child(currentUser.getUid());
            database.addChildEventListener(childEventListener);
        } else {
            query.addChildEventListener(childEventListener);
        }
        mAdapter = new ShareAdapter(PostActivity.this, mData, mDataId, idpost);
        recyclerView.setAdapter(mAdapter);
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
                Toast.makeText(PostActivity.this, "errorpak " , Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(PostActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if(intent.getStringExtra("share")!=null){
            getSliding("");
        }
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
                                            Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    public static class CommentHolder extends RecyclerView.ViewHolder {
        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;
        LinearLayout rootLayout;

        public CommentHolder(View itemView) {
            super(itemView);
            commentOwnerDisplay = (ImageView) itemView.findViewById(R.id.image_comment);
            usernameTextView = (TextView) itemView.findViewById(R.id.username_comment);
            timeTextView = (TextView) itemView.findViewById(R.id.date_comment);
            commentTextView = (TextView) itemView.findViewById(R.id.comment_text);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.rootlayoutcomment);
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

}
