package com.example.meza.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.model.User2;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 2/25/2022.
 */
public class ActiveThumnailAdapter extends RecyclerView.Adapter<ActiveThumnailAdapter.ViewHolder> {
    ArrayList<User2> listActiveUser;

    public ActiveThumnailAdapter(ArrayList<User2> listActiveUser) {
        this.listActiveUser = listActiveUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_thumnail_horizontal, parent, false);
        return new ViewHolder(view);
    }



    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User2 activeUser = listActiveUser.get(position);
        holder.thumnail.setImageResource(R.drawable.muitreo);
        holder.name.setText(activeUser.getFullname());


    }

    @Override
    public int getItemCount() {
        return listActiveUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView thumnail;
        TextView name;
        FrameLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumnail =itemView.findViewById(R.id.item_active_thumnail);
            name = itemView.findViewById(R.id.item_active_name);
            container = itemView.findViewById(R.id.framelayout_thumnail);
        }
    }
}
