package com.example.meza.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.model.ConversationModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    Context context;
    ConversationModel conversationModel;
    ArrayList<ConversationModel.Message> copyMsg = new ArrayList<>();

    public ConversationAdapter(Context context, ConversationModel conversationModel) {
        this.context = context;
        this.conversationModel = conversationModel;

        for (ConversationModel.Message msg : conversationModel.getListMessage())
            copyMsg.add(new ConversationModel.Message(msg));
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Chuyển item_receive_chatbox hoặc item_send_chatbox thành View (Tùy thuộc vào tin nhắn đang có là gì )
        // Tuy nhiên làm sao để lấy view phù hợp, Tại vì mình đâu biết view nào ở vị trí hiện tại đâu ?
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        if (copyMsg.get(0).isiSend()){
            copyMsg.remove(0);
            return new ViewHolder(layoutInflater.inflate(R.layout.item_send_chatbox, parent, false));
        }
        else{
            copyMsg.remove(0);
            return new ViewHolder(layoutInflater.inflate(R.layout.item_receive_chatbox, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversationModel.Message msg = conversationModel.getListMessage().get(position);
        holder.message.setText(msg.getMessage());
        if (holder.userImage != null)
            holder.userImage.setImageResource(conversationModel.getPartnerImage());

    }

    @Override
    public int getItemCount() {
        return conversationModel.getListMessage().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            message = itemView.findViewById(R.id.message);
        }
    }
}
