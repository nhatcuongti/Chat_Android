package com.example.meza.model;

import java.util.ArrayList;

public class ConversationModel {
    //Dữ liệu cần phải có : (Giả sử chat với người A)
    // - Danh sách tất cả đoạn chat của mình với người A
    // - Danh sách tất cả đoạn chat của người A với Mình
    // => Cùng một loại dữ liệu, đặt tên là conversation

    //Conversation gồm có :
    // - UserID (Mình)
    // - partnerID(Đối phương) (Từ đây sẽ biết được Name, Image và tình trạng hoạt động)
    // - ArrayList<Messenger> (Messenger gồm : ISend = true nếu mình gửi, false nếu đối tác gửi và đoạn tin nhắn, thể loại)

//    private String userID;
//    private String partnerID;
    private String partnerName;
    private Integer partnerImage;
    private boolean isPartnerActive;
    private ArrayList<Message> listMessage;

    public static class Message {
        boolean iSend ;
        String message;
//        String typeMessage;


        public  Message(boolean iSend, String message) {
            this.iSend = iSend;
            this.message = message;
        }

        public Message(Message copyMsg){
            this.iSend = copyMsg.iSend;
            this.message = copyMsg.message;
        }

        public boolean isiSend() {
            return iSend;
        }

        public void setiSend(boolean iSend) {
            this.iSend = iSend;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public ConversationModel(String partnerName, Integer partnerImage, boolean isPartnerActive, ArrayList<Message> listMessage) {
        this.partnerName = partnerName;
        this.partnerImage = partnerImage;
        this.isPartnerActive = isPartnerActive;
        this.listMessage = listMessage;
    }


    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Integer getPartnerImage() {
        return partnerImage;
    }

    public void setPartnerImage(Integer partnerImage) {
        this.partnerImage = partnerImage;
    }

    public boolean isPartnerActive() {
        return isPartnerActive;
    }

    public void setPartnerActive(boolean partnerActive) {
        isPartnerActive = partnerActive;
    }

    public ArrayList<Message> getListMessage() {
        return listMessage;
    }

    public void setListMessage(ArrayList<Message> listMessage) {
        this.listMessage = listMessage;
    }
}
