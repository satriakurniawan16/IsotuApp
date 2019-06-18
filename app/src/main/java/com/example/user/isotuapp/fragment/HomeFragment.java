package com.example.user.isotuapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.isotuapp.Controller.PostAdapter;
import com.example.user.isotuapp.Model.Post;
import com.example.user.isotuapp.R;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;


    private List<String> followingList;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    ProgressBar progress_circular;
    LinearLayout emptyview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.listPosting);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        emptyview = view.findViewById(R.id.emptyview);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
//        recyclerView_story = view.findViewById(R.id.recycler_view_story);
//        recyclerView_story.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
//                LinearLayoutManager.HORIZONTAL, false);
//        recyclerView_story.setLayoutManager(linearLayoutManager);
//        storyList = new ArrayList<>();
//        storyAdapter = new StoryAdapter(getContext(), storyList);
//        recyclerView_story.setAdapter(storyAdapter);

        progress_circular = view.findViewById(R.id.progress_circular);

        checkFollowing();

        return view;
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("contact")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("contactadded");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                readPosts();
//                readStory();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posting");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                Log.d("duplicatedata", "onDataChange: " + dataSnapshot.getChildrenCount() + followingList.size());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : followingList){
                        if(post.getType().equals("1")){
                            if (post.getIduser().equals(id) ) {
                                postList.add(post);
                            }
                        }else if(post.getType().equals("0")){
                            if (post.getUser().getUid().equals(id) ) {
                                postList.add(post);
                            }
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
                Log.d("duplicatedata", "onDataChange: " + postList);
                if(postList.size() == 0 ){
                    emptyview.setVisibility(View.VISIBLE);
                }else {
                    emptyview.setVisibility(View.GONE);
                }
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


//
}