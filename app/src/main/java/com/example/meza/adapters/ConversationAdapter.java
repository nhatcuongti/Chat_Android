package com.example.meza.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meza.activities.ChatActivity;
import com.example.meza.databinding.ItemReceiveChatboxBinding;
import com.example.meza.databinding.ItemSendChatboxBinding;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utils.Utilss;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ConversationModel conversationModel;
    private ArrayList<ConversationModel.Message> listMsg;
    private User currentUser;
    private Map<String, Bitmap> user_image;

    public final int VIEW_SENT = 1;
    public final int VIEW_RECEIVE = 0;

    public ConversationAdapter(Context context, ConversationModel conversationModel, User currentUser) {
        this.context = context;
        this.conversationModel = conversationModel;
        listMsg = conversationModel.getListMessage();
        this.currentUser = currentUser;

        user_image = conversationModel.getUser_image();
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
        ConversationModel.Message currentMsg = listMsg.get(position);
        String idUserSendMsg = currentMsg.getSender();

        if (getItemViewType(position) == VIEW_SENT){ // Trong trường hợp currentUser gửi tin nhắn .

            //**************************************************************************************
                            //Xử lý khoảng cách giữa các item//

            int topMargin = 30;
            if (position >= 1 && getItemViewType(position - 1 ) == VIEW_SENT) // Nếu như item trước cũng là itemViewSent thì giảm topmargin
                topMargin = 10;
            //************************************End***********************************************




            //**************************************************************************************
                            //Xử lý phần "seen" //

            Bitmap partnerAvatar = null;
            Map<String, Boolean> list_seen = currentMsg.getListSeen();
            boolean seen = (list_seen != null) ? true : false; // Xem xét người dùng đã seen hay chưa
            if (position == listMsg.size() - 1 && seen){
                for (String idUser : list_seen.keySet())
                    if (!idUser.equals(currentUser.getId())){
                        partnerAvatar = user_image.get(idUser);
                    }
            }

            if (position == listMsg.size() - 1){
                partnerAvatar = null;
                list_seen = currentMsg.getListSeen();
                seen = (!list_seen.isEmpty()) ? true : false; // Xem xét người dùng đã seen hay chưa
                if (position == listMsg.size() - 1 && seen){
                    for (String idUser : list_seen.keySet())
                        if (!idUser.equals(currentUser.getId())){
                            partnerAvatar = user_image.get(idUser);
                        }
                }
            }

            //***********************************End************************************************


            //**************************************************************************************
                            //Xem xét có nên để thời gian trên đầu item hay không //

            boolean setStartTime = false;

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
            Bitmap partnerAvatar = user_image.get(idUserSendMsg);

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
            currentMsg = listMsg.get(position);

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
                    partnerAvatar = null;
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

    public static class ItemSendViewHolder extends RecyclerView.ViewHolder {

        ItemSendChatboxBinding binding;
        Context context;

        public ItemSendViewHolder(Context context, ItemSendChatboxBinding itemSendChatboxBinding) {
            super(itemSendChatboxBinding.getRoot());
            binding = itemSendChatboxBinding;
            this.context = context;
        }

        public void setData(String msg, String typeMsg, Bitmap partnerImage, int topMargin, LocalDateTime startTime){
            //**************************************************************************************
                                    //Xử lý dữ liệu đoạn tin nhắn (Hỉnh ảnh, text, file, ...)/
            ConversationAdapter.setMessage(binding.message,
                    binding.imgMessage,
                    msg,
                    typeMsg,
                    context);
            //************************************End***********************************************


            //**************************************************************************************
                                        //Xử lý ảnh và thời gian
            if (partnerImage != null){
                binding.userImage.setImageBitmap(partnerImage);
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
            //************************************End***********************************************

            //**************************************************************************************
                                        //Set parameter của ViewGroup Relative Layout
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, topMargin, 0, 0);

            RelativeLayout viewParent = binding.getRoot();
            viewParent.setLayoutParams(lp);
            //************************************End***********************************************

        }

    }

    /**
     * ItemReceiveViewHolder là đoạn tin nhắn người khác gửi về cho currentUser
     */
    public static class ItemReceiveViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveChatboxBinding binding;
        Context context;

        public ItemReceiveViewHolder(Context context, ItemReceiveChatboxBinding itemReceiveChatboxBinding) {
            super(itemReceiveChatboxBinding.getRoot());
            binding = itemReceiveChatboxBinding;
            this.context = context;
        }

        public void setData(String msg, String typeMsg, Bitmap partnerAvatar, int topMargin, LocalDateTime startTime){
            //**************************************************************************************
                                //Xử lý dữ liệu đoạn tin nhắn (Hỉnh ảnh, text, file, ...)
            ConversationAdapter.setMessage(binding.message,
                    binding.imgMessage,
                    msg,
                    typeMsg,
                    context);
            //************************************End***********************************************


            //**************************************************************************************
                                        //Xử lý ảnh và thời gian
            ConversationAdapter.setImageAndTime(binding.userImage,
                    binding.userActive,
                    binding.startTime,
                    startTime,
                    partnerAvatar);
            //************************************End***********************************************


            //**************************************************************************************
                                    //Set parameter của ViewGroup Relative Layout
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, topMargin, 0, 0);

            RelativeLayout viewParent = binding.getRoot();
            viewParent.setLayoutParams(lp);
            //************************************End***********************************************

        }
    }

    public static void setMessage(TextView messageTxt,
                               ImageView imgMessage,
                               String msg,
                               String typeMsg,
                               Context context) {
            switch (typeMsg){
            case Constants.KEY_TEXT:{
                messageTxt.setText(msg);
                messageTxt.setVisibility(View.VISIBLE);
                imgMessage.setVisibility(View.GONE);
                break;
            }
            case Constants.KEY_IMAGE:{
                Bitmap bitmap = Utilss.decodeImage(msg);
                Bitmap resizeBitmap = Utilss.resizedBitmap(bitmap, 400);
                imgMessage.setImageBitmap(resizeBitmap);
                imgMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ChatActivity) context).onGetImageClick(msg);
                    }
                });
                imgMessage.setVisibility(View.VISIBLE);
                messageTxt.setVisibility(View.GONE);
                break;
            }
        }
    }

    public static void setImageAndTime(CircleImageView userImage,
                                       CircleImageView userActive,
                                       TextView startTimeText,
                                       LocalDateTime startTime,
                                       Bitmap partnerAvatar){
        if (partnerAvatar != null){ // Nếu ở đoạn tin nhắn này không được để ảnh
            userImage.setImageBitmap(partnerAvatar);
            userImage.setVisibility(View.VISIBLE);
            userActive.setVisibility(View.VISIBLE);
        }
        else{ // Nếu đoạn tin nhắn này được để ảnh
            userImage.setVisibility(View.INVISIBLE);
            userActive.setVisibility(View.INVISIBLE);
        }

        if (startTime != null){ // Nếu ở trên đầu đoạn tin nhắn được để thời gian
            startTimeText.setVisibility(View.VISIBLE);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDateTime = startTime.format(formatter);
            startTimeText.setText(formattedDateTime);
        }
        else // Nếu ở trên đầu đoạn tin nhắn không được để thời gian
            startTimeText.setVisibility(View.GONE);
    }

    public static boolean isAfter30Minutes(LocalDateTime StartTime, LocalDateTime AfterStartTime){
        Duration duration = Duration.between(StartTime, AfterStartTime);
        if (duration.toMinutes() >= 30)
            return true;

        return false;
    }



}
