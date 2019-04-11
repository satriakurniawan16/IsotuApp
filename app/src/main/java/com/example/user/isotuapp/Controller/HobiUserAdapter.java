package com.example.user.isotuapp.Controller;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.isotuapp.Model.HobiModel;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.Model.UserHobi;
import com.example.user.isotuapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HobiUserAdapter  extends RecyclerView.Adapter<HobiUserAdapter.ViewHolder> {
    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<UserHobi> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    private View mEmptyView;



    public HobiUserAdapter(Context context, ArrayList<UserHobi> data, ArrayList<String> dataId,View emptyView,
                       ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
        mEmptyView = emptyView;
        setHasStableIds(true);
    }

    public void updateEmptyView() {
        if (mData.size() == 0)
            mEmptyView.setVisibility(View.VISIBLE);
        else
            mEmptyView.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        UserHobi pet = mData.get(position);
        holder.nameTextView.setText(pet.getNamaprofil());
        Picasso.get().load(pet.getFotoprofil()).into(holder.profilImageview);
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("user").child(pet.getIduser());
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);
                holder.jurusanTextView.setText(user1.getFakultas()+", "+user1.getJurusan());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.itemView.setSelected(mSelectedId.contains(mDataId.get(position)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void toggleSelection(String dataId) {
        if (mSelectedId.contains(dataId))
            mSelectedId.remove(dataId);
        else
            mSelectedId.add(dataId);
        notifyDataSetChanged();
    }

    public int selectionCount() {
        return mSelectedId.size();
    }

    public void resetSelection() {
        mSelectedId = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedId() {
        return mSelectedId;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {
        final TextView nameTextView;
        final TextView jurusanTextView;
        final ImageView profilImageview;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.namauser);
            profilImageview = itemView.findViewById(R.id.fotouser);
            jurusanTextView  = itemView.findViewById(R.id.jurusanuser);

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

    public interface ClickHandler {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }
}
