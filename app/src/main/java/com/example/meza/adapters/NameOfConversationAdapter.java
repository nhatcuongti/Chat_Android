package com.example.meza.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.activities.ChatActivity;
import com.example.meza.activities.HomePageActivity;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utils.Utilss;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by reiko-lhnhat on 2/25/2022.
 */
public class NameOfConversationAdapter extends RecyclerView.Adapter<NameOfConversationAdapter.ViewHolder> {
    ArrayList<ConversationModel> listRecentConversation;
    Context context;
    ArrayList<User> listObjectUserFriend;

    public NameOfConversationAdapter(Context c,ArrayList<ConversationModel> listRecentConversation, ArrayList<User> listObjectUserFriend) {
        context = c;
        this.listRecentConversation = listRecentConversation;
        this.listObjectUserFriend = listObjectUserFriend;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nameconversation_in_chats__vertical, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currentUser = User.getCurrentUser(context);
//        User chosenUser;

        ConversationModel conversation = listRecentConversation.get(position);
        String [] arrKey = conversation.getParticipant_list().keySet().toArray(new String[0]);
        String chosenUserPhoneNumber; // user was clicked by current user

        if(arrKey.length <= 2) {
            if(arrKey[0].equals(currentUser.getId()))
                chosenUserPhoneNumber = arrKey[1];
            else
                chosenUserPhoneNumber = arrKey[0];

            for (User u: listObjectUserFriend){
                if(u.getPhone_number().equals(chosenUserPhoneNumber)) {
//                    chosenUser = u;
                    holder.thumnail.setImageBitmap(Utilss.decodeImage(u.getImage()));
                    holder.name.setText(u.getFullname());
                    break;
                }
            }
        }
        else {
            // here: handle conversation with 2 or more
        }




        holder.lastMessage.setText(conversation.getLast_message());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("conversationID",listRecentConversation.get(holder.getAdapterPosition()).getID());
                Log.i("debug", "onClick: " + listRecentConversation.get(holder.getAdapterPosition()).getID());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRecentConversation.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView thumnail;
        TextView name, lastMessage;
        LinearLayout layout;

        private HomePageActivity.ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumnail =itemView.findViewById(R.id.item_conversation_thumnail);
            name = itemView.findViewById(R.id.item_name_in_chats);
            lastMessage = itemView.findViewById(R.id.item_last_message_in_chats);
            layout = itemView.findViewById(R.id.item_conversation_name_layout);
        }
    }
}
