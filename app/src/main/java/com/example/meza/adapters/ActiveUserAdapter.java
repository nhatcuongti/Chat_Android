package com.example.meza.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.activities.ChatActivity;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 2/25/2022.
 */
public class ActiveUserAdapter extends RecyclerView.Adapter<ActiveUserAdapter.ViewHolder> {
    ArrayList<User> listActiveUser;
    ArrayList<ConversationModel> listRecentConversation;
    Context mContext;

    public ActiveUserAdapter(Context mContext,ArrayList<User> listActiveUser, ArrayList<ConversationModel> listRecentConversation) {
        this.listActiveUser = listActiveUser;
        this.listRecentConversation = listRecentConversation;
        this.mContext = mContext;
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
                Utils.decodeImage(
                        listActiveUser.get(position).getImage()));

        holder.name.setText(activeUser.getFullname());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                Bundle bundle = new Bundle();

                ConversationModel temp = null; //

                for(ConversationModel cm: listRecentConversation){
                    if(cm.getParticipant_list().get(
                            listActiveUser.get(
                                    holder.getAdapterPosition()).getPhone_number()) != null){

                        temp = cm;
                        break;
                    }
                }

                if(temp != null){
                    bundle.putString("conversationID",temp.getID());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
                else {
                    // not exists conversation between cur user and chosen user --> create new conversation
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listActiveUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView thumnail;
        TextView name;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumnail =itemView.findViewById(R.id.item_thumnail_in_active_people);
            name = itemView.findViewById(R.id.item_name_in_active_people);
            cardView = itemView.findViewById(R.id.item_user_in_active_people_frag_layout);
        }
    }
}
