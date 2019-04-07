package com.example.user.isotuapp.Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.isotuapp.Model.Contact;
import com.example.user.isotuapp.Model.User;
import com.example.user.isotuapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchUserAdapter  extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<User> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    public SearchUserAdapter(Context context, ArrayList<User> data, ArrayList<String> dataId,
                             ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User pet = mData.get(position);
        holder.nameTextView.setText(pet.getFullname());
        holder.keteranganTextView.setText(pet.getJurusan()+", "+pet.getFakultas());
        if(!pet.getImage().equals("")) {
            Picasso.get().load(pet.getImage()).into(holder.profilImageview);
        }
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
        final TextView keteranganTextView;
        final ImageView profilImageview;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nama_contact);
            keteranganTextView = itemView.findViewById(R.id.keterangan_contact);
            profilImageview = itemView.findViewById(R.id.image_contact);
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