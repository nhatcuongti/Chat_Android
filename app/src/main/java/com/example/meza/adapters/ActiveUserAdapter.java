package com.example.meza.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.model.User;
import com.example.meza.utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 2/25/2022.
 */
public class ActiveUserAdapter extends RecyclerView.Adapter<ActiveUserAdapter.ViewHolder> {
    ArrayList<User> listActiveUser;

    public ActiveUserAdapter(ArrayList<User> listActiveUser) {
        this.listActiveUser = listActiveUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_in_activepeople__vertical, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User activeUser = listActiveUser.get(position);

        holder.thumnail.setImageBitmap(
                Utils.encodeBase64StringToBitMapImage(
                        listActiveUser.get(position).getImage()));

        holder.name.setText(activeUser.getFullname());
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
            thumnail =itemView.findViewById(R.id.item_thumnail_in_active_people);
            name = itemView.findViewById(R.id.item_name_in_active_people);
        }
    }
}
