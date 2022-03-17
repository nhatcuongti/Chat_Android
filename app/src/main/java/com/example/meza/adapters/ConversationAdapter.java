package com.example.meza.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.databinding.ItemReceiveChatboxBinding;
import com.example.meza.databinding.ItemSendChatboxBinding;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ConversationModel conversationModel;
    private ArrayList<ConversationModel.Message> listMsg;
    private User currentUser;

    public final int VIEW_SENT = 1;
    public final int VIEW_RECEIVE = 0;

    public ConversationAdapter(Context context, ConversationModel conversationModel, User currentUser) {
        this.context = context;
        this.conversationModel = conversationModel;
        listMsg = conversationModel.getListMessage();
        this.currentUser = currentUser;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_SENT){

            ItemSendViewHolder viewHolder = new ItemSendViewHolder(ItemSendChatboxBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            ));

            return  viewHolder;
        } else {

            ItemReceiveViewHolder viewHolder = new ItemReceiveViewHolder(ItemReceiveChatboxBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            ));

            return  viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_SENT){
            int topMargin = 30;
            if (position >= 1 && getItemViewType(position - 1 ) == VIEW_SENT) // Nếu như item trước cũng là itemViewSent thì giảm topmargin
                topMargin = 10;

            int partnerAvatar = -1;
            boolean seen = true; // Xem xét người dùng đã seen hay chưa
            Log.d("test", "position: " + position);
            Log.d("test", "size: " + listMsg.size());

            if (position == listMsg.size() - 1 && seen){
                // Vì chưa có dữ liệu nên dùng lệnh dưới đây .
                partnerAvatar = R.drawable.hieule;
            }

            // Kiểm tra có để thời gian ở trên đầu hay không
            // Các trường hợp để thời gian trên đầu .
            // Đoạn tin nhắn trước cách đoạn tin nhắn hiện tại 30 phút
            boolean setStartTime = false;
            if (position >= 1){
                LocalDateTime startTime = listMsg.get(position).getStartTime();
                LocalDateTime previousStartTime = listMsg.get(position - 1).getStartTime();
                if (isAfter30Minutes(previousStartTime, startTime))
                    setStartTime = true;
            }
            else
                setStartTime = true;


            ((ItemSendViewHolder) holder).setData(
                    listMsg.get(position).getText(),
                    partnerAvatar,
                    topMargin,
                    (setStartTime) ? listMsg.get(position).getStartTime() : null);
        }
        else {
            int topMargin = 30;

            // Vì chưa có dữ liệu nên dùng lệnh dưới đây .
            int partnerAvatar = R.drawable.hieule;

            if (position >= 1 && getItemViewType(position - 1 ) == VIEW_RECEIVE){   // Nếu như item trước cũng là itemViewSent thì giảm topmargin
                topMargin = 10;
            }

            boolean setStartTime = false;
            // Kiểm tra có để thời gian ở trên đầu hay không
            // Các trường hợp để thời gian trên đầu .
            // Đoạn tin nhắn trước cách đoạn tin nhắn hiện tại 30 phút
            if (position >= 1){
                LocalDateTime startTime = listMsg.get(position).getStartTime();
                LocalDateTime previousStartTime = listMsg.get(position - 1).getStartTime();
                if (isAfter30Minutes(previousStartTime, startTime))
                    setStartTime = true;
            }


            // Kiểm tra để xem có nên để avatar kế bên đoạn chat và hiện ra ngày trên đầu hay không
            // Cac th không để avatar
            // => Đoạn hội thoại kế bên trùng với đoạn hội thoại của mình và thời gian < 30 phút

            if (position + 1 < listMsg.size() && getItemViewType(position + 1 ) == VIEW_RECEIVE){
                LocalDateTime startTime = listMsg.get(position).getStartTime();
                LocalDateTime afterStartTime = listMsg.get(position + 1).getStartTime();

                Log.d("test", "OK");

                if (!isAfter30Minutes(startTime, afterStartTime))
                    partnerAvatar = -1;
            }




            ((ItemReceiveViewHolder) holder).setData(listMsg.get(position).getText(),
                    partnerAvatar,
                    topMargin,
                    (setStartTime) ? listMsg.get(position).getStartTime() : null);
        }

    }

    @Override
    public int getItemCount() {
        return listMsg.size();
    }

    @Override
    public int getItemViewType(int position) {
        User sender = listMsg.get(position).getSender();
        if (sender.getId().equals(currentUser.getId()))
            return VIEW_SENT;
        else
            return VIEW_RECEIVE;
    }

    public static class ItemSendViewHolder extends RecyclerView.ViewHolder {

        ItemSendChatboxBinding binding;

        public ItemSendViewHolder(ItemSendChatboxBinding itemSendChatboxBinding) {
            super(itemSendChatboxBinding.getRoot());
            binding = itemSendChatboxBinding;
        }

        public void setData(String msg, int partnerImage, int topMargin, LocalDateTime startTime){
            binding.message.setText(msg);
            if (partnerImage != -1){
                binding.userImage.setImageResource(partnerImage);
                binding.userImage.setVisibility(View.VISIBLE);
            }
            else
                binding.userImage.setVisibility(View.INVISIBLE);

            if (startTime != null){
                binding.startTime.setVisibility(View.VISIBLE);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formattedDateTime = startTime.format(formatter);
                binding.startTime.setText(formattedDateTime);
            }
            else
                binding.startTime.setVisibility(View.GONE);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, topMargin, 0, 0);

            RelativeLayout viewParent = binding.getRoot();
            viewParent.setLayoutParams(lp);
        }


    }

    public static class ItemReceiveViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveChatboxBinding binding;

        public ItemReceiveViewHolder(ItemReceiveChatboxBinding itemReceiveChatboxBinding) {
            super(itemReceiveChatboxBinding.getRoot());
            binding = itemReceiveChatboxBinding;
        }

        public void setData(String msg, Integer partnerAvatar, int topMargin, LocalDateTime startTime){
            binding.message.setText(msg);
            if (partnerAvatar != -1){
                binding.userImage.setImageResource(partnerAvatar);
                binding.userImage.setVisibility(View.VISIBLE);
                binding.userActive.setVisibility(View.VISIBLE);
            }
            else{
                binding.userImage.setVisibility(View.INVISIBLE);
                binding.userActive.setVisibility(View.INVISIBLE);
            }

            if (startTime != null){
                binding.startTime.setVisibility(View.VISIBLE);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formattedDateTime = startTime.format(formatter);
                binding.startTime.setText(formattedDateTime);
            }
            else
                binding.startTime.setVisibility(View.GONE);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, topMargin, 0, 0);

            RelativeLayout viewParent = binding.getRoot();
            viewParent.setLayoutParams(lp);
        }


    }

    public static boolean isAfter30Minutes(LocalDateTime StartTime, LocalDateTime AfterStartTime){
        Duration duration = Duration.between(StartTime, AfterStartTime);
        if (duration.toMinutes() >= 30)
            return true;

        return false;
    }

}
