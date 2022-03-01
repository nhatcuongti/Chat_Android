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
public class NameOfConversationAdapter extends RecyclerView.Adapter<NameOfConversationAdapter.ViewHolder> {
    ArrayList<String> listRecentConversation;

    public NameOfConversationAdapter(ArrayList<String> listRecentConversation) {
        this.listRecentConversation = listRecentConversation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nameconversation_in_chats__vertical, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String conversation = listRecentConversation.get(position);
        holder.thumnail.setImageResource(R.drawable.asite);
        holder.name.setText(conversation);
    }

    @Override
    public int getItemCount() {
        return listRecentConversation.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView thumnail;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumnail =itemView.findViewById(R.id.item_conversation_thumnail);
            name = itemView.findViewById(R.id.item_name_in_chats);
        }
    }
}
