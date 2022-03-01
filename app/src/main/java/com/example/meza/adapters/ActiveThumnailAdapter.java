package com.example.meza.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 2/25/2022.
 */
public class ActiveThumnailAdapter extends RecyclerView.Adapter<ActiveThumnailAdapter.ViewHolder> {
    ArrayList<String> listActiveUser;

    public ActiveThumnailAdapter(ArrayList<String> listActiveUser) {
        this.listActiveUser = listActiveUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_thumnail_horizontal, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String activeUser = listActiveUser.get(position);
        holder.thumnail.setImageResource(R.drawable.muitreo);
        holder.name.setText(activeUser);
    }

    @Override
    public int getItemCount() {
        return listActiveUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView thumnail;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumnail =itemView.findViewById(R.id.item_active_thumnail);
            name = itemView.findViewById(R.id.item_active_name);
        }
    }
}
