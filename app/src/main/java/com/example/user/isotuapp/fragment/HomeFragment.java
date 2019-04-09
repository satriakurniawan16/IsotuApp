package com.example.user.isotuapp.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.isotuapp.Controller.ContactAdapter;
import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.CompleteProfile;
import com.example.user.isotuapp.View.Dashboard;
import com.example.user.isotuapp.View.DetailUserHobi;
import com.example.user.isotuapp.View.EditPost;
import com.example.user.isotuapp.View.FriendProfile;
import com.example.user.isotuapp.View.MessageActivity;
import com.example.user.isotuapp.View.PostActivity;
import com.example.user.isotuapp.View.Posting;
import com.example.user.isotuapp.View.ShareActivity;
import com.example.user.isotuapp.View.ShareDetailActivity;
import com.example.user.isotuapp.utils.Constants;
import com.example.user.isotuapp.utils.FirebaseUtils;
import com.example.user.isotuapp.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class HomeFragment extends Fragment implements Serializable {

    View rootView;
    private View mRootVIew;
    private FirebaseRecyclerAdapter<Post, PostHolder> mPostAdapter;
    private RecyclerView mPostRecyclerView;
    private boolean statuslike = false;
    private Parcelable recyclerViewState;
    FirebaseUser currentUser;
    private DatabaseReference database;
    LinearLayout emptyview;
    private FirebaseAuth mFirebaseAuth;
    FloatingActionButton fab;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootVIew = inflater.inflate(R.layout.fragment_home, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        emptyview = (LinearLayout) mRootVIew.findViewById(R.id.emptyview);
        emptyview();
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
        emptyview();
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
                final String id;
                emptyview();
                if(model.getType().equals("1")){
                    viewHolder.firstLinear.setVisibility(View.VISIBLE);
                    if(!model.getIduser().equals(currentUser.getUid())){
                        viewHolder.moreShareLayout.setVisibility(View.GONE);
                    }else {
                        viewHolder.moreShareLayout.setVisibility(View.VISIBLE);
                    }

                    viewHolder.moreShareLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View mView = getActivity().getLayoutInflater().inflate(R.layout.popup_more,
                                    null);
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                            final TextView shareFriend = (TextView) mView.findViewById(R.id.moreedit);

                            TextView shareHome = (TextView) mView.findViewById(R.id.moredelete);
                            mBuilder.setView(mView);

                            final AlertDialog dialoglol = mBuilder.create();
                            dialoglol.show();

                            shareFriend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(),EditPost.class);
                                    intent.putExtra("idpost",model.getPostId());
                                    intent.putExtra("type","1");
                                    intent.putExtra("textpost",model.getText());
                                    intent.putExtra("textshare",model.getCaptionshare());
                                    startActivity(intent);

                                }
                            });

                            shareHome.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                            .setMessage("Apakah anda yakin untuk menghapus ?")
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DatabaseReference dbfpost = FirebaseDatabase.getInstance().getReference("posting");
                                                    dbfpost.child(model.getPostId()).removeValue();
                                                    dialoglol.dismiss();
                                                }
                                            })
                                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();

                                                }
                                            });
                                    builder.create().show();
                                }
                            });
                        }
                    });


                    viewHolder.secondLinear.setPadding(10,10,10,10);
                    viewHolder.thirdLinear.setPadding(10,10,10,10);
                    viewHolder.thirdLinear.setBackgroundResource(R.drawable.custom_border);
                    viewHolder.postCommentLayout.setVisibility(View.GONE);
                    viewHolder.commentShare.setVisibility(View.VISIBLE);
                    viewHolder.textShare.setVisibility(View.GONE);
                    viewHolder.postNumShareTextView.setVisibility(View.GONE);
                    viewHolder.sharePostingImageView.setVisibility(View.GONE);
                    viewHolder.captionTextView.setVisibility(View.VISIBLE);
                    id = model.getIdpost();
                    Glide.with(getActivity())
                            .load(model.getImageuser())
                            .into(viewHolder.imageUserImageView);

                    viewHolder.imageUserImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), FriendProfile.class);
                            intent.putExtra("iduser",model.getIduser());
                            startActivity(intent);
                        }
                    });

                    viewHolder.moreLayout.setVisibility(View.GONE);
                    viewHolder.ShareDirection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(),ShareDetailActivity.class);
                            intent.putExtra("idpost",model.getPostId());
                            startActivity(intent);
                        }
                    });

                    viewHolder.nameTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), FriendProfile.class);
                            intent.putExtra("iduser",model.getIduser());
                            startActivity(intent);
                        }
                    });


                    viewHolder.nameSharedTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), FriendProfile.class);
                            intent.putExtra("iduser",model.getUser().getUid());
                            startActivity(intent);
                        }
                    });


                    viewHolder.nameTextView.setText(model.getNameUser());
                    viewHolder.nameSharedTextView.setText(model.getUser().getFullname());
                    viewHolder.dateSharedTextView.setText(DateUtils.getRelativeTimeSpanString(model.getNewtimeCreate()));
                    viewHolder.captionTextView.setText(model.getCaptionshare());
                    viewHolder.seeComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(),ShareDetailActivity.class);
                            intent.putExtra("idpost",model.getPostId());
                            startActivity(intent);
                        }
                    });
                }else{
                    viewHolder.seeComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), PostActivity.class);
                            intent.putExtra(Constants.EXTRA_POST, model.getPostId());
                            startActivity(intent);
                        }
                    });
                    id = model.getPostId();
                    viewHolder.commentShare.setVisibility(View.GONE);
                }




                viewHolder.setNumCOmments(String.valueOf(model.getNumComment()));
                viewHolder.setNumLikes(String.valueOf(model.getNumlikes()));
                viewHolder.setTIme(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
                viewHolder.setUsername(model.getUser().getFullname());
                viewHolder.setPostText(model.getText());
                viewHolder.setNumShare(String.valueOf(model.getNumshare()));


                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                DatabaseReference dblike = FirebaseDatabase.getInstance().
                        getReference().child("post_liked").child(mAuth.getUid());

                dblike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean status = false;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d("datasnapshotnya lagi", "onDataChange: " + ds.getKey() + model.getPostId());
                            if(ds.getKey().equals(model.getPostId())){
                                status = true;
                                break;
                            }
                        }
                        if(status == true){
                            viewHolder.imagelike.setImageResource(R.mipmap.filllike);
                        }else{
                            viewHolder.imagelike.setImageResource(R.mipmap.emptylike);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(model.getUser().getUid().equals(currentUser.getUid())) {
                    if (!model.getType().equals("1")){
                        viewHolder.moreLayout.setVisibility(View.VISIBLE);
                        }
                }else {
                    viewHolder.moreLayout.setVisibility(View.GONE);
                }

                viewHolder.moreLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.popup_more,
                                null);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        final TextView shareFriend = (TextView) mView.findViewById(R.id.moreedit);

                        TextView shareHome = (TextView) mView.findViewById(R.id.moredelete);
                        mBuilder.setView(mView);

                        final AlertDialog dialoglol = mBuilder.create();
                        dialoglol.show();

                        shareFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(),EditPost.class);
                                intent.putExtra("idpost",model.getPostId());
                                intent.putExtra("type","0");
                                intent.putExtra("textpost",model.getText());
                                intent.putExtra("textshare","");
                                startActivity(intent);

                            }
                        });

                        shareHome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                        .setMessage("Apakah anda yakin untuk menghapus ?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DatabaseReference dbfpost = FirebaseDatabase.getInstance().getReference("posting");
                                                dbfpost.child(model.getPostId()).removeValue();
                                                dialoglol.dismiss();
                                            }
                                        })
                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();

                                            }
                                        });
                                builder.create().show();
                            }
                        });
                    }
                });

                Glide.with(getActivity())
                        .load(model.getUser().getImage())
                        .into(viewHolder.postOwnerDisplayImageView);


                if (model.getImage() != null && !model.getImage().equals("")) {
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


                viewHolder.DetailPostDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostActivity.class);
                        intent.putExtra(Constants.EXTRA_POST, id);
                        startActivity(intent);
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

                viewHolder.liker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), DetailUserHobi.class);
                        intent.putExtra("reference","userpostliked");
                        intent.putExtra("child",model.getPostId());
                        startActivity(intent);
                    }
                });

                viewHolder.ProfileDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), FriendProfile.class);
                        intent.putExtra("iduser",model.getUser().getUid());
                        startActivity(intent);
                    }
                });

                viewHolder.sharePostingImageView.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onClick(View v) {
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.pop_up_share,
                                null);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        TextView shareFriend = (TextView) mView.findViewById(R.id.share_to_friend);

                        TextView shareHome = (TextView) mView.findViewById(R.id.share_to_home);
                        mBuilder.setView(mView);

                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        shareFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                ((Dashboard)getActivity()).getSliding(model.getPostId());
                            }
                        });
                        shareHome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(),ShareActivity.class);
                                if (model.getImage() != null) {
                                    intent.putExtra("imagepost",model.getImage());
                                }else{
                                    intent.putExtra("imagepost","");
                                }
                                intent.putExtra("userpost",model.getUser().getFullname());
                                intent.putExtra("idpost",model.getPostId());
                                intent.putExtra("imageuserpost",model.getUser().getImage());
                                intent.putExtra("captionpost",model.getText());
                                intent.putExtra("iduser",model.getUser().getUid() );
                                String lol = String.valueOf(model.getTimeCreated());
                                intent.putExtra("timecreated",lol);
                                startActivity(intent);
                            }
                        });
                    }
                });

                viewHolder.commentShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),ShareDetailActivity.class);
                        intent.putExtra("idpost",model.getPostId());
                        startActivity(intent);
                    }
                });

            }
        };
    }

    private void emptyview(){
        DatabaseReference dbpost = FirebaseDatabase.getInstance().getReference("posting");
        dbpost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    emptyview.setVisibility(View.GONE);
                }else{
                    emptyview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //During the tutorial I think I messed up this code. Make sure your's aligns to this, or just
    //check out the github code
    private void onLikeClick(final String postId) {
        Log.d("tesketololan", "onLikeClick: " + postId);
        Toast.makeText(getContext(), "terclick", Toast.LENGTH_SHORT).show();
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
                                            statuslike = false;
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            FirebaseUtils.getPostLikedRef(postId)
                                                    .setValue(null);
                                            dbuserliked.child(mAuth.getUid()).removeValue();
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
                                                    User usr = dataSnapshot.getValue(User.class);
                                                    final HashMap<String, Object> hobiuser= new HashMap<>();
                                                    hobiuser.put("iduser",usr.getUid());
                                                    hobiuser.put("fotoprofil",usr.getImage());
                                                    hobiuser.put("namaprofil", usr.getFullname());
                                                    dbuserliked.child(mAuth.getUid()).setValue(hobiuser);
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
        TextView postNumShareTextView;
        TextView liker;
        LinearLayout sharePostingImageView;
        LinearLayout firstLinear;
        LinearLayout secondLinear;
        LinearLayout thirdLinear;
        LinearLayout commentShare;
        ImageView imageUserImageView;
        TextView nameTextView;
        TextView nameSharedTextView;
        TextView dateSharedTextView;
        TextView captionTextView;
        LinearLayout DetailPostDirection;
        LinearLayout ProfileDirection;
        LinearLayout ShareDirection;
        TextView textShare;
        TextView seeComment;
        LinearLayout moreLayout;
        LinearLayout moreShareLayout;

        public PostHolder(View itemView) {
            super(itemView);
            postOwnerDisplayImageView = (ImageView) itemView.findViewById(R.id.image_profile);
            postOwnerUsernameTextView = (TextView) itemView.findViewById(R.id.name_profile);
            postTimeCreatedTextView = (TextView) itemView.findViewById(R.id.date_posting);
            postDisplayImageVIew = (ImageView) itemView.findViewById(R.id.image_posting);
            postLikeLayout = (LinearLayout) itemView.findViewById(R.id.like_posting);
            postCommentLayout = (LinearLayout) itemView.findViewById(R.id.comment_posting);
            commentShare = (LinearLayout) itemView.findViewById(R.id.comment_posting_share);
            postNumLikesTextView = (TextView) itemView.findViewById(R.id.number_like);
            postNumCommentsTextView = (TextView) itemView.findViewById(R.id.number_comment);
            postTextTextView = (TextView) itemView.findViewById(R.id.caption_posting);
            imagelike = (ImageView) itemView.findViewById(R.id.status_like);
            liker = (TextView) itemView.findViewById(R.id.tolistliker);
            sharePostingImageView = (LinearLayout) itemView.findViewById(R.id.share_posting) ;
            postNumShareTextView = (TextView) itemView.findViewById(R.id.number_share);
            firstLinear = (LinearLayout) itemView.findViewById(R.id.first_child_post_item);
            secondLinear = (LinearLayout) itemView.findViewById(R.id.child_post_item);
            thirdLinear = (LinearLayout) itemView.findViewById(R.id.second_child_post_item);
            imageUserImageView = (ImageView) itemView.findViewById(R.id.image_profile_share);
            nameTextView = (TextView) itemView.findViewById(R.id.name_profile_share);
            nameSharedTextView = (TextView) itemView.findViewById(R.id.name_profile_shared_user);
            dateSharedTextView = (TextView) itemView.findViewById(R.id.date_posting_share);
            captionTextView = (TextView) itemView.findViewById(R.id.caption_posting_share);
            DetailPostDirection = (LinearLayout) itemView.findViewById(R.id.detail_post_direction);
            ProfileDirection = (LinearLayout) itemView.findViewById(R.id.profile_direction);
            ShareDirection = (LinearLayout) itemView.findViewById(R.id.detail_post_direction);
            textShare = (TextView) itemView.findViewById(R.id.textShare);
            seeComment = (TextView) itemView.findViewById(R.id.seeComment);
            moreLayout = (LinearLayout) itemView.findViewById(R.id.more);
            moreShareLayout = (LinearLayout) itemView.findViewById(R.id.moreshare);
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
        public void setNumShare(String numComments) {
            postNumShareTextView.setText(numComments);
        }

        public void setPostText(String text) {
            postTextTextView.setText(text);
        }

    }
}