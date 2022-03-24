package com.example.meza.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.R;
import com.example.meza.activities.ChatActivity;
import com.example.meza.databinding.ItemReceiveChatboxBinding;
import com.example.meza.databinding.ItemSendChatboxBinding;
import com.example.meza.interfaces.OnGetImageClickListener;
import com.example.meza.interfaces.OnGetValueListener;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utils.Utils;
import com.google.firebase.database.DataSnapshot;

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

            ItemSendViewHolder viewHolder = new ItemSendViewHolder(context, ItemSendChatboxBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            ));

            return  viewHolder;
        } else {

            ItemReceiveViewHolder viewHolder = new ItemReceiveViewHolder(context, ItemReceiveChatboxBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            ));

            return  viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        listMsg = conversationModel.getListMessage();
        // Lấy địa chỉ ảnh của đối tác
        // Đầu tiên sửa đổi dữ liệu trong class ConversationModel từ Map<String, Boolean> thành Map<String, String>
        // decode String sang Integer

        if (getItemViewType(position) == VIEW_SENT){ // Trong trường hợp currentUser gửi tin nhắn .

            //**************************************************************************************
                            //Xử lý khoảng cách giữa các item//

            int topMargin = 30;
            if (position >= 1 && getItemViewType(position - 1 ) == VIEW_SENT) // Nếu như item trước cũng là itemViewSent thì giảm topmargin
                topMargin = 10;
            //************************************End***********************************************




            //**************************************************************************************
                            //Xử lý phần "seen" //

            int partnerAvatar = -1;
            boolean seen = true; // Xem xét người dùng đã seen hay chưa
            if (position == listMsg.size() - 1 && seen){
                // Vì chưa có dữ liệu nên dùng lệnh dưới đây .
                partnerAvatar = R.drawable.hieule; // Gắn cứng image tại vì chưa có dữ liệu cụ thể
            }
            //***********************************End************************************************


            //**************************************************************************************
                            //Xem xét có nên để thời gian trên đầu item hay không //

            boolean setStartTime = false;

            ConversationModel.Message currentMsg = listMsg.get(position);
            if (position >= 1){
                ConversationModel.Message previousMsg = listMsg.get(position - 1);
                LocalDateTime startTime = currentMsg.getStartTime();
                LocalDateTime previousStartTime = previousMsg.getStartTime();
                if (isAfter30Minutes(previousStartTime, startTime))
                    setStartTime = true;
            }
            else
                setStartTime = true;
            //************************************End***********************************************



            ((ItemSendViewHolder) holder).setData(
                    currentMsg.getText(),
                    currentMsg.getTypeMessage(),
                    partnerAvatar,
                    topMargin,
                    (setStartTime) ? currentMsg.getStartTime() : null);
        }
        else { // Nếu các bên khác gửi tin nhắn về currentUser

            //**************************************************************************************
                                        //Xử lý khoảng cách giữa các item//

            int topMargin = 30;
            // Vì chưa có dữ liệu nên dùng lệnh dưới đây .
            int partnerAvatar = R.drawable.hieule;

            if (position >= 1 && getItemViewType(position - 1 ) == VIEW_RECEIVE){   // Nếu như item trước cũng là itemViewSent thì giảm topmargin
                topMargin = 10;
            }
            //************************************End***********************************************


            //**************************************************************************************
                                    //Xem xét có nên để thời gian trên đầu item hay khonog//

            boolean setStartTime = false;
            // Kiểm tra có để thời gian ở trên đầu hay không
            // Các trường hợp để thời gian trên đầu .
            // Đoạn tin nhắn trước cách đoạn tin nhắn hiện tại 30 phút
            ConversationModel.Message currentMsg = listMsg.get(position);

            if (position >= 1){
                ConversationModel.Message previousMsg = listMsg.get(position - 1);
                LocalDateTime startTime = currentMsg.getStartTime();
                LocalDateTime previousStartTime = previousMsg.getStartTime();
                if (isAfter30Minutes(previousStartTime, startTime))
                    setStartTime = true;
            }
            //************************************End***********************************************


            //**************************************************************************************
                            //Xem xét có nên để avatar ở bên trái đoạn tin nhắn hay không//

            // Cac th không để avatar
            // => Đoạn hội thoại kế bên trùng với đoạn hội thoại của mình và thời gian < 30 phút
            if (position + 1 < listMsg.size() && getItemViewType(position + 1 ) == VIEW_RECEIVE){
                ConversationModel.Message afterMsg = listMsg.get(position + 1);

                LocalDateTime startTime = currentMsg.getStartTime();
                LocalDateTime afterStartTime = afterMsg.getStartTime();

                Log.d("test", "OK");

                if (!isAfter30Minutes(startTime, afterStartTime))
                    partnerAvatar = -1;
            }
            //************************************End***********************************************

            ((ItemReceiveViewHolder) holder).setData(currentMsg.getText(),
                    currentMsg.getTypeMessage(),
                    partnerAvatar,
                    topMargin,
                    (setStartTime) ? currentMsg.getStartTime() : null);
        }

    }

    @Override
    public int getItemCount() {
        listMsg = conversationModel.getListMessage();
        if (listMsg == null || listMsg.isEmpty())
            return 0;

        return listMsg.size();
    }

    @Override
    public int getItemViewType(int position) {
        listMsg = conversationModel.getListMessage();
        ConversationModel.Message currentMsg = listMsg.get(position);
        Log.d("test", "ConversationAdapter : " + currentMsg.toString());
        String senderId = currentMsg.getSender();

        if (senderId.equals(currentUser.getId()))
            return VIEW_SENT;
        else
            return VIEW_RECEIVE;
    }

    public static class ItemSendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemSendChatboxBinding binding;
        Context context;

        public ItemSendViewHolder(Context context, ItemSendChatboxBinding itemSendChatboxBinding) {
            super(itemSendChatboxBinding.getRoot());
            binding = itemSendChatboxBinding;
            this.context = context;
        }

        public void setData(String msg, String typeMsg, int partnerImage, int topMargin, LocalDateTime startTime){
            Log.d("abcxyz", "setData: " + typeMsg);
            if (typeMsg.equals(Constants.KEY_TEXT)){ //  Nếu tin nhắn là đoạn text
                binding.message.setText(msg);
                binding.message.setVisibility(View.VISIBLE);
                binding.imgMessage.setVisibility(View.GONE);
                Log.d("abcxyz", "setData: " + binding.message.getVisibility());
            } else if (typeMsg.equals(Constants.KEY_IMAGE)){ // Nếu tin nhắn là hình ảnh
                Bitmap bitmap = Utils.decodeImage(msg);
                Bitmap resizeBitmap = Utils.resizedBitmap(bitmap, 400);
                binding.imgMessage.setImageBitmap(resizeBitmap);
                binding.imgMessage.setOnClickListener(this);
                binding.imgMessage.setVisibility(View.VISIBLE);
                binding.message.setVisibility(View.GONE);
                Log.d("abcxyz", "setData: " + binding.message.getVisibility());

            }

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


        @Override
        public void onClick(View view) {
            Bitmap bm=((BitmapDrawable)binding.imgMessage.getDrawable()).getBitmap();
            ((ChatActivity) context).onGetImageClick(bm);
        }
    }

    /**
     * ItemReceiveViewHolder là đoạn tin nhắn người khác gửi về cho currentUser
     */
    public static class ItemReceiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemReceiveChatboxBinding binding;
        Context context;

        public ItemReceiveViewHolder(Context context, ItemReceiveChatboxBinding itemReceiveChatboxBinding) {
            super(itemReceiveChatboxBinding.getRoot());
            binding = itemReceiveChatboxBinding;
            this.context = context;
        }

        public void setData(String msg, String typeMsg, Integer partnerAvatar, int topMargin, LocalDateTime startTime){
            if (typeMsg.equals(Constants.KEY_TEXT)){ //  Nếu tin nhắn là đoạn text
                binding.message.setText(msg);
                binding.message.setVisibility(View.VISIBLE);
                binding.imgMessage.setVisibility(View.GONE);
            } else if (typeMsg.equals(Constants.KEY_IMAGE)){ // Nếu tin nhắn là hình ảnh
                Bitmap bitmap = Utils.decodeImage(msg);
                binding.imgMessage.setImageBitmap(bitmap);
                binding.imgMessage.setOnClickListener(this);
                binding.imgMessage.setVisibility(View.VISIBLE);
                binding.message.setVisibility(View.GONE);
            }

            if (partnerAvatar != -1){ // Nếu ở đoạn tin nhắn này không được để ảnh
                binding.userImage.setImageResource(partnerAvatar);
                binding.userImage.setVisibility(View.VISIBLE);
                binding.userActive.setVisibility(View.VISIBLE);
            }
            else{ // Nếu đoạn tin nhắn này được để ảnh
                binding.userImage.setVisibility(View.INVISIBLE);
                binding.userActive.setVisibility(View.INVISIBLE);
            }

            if (startTime != null){ // Nếu ở trên đầu đoạn tin nhắn được để thời gian
                binding.startTime.setVisibility(View.VISIBLE);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formattedDateTime = startTime.format(formatter);
                binding.startTime.setText(formattedDateTime);
            }
            else // Nếu ở trên đầu đoạn tin nhắn không được để thời gian
                binding.startTime.setVisibility(View.GONE);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, topMargin, 0, 0);

            RelativeLayout viewParent = binding.getRoot();
            viewParent.setLayoutParams(lp);
        }

        @Override
        public void onClick(View view) {
            Bitmap bm=((BitmapDrawable)binding.imgMessage.getDrawable()).getBitmap();
            ((ChatActivity) context).onGetImageClick(bm);
        }
    }

    public static boolean isAfter30Minutes(LocalDateTime StartTime, LocalDateTime AfterStartTime){
        Duration duration = Duration.between(StartTime, AfterStartTime);
        if (duration.toMinutes() >= 30)
            return true;

        return false;
    }

}
