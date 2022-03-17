package com.example.meza.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ConversationModel {


    //----New Data--//
    private String ID;
    private ArrayList<User> participantList;
    private ArrayList<Message> listMessage;
    private User creator;


    public static class Message {

        //---New Data--//
        private String id;
        private User sender;
        private String text;
        LocalDateTime startTime;


        //--- New Method ---//
        public Message(String id, User sender, String text, LocalDateTime startTime) {
            this.id = id;
            this.sender = sender;
            this.text = text;
            this.startTime = startTime;
        }

        public Message(Message copyMsg){
            this.id = copyMsg.id;
            this.sender = copyMsg.sender;
            this.text = copyMsg.text;
            this.startTime = copyMsg.startTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public User getSender() {
            return sender;
        }

        public void setSender(User sender) {
            this.sender = sender;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }
    }

    //--- New Method ---//
    public ConversationModel(String ID, ArrayList<User> participantList, ArrayList<Message> listMessage, User creator) {
        this.ID = ID;
        this.participantList = participantList;
        this.listMessage = listMessage;
        this.creator = creator;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ArrayList<User> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<User> participantList) {
        this.participantList = participantList;
    }

    public ArrayList<Message> getListMessage() {
        return listMessage;
    }

    public void setListMessage(ArrayList<Message> listMessage) {
        this.listMessage = listMessage;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
