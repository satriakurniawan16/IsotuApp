package com.example.user.isotuapp.Controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Notification.Client;
import com.example.user.isotuapp.Notification.Data;
import com.example.user.isotuapp.Notification.MyResponse;
import com.example.user.isotuapp.Notification.Sender;
import com.example.user.isotuapp.Notification.Token;
import com.example.user.isotuapp.R;
import com.example.user.isotuapp.View.Dashboard;
import com.example.user.isotuapp.View.DetailUserHobi;
import com.example.user.isotuapp.View.EditPost;
import com.example.user.isotuapp.View.FriendProfile;
import com.example.user.isotuapp.View.MessageActivity;
import com.example.user.isotuapp.View.PostActivity;
import com.example.user.isotuapp.View.ShareActivity;
import com.example.user.isotuapp.View.ShareDetailActivity;
import com.example.user.isotuapp.fragment.APIService;
import com.example.user.isotuapp.utils.Constants;
import com.example.user.isotuapp.utils.FirebaseUtils;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    boolean statuslike = false;
    private FirebaseUser firebaseUser;
    APIService apiService;
    public static  final int POST_TYPE_0 = 0;
    public static  final int POST_TYPE_1 = 1;

    public PostAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == POST_TYPE_0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_post, parent, false);
            return new PostAdapter.ImageViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_post_share, parent, false);
            return new PostAdapter.ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ImageViewHolder holder, final int position) {
        final String id;
        final Post model = mPosts.get(position);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (model.getType().equals("1")) {
//            holder.firstLinear.setVisibility(View.VISIBLE);
            if (!model.getIduser().equals(currentUser.getUid())) {
                holder.moreShareLayout.setVisibility(View.GONE);
            } else {
                holder.moreShareLayout.setVisibility(View.VISIBLE);
            }


            holder.moreShareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View mView = LayoutInflater.from(mContext).inflate(R.layout.popup_more, null);
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                    final TextView shareFriend = (TextView) mView.findViewById(R.id.moreedit);

                    TextView shareHome = (TextView) mView.findViewById(R.id.moredelete);
                    mBuilder.setView(mView);

                    final AlertDialog dialoglol = mBuilder.create();
                    dialoglol.show();

                    shareFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, EditPost.class);
                            intent.putExtra("idpost", model.getPostId());
                            intent.putExtra("type", "1");
                            intent.putExtra("textpost", model.getText());
                            intent.putExtra("textshare", model.getCaptionshare());
                            mContext.startActivity(intent);
                        }
                    });

                    shareHome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                    .setMessage("Apakah anda yakin untuk menghapus ?")
                                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DatabaseReference dbfpost = FirebaseDatabase.getInstance().getReference("posting");
                                            dbfpost.child(model.getPostId()).removeValue();
                                            dialoglol.dismiss();
                                        }
                                    })
                                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
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

//
//            holder.secondLinear.setPadding(10, 10, 10, 10);
//            holder.thirdLinear.setPadding(10, 10, 10, 10);
//            holder.thirdLinear.setBackgroundResource(R.drawable.custom_border);
//            holder.postCommentLayout.setVisibility(View.GONE);
//            holder.commentShare.setVisibility(View.VISIBLE);
//            holder.textShare.setVisibility(View.GONE);
//            holder.postNumShareTextView.setVisibility(View.GONE);
//            holder.sharePostingImageView.setVisibility(View.GONE);
//            holder.captionTextView.setVisibility(View.VISIBLE);
            id = model.getIdpost();

            DatabaseReference dbusershare = FirebaseDatabase.getInstance().getReference("user").child(model.getIduser());
            dbusershare.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    Picasso.get().load(user.getImage()).into(holder.imageUserImageView);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            holder.imageUserImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FriendProfile.class);
                    intent.putExtra("iduser", model.getIduser());
                    mContext.startActivity(intent);
                }
            });

            holder.moreLayout.setVisibility(View.GONE);
            holder.ShareDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShareDetailActivity.class);
                    intent.putExtra("idpost", model.getPostId());
                    mContext.startActivity(intent);
                }
            });

            holder.nameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FriendProfile.class);
                    intent.putExtra("iduser", model.getIduser());
                    mContext.startActivity(intent);
                }
            });


            holder.nameSharedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FriendProfile.class);
                    intent.putExtra("iduser", model.getUser().getUid());
                    mContext.startActivity(intent);
                }
            });


            holder.nameTextView.setText(model.getNameUser());
            holder.nameSharedTextView.setText(model.getUser().getFullname());
            holder.dateSharedTextView.setText(DateUtils.getRelativeTimeSpanString(model.getNewtimeCreate()));
            holder.captionTextView.setText(model.getCaptionshare());
            holder.seeComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShareDetailActivity.class);
                    intent.putExtra("idpost", model.getPostId());
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.seeComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostActivity.class);
                    intent.putExtra(Constants.EXTRA_POST, model.getPostId());
                    mContext.startActivity(intent);
                }
            });
            id = model.getPostId();
            holder.commentShare.setVisibility(View.GONE);
        }


        holder.postNumCommentsTextView.setText(String.valueOf(model.getNumComment()));
        holder.postNumLikesTextView.setText(String.valueOf(model.getNumlikes()));
        holder.postTimeCreatedTextView.setText(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));
        holder.postOwnerUsernameTextView.setText(model.getUser().getFullname());
        holder.captionTextView.setText(model.getCaptionshare());
        holder.postNumShareTextView.setText(String.valueOf(model.getNumshare()));
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dblike = FirebaseDatabase.getInstance().
                getReference().child("post_liked").child(mAuth.getUid());

        dblike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean status = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("datasnapshotnya lagi", "onDataChange: " + ds.getKey() + model.getPostId());
                    if (ds.getKey().equals(model.getPostId())) {
                        status = true;
                        break;
                    }
                }
                if (status == true) {
                    holder.imagelike.setImageResource(R.mipmap.filllike);
                } else {
                    holder.imagelike.setImageResource(R.mipmap.emptylike);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (model.getUser().getUid().equals(currentUser.getUid())) {
            if (!model.getType().equals("1")) {
                holder.moreLayout.setVisibility(View.VISIBLE);
            }
        } else {
            holder.moreLayout.setVisibility(View.GONE);
        }

        holder.moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mView = LayoutInflater.from(mContext).inflate(R.layout.popup_more,
                        null);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                final TextView shareFriend = (TextView) mView.findViewById(R.id.moreedit);

                TextView shareHome = (TextView) mView.findViewById(R.id.moredelete);
                mBuilder.setView(mView);

                final AlertDialog dialoglol = mBuilder.create();
                dialoglol.show();

                shareFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EditPost.class);
                        intent.putExtra("idpost", model.getPostId());
                        intent.putExtra("type", "0");
                        intent.putExtra("textpost", model.getText());
                        intent.putExtra("textshare", "");
                        mContext.startActivity(intent);

                    }
                });

                shareHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
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

        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("user").child(model.getUser().getUid());
        dbuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                try {
                    Glide.with(mContext)
                            .load(user.getImage())
                            .into(holder.postOwnerDisplayImageView);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (model.getImage() != null && !model.getImage().equals("")) {
            holder.postDisplayImageVIew.setVisibility(View.VISIBLE);
            Picasso.get().load(model.getImage()).fit()
                    .into(holder.postDisplayImageVIew);

        } else {
            holder.postDisplayImageVIew.setImageBitmap(null);
            holder.postDisplayImageVIew.setVisibility(View.GONE);
        }
        holder.postLikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.postLikeLayout.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        holder.postLikeLayout.setEnabled(true);
                    }
                }, 3000);
                if (model.getType().equals("0")) {
                    onLikeClick(model.getPostId(), model.getUser().getUid());
                } else if (model.getType().equals("1")) {
                    onLikeClick(model.getPostId(), model.getIduser());
                }

            }
        });


        holder.DetailPostDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostActivity.class);
                intent.putExtra(Constants.EXTRA_POST, id);
                mContext.startActivity(intent);
            }
        });


        holder.postCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostActivity.class);
                intent.putExtra(Constants.EXTRA_POST, model.getPostId());
                mContext.startActivity(intent);
            }
        });

        holder.liker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailUserHobi.class);
                intent.putExtra("reference", "userpostliked");
                intent.putExtra("child", model.getPostId());
                intent.putExtra("title", "Teman yang menyukai ini");
                mContext.startActivity(intent);
            }
        });

        holder.ProfileDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FriendProfile.class);
                intent.putExtra("iduser", model.getUser().getUid());
                mContext.startActivity(intent);
            }
        });

        holder.postTextTextView.setText(model.getText());
        holder.sharePostingImageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                View mView = LayoutInflater.from(mContext).inflate(R.layout.pop_up_share,
                        null);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                TextView shareFriend = (TextView) mView.findViewById(R.id.share_to_friend);

                TextView shareHome = (TextView) mView.findViewById(R.id.share_to_home);
                mBuilder.setView(mView);

                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                shareFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ((Dashboard) mContext).getSliding(model.getPostId());
                    }
                });
                shareHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ShareActivity.class);
                        if (model.getImage() != null) {
                            intent.putExtra("imagepost", model.getImage());
                        } else {
                            intent.putExtra("imagepost", "");
                        }
                        intent.putExtra("userpost", model.getUser().getFullname());
                        intent.putExtra("idpost", model.getPostId());
                        intent.putExtra("imageuserpost", model.getUser().getImage());
                        intent.putExtra("captionpost", model.getText());
                        intent.putExtra("iduser", model.getUser().getUid());
                        String lol = String.valueOf(model.getTimeCreated());
                        intent.putExtra("timecreated", lol);
                        mContext.startActivity(intent);
                    }
                });
            }
        });

        holder.commentShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShareDetailActivity.class);
                intent.putExtra("idpost", model.getPostId());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
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
        LinearLayout rootLayout;

        public ImageViewHolder(View itemView) {
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
            sharePostingImageView = (LinearLayout) itemView.findViewById(R.id.share_posting);
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
            rootLayout = (LinearLayout) itemView.findViewById(R.id.root_post_item);
        }
    }

    private void addNotification(String userid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        FirebaseAuth authuser = FirebaseAuth.getInstance();
        FirebaseUser mUser = authuser.getCurrentUser();
        String key;
        key = reference.push().getKey();
        hashMap.put("id", key);
        hashMap.put("userid", mUser.getUid());
        hashMap.put("text", "menyukai unggahan anda");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("type", "0");
        hashMap.put("date", System.currentTimeMillis());
        reference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(mContext, "Berhasil terimpan", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(mContext, "errorpak ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNotifications(final String postid, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("postid").getValue().equals(postid)) {
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
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

    private void nrLikes(final TextView likes, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    private void isSaved(final String postid, final ImageView imageView){
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Saves").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(postid).exists()){
//                    imageView.setImageResource(R.drawable.ic_save_black);
//                    imageView.setTag("saved");
//                } else{
//                    imageView.setImageResource(R.drawable.ic_savee_black);
//                    imageView.setTag("save");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void getText(String postid, final EditText editText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posting")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getText());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onLikeClick(final String postId, final String idUser) {
        Log.d("tesketololan", "onLikeClick: " + postId);
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
                                            deleteNotifications(postId, idUser);
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
                                            statuslike = true;
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
                                                    final HashMap<String, Object> hobiuser = new HashMap<>();
                                                    hobiuser.put("iduser", usr.getUid());
                                                    hobiuser.put("fotoprofil", usr.getImage());
                                                    hobiuser.put("namaprofil", usr.getFullname());
                                                    dbuserliked.child(mAuth.getUid()).setValue(hobiuser);
                                                    if (!idUser.equals(mAuth.getUid())) {
                                                        sendNotifiaction(idUser, usr.getFullname(), "", idUser, postId);
                                                        addNotification(idUser, postId);
                                                    }
                                                    notifyDataSetChanged();
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
    public int getItemViewType(int position) {
        if(mPosts.get(position).getType().equals("0")){
            return POST_TYPE_0;
        }else {
            return POST_TYPE_1;
        }

    }

    private void sendNotifiaction(String receiver, final String username, final String message, final String userid, final String idpost) {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        final FirebaseUser fuser = mauth.getCurrentUser();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + " menyukai postingan anda" + message, username,
                            userid, "post", idpost);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
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

}