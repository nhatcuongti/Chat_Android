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
import com.example.meza.databinding.ItemLoadingBinding;
import com.example.meza.databinding.ItemReceiveChatboxBinding;
import com.example.meza.databinding.ItemSendChatboxBinding;
import com.example.meza.model.ConversationModel;
import com.example.meza.model.User;
import com.example.meza.utilities.Constants;
import com.example.meza.utils.Utils;
import com.google.api.LogDescriptor;

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
    private static Boolean isPartnerActive = false;

    public final int VIEW_LOADING = 2;
    public final int VIEW_SENT = 1;
    public final int VIEW_RECEIVE = 0;
    Boolean isLoadingAdd = false;

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
        } else if (viewType == VIEW_RECEIVE) {

            ItemReceiveViewHolder viewHolder = new ItemReceiveViewHolder(context, ItemReceiveChatboxBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            ));

            return  viewHolder;
        } else {
            ItemLoadingViewHolder viewHolder = new ItemLoadingViewHolder(context, ItemLoadingBinding.inflate(
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

        if (getItemViewType(position) == VIEW_SENT){ // Trong tr?????ng h???p currentUser g???i tin nh???n .

            //**************************************************************************************
                            //X??? l?? kho???ng c??ch gi???a c??c item//

            int topMargin = 30;
            if (position >= 1 && getItemViewType(position - 1 ) == VIEW_SENT) // N???u nh?? item tr?????c c??ng l?? itemViewSent th?? gi???m topmargin
                topMargin = 10;
            //************************************End***********************************************




            //**************************************************************************************
                            //X??? l?? ph???n "seen" //

            Bitmap partnerAvatar = null;
            Map<String, Boolean> list_seen = currentMsg.getListSeen();
            boolean seen = (list_seen != null) ? true : false; // Xem x??t ng?????i d??ng ???? seen hay ch??a
            if (position == listMsg.size() - 1 && seen){
                for (String idUser : list_seen.keySet())
                    if (!idUser.equals(currentUser.getId())){
                        partnerAvatar = user_image.get(idUser);
                    }
            }

            if (position == listMsg.size() - 1){
                partnerAvatar = null;
                list_seen = currentMsg.getListSeen();
                seen = (!list_seen.isEmpty()) ? true : false; // Xem x??t ng?????i d??ng ???? seen hay ch??a
                if (position == listMsg.size() - 1 && seen){
                    for (String idUser : list_seen.keySet())
                        if (!idUser.equals(currentUser.getId())){
                            partnerAvatar = user_image.get(idUser);
                        }
                }
            }

            //***********************************End************************************************


            //**************************************************************************************
                            //Xem x??t c?? n??n ????? th???i gian tr??n ?????u item hay kh??ng //

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
        else if (getItemViewType(position) == VIEW_RECEIVE) { // N???u c??c b??n kh??c g???i tin nh???n v??? currentUser

            //**************************************************************************************
                                        //X??? l?? kho???ng c??ch gi???a c??c item//

            int topMargin = 30;
            // V?? ch??a c?? d??? li???u n??n d??ng l???nh d?????i ????y .
            Bitmap partnerAvatar = user_image.get(idUserSendMsg);

            if (position >= 1 && getItemViewType(position - 1 ) == VIEW_RECEIVE){   // N???u nh?? item tr?????c c??ng l?? itemViewSent th?? gi???m topmargin
                topMargin = 10;
            }
            //************************************End***********************************************


            //**************************************************************************************
                                    //Xem x??t c?? n??n ????? th???i gian tr??n ?????u item hay khonog//

            boolean setStartTime = false;
            // Ki???m tra c?? ????? th???i gian ??? tr??n ?????u hay kh??ng
            // C??c tr?????ng h???p ????? th???i gian tr??n ?????u .
            // ??o???n tin nh???n tr?????c c??ch ??o???n tin nh???n hi???n t???i 30 ph??t
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
                            //Xem x??t c?? n??n ????? avatar ??? b??n tr??i ??o???n tin nh???n hay kh??ng//

            // Cac th kh??ng ????? avatar
            // => ??o???n h???i tho???i k??? b??n tr??ng v???i ??o???n h???i tho???i c???a m??nh v?? th???i gian < 30 ph??t
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
        String senderId = currentMsg.getSender();

        Log.d("ItemViewTypeDebug", "list Msg: " + listMsg.toString());
        Log.d("ItemViewTypeDebug", "(position, isLoadingAdd, sender) = " + "(" + position + "," + isLoadingAdd + "," + senderId + ")");

        if (isLoadingAdd && position == 0)
            return VIEW_LOADING;
        else if (senderId.equals(currentUser.getId()))
            return VIEW_SENT;
        else
            return VIEW_RECEIVE;
    }

    public void addLoading(){
        isLoadingAdd = true;
        listMsg = conversationModel.getListMessage();
        listMsg.add(0, new ConversationModel.Message());
        conversationModel.setListMessage(listMsg);
        notifyItemInserted(0);
    }

    public void removeLoading(){
        isLoadingAdd = false;

        listMsg = conversationModel.getListMessage();
        if (!listMsg.isEmpty()){
            listMsg.remove(0);
            notifyItemRemoved(0);
        }
    }


    public static class ItemSendViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ItemSendChatboxBinding binding;
        Context context;

        public ItemSendViewHolder(Context context, ItemSendChatboxBinding itemSendChatboxBinding) {
            super(itemSendChatboxBinding.getRoot());
            binding = itemSendChatboxBinding;
            binding.getRoot().setOnLongClickListener(this);
            this.context = context;
        }

        public void setData(String msg, String typeMsg, Bitmap partnerImage, int topMargin, LocalDateTime startTime){
            //**************************************************************************************
                                    //X??? l?? d??? li???u ??o???n tin nh???n (H???nh ???nh, text, file, ...)/
            ConversationAdapter.setMessage(binding.message,
                    binding.imgMessage,
                    msg,
                    typeMsg,
                    context);
            //************************************End***********************************************


            //**************************************************************************************
                                        //X??? l?? ???nh v?? th???i gian
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
                                        //Set parameter c???a ViewGroup Relative Layout
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, topMargin, 0, 0);

            RelativeLayout viewParent = binding.getRoot();
            viewParent.setLayoutParams(lp);
            //************************************End***********************************************

        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatActivity) context).onLongClickItem(getAdapterPosition());
            return false;
        }
    }

    /**
     * ItemReceiveViewHolder l?? ??o???n tin nh???n ng?????i kh??c g???i v??? cho currentUser
     */
    public static class ItemReceiveViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ItemReceiveChatboxBinding binding;
        Context context;

        public ItemReceiveViewHolder(Context context, ItemReceiveChatboxBinding itemReceiveChatboxBinding) {
            super(itemReceiveChatboxBinding.getRoot());
            binding = itemReceiveChatboxBinding;
            binding.getRoot().setOnLongClickListener(this);
            this.context = context;
        }

        public void setData(String msg, String typeMsg, Bitmap partnerAvatar, int topMargin, LocalDateTime startTime){
            //**************************************************************************************
                                //X??? l?? d??? li???u ??o???n tin nh???n (H???nh ???nh, text, file, ...)
            ConversationAdapter.setMessage(binding.message,
                    binding.imgMessage,
                    msg,
                    typeMsg,
                    context);
            //************************************End***********************************************


            //**************************************************************************************
                                        //X??? l?? ???nh v?? th???i gian
            ConversationAdapter.setImageAndTime(binding.userImage,
                    binding.userActive,
                    binding.startTime,
                    startTime,
                    partnerAvatar);
            //************************************End***********************************************


            //**************************************************************************************
                                    //Set parameter c???a ViewGroup Relative Layout
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, topMargin, 0, 0);

            RelativeLayout viewParent = binding.getRoot();
            viewParent.setLayoutParams(lp);
            //************************************End***********************************************

        }

        @Override
        public boolean onLongClick(View view) {
            ((ChatActivity) context).onLongClickItem(getAdapterPosition());
            return false;
        }
    }

    public static class ItemLoadingViewHolder extends RecyclerView.ViewHolder {

        ItemLoadingBinding binding;
        Context context;

        public ItemLoadingViewHolder(Context context, ItemLoadingBinding itemLoadingBinding) {
            super(itemLoadingBinding.getRoot());
            binding = itemLoadingBinding;
            this.context = context;
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
                Bitmap bitmap = Utils.decodeImage(msg);
                Bitmap resizeBitmap = Utils.resizedBitmap(bitmap, 400);
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
        if (partnerAvatar != null){ // N???u ??? ??o???n tin nh???n n??y kh??ng ???????c ????? ???nh
            userImage.setImageBitmap(partnerAvatar);
            userImage.setVisibility(View.VISIBLE);
            if (isPartnerActive)
                userActive.setVisibility(View.VISIBLE);
            else
                userActive.setVisibility(View.INVISIBLE);
        }
        else{ // N???u ??o???n tin nh???n n??y ???????c ????? ???nh
            userImage.setVisibility(View.INVISIBLE);
            userActive.setVisibility(View.INVISIBLE);
        }

        if (startTime != null){ // N???u ??? tr??n ?????u ??o???n tin nh???n ???????c ????? th???i gian
            startTimeText.setVisibility(View.VISIBLE);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDateTime = startTime.format(formatter);
            startTimeText.setText(formattedDateTime);
        }
        else // N???u ??? tr??n ?????u ??o???n tin nh???n kh??ng ???????c ????? th???i gian
            startTimeText.setVisibility(View.GONE);
    }

    public static boolean isAfter30Minutes(LocalDateTime StartTime, LocalDateTime AfterStartTime){
        Duration duration = Duration.between(StartTime, AfterStartTime);
        if (duration.toMinutes() >= 30)
            return true;

        return false;
    }

    public void setPartnerActive(Boolean partnerActive) {
        isPartnerActive = partnerActive;
    }

    public static interface OnLongClickItem{
        public void onLongClickItem(Integer adapterPosition);
    }
}
