package com.example.meza.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.activities.ChatActivity;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utils.Utilss;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 2/25/2022.
 */
public class ActiveThumnailAdapter extends RecyclerView.Adapter<ActiveThumnailAdapter.ViewHolder> {
    ArrayList<User> listActiveUser;
    ArrayList<ConversationModel> listRecentConversation;
    Context mContext;

    public ActiveThumnailAdapter(Context c,ArrayList<User> listActiveUser, ArrayList<ConversationModel> listRecentConversation) {
        this.listActiveUser = listActiveUser;
        mContext = c;
        this.listRecentConversation = listRecentConversation;
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
        User activeUser = listActiveUser.get(position);

        holder.thumnail.setImageBitmap(
                Utilss.decodeImage(
                        listActiveUser.get(position).getImage()));

        holder.name.setText(activeUser.getFullname());

        holder.layout.setOnClickListener(new View.OnClickListener() {
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
        LinearLayout layout;
        FrameLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumnail =itemView.findViewById(R.id.item_active_thumnail);
            name = itemView.findViewById(R.id.item_active_name);
            container = itemView.findViewById(R.id.framelayout_thumnail);
            layout = itemView.findViewById(R.id.item_active_thumnail_layout);
        }
    }
}
