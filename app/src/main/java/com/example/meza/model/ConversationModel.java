package com.example.meza.model;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.meza.interfaces.OnGetValueListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ConversationModel {


    //----New Data--//
    private String ID;
    private HashMap<String, Boolean> participant_list;
    private ArrayList<String> participantListArray;
    private ArrayList<ConversationModel.Message> message_list;
    private String creator;

    // ---- new data was added by nhat---
    private String tittle;
    private Long last_time; // thoi gian gui cua tin nhan cuoi cung
    private String last_message; // tin nhan cuoi cung


    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public Long getLast_time() {
        return last_time;
    }

    public void setLast_time(Long last_time) {
        this.last_time = last_time;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public ConversationModel(){

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public HashMap<String, Boolean> getParticipant_list() {
        return participant_list;
    }

    public void setParticipant_list(HashMap<String, Boolean> participant_list) {
        this.participant_list = participant_list;
    }

    public ArrayList<String> getParticipantListArray() {
        return participantListArray;
    }

    public void setParticipantListArray(ArrayList<String> participantListArray) {
        this.participantListArray = participantListArray;
    }

    public ArrayList<ConversationModel.Message> getListMessage() {
        return message_list;
    }

    public void setListMessage(ArrayList<ConversationModel.Message> listMessage) {
        this.message_list = listMessage;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public static void getFirstConversationWithID(String id,
                                                  OnGetValueListener onGetValueListener){
        String path = "/conversation/" + id;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (onGetValueListener != null)
                    onGetValueListener.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public String toString() {
        return "ConversationModel{" +
                "ID='" + ID + '\'' +
                ", participant_list=" + participantListArray +
                ", message_list=" + message_list +
                ", creator='" + creator + '\'' +
                '}';
    }

    public void formatParticipantList() {
        participantListArray = new ArrayList<>(participant_list.keySet());
    }

    public static class Message {

        //---New Data--//
        private String id;
        private String sender;
        private String text;
        LocalDateTime startTime;
        private long timestamp;

        public Message(){

        }

        //--- New Method ---//
        public Message(String id, String sender, String text, LocalDateTime startTime) {
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

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
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

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public void formatStartTime(){
            startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                    TimeZone.getDefault().toZoneId());
        }

        private static ArrayList<ConversationModel.Message> list_message = new ArrayList<>();

        public static void listenChange(String id, OnGetValueListener onGetValueListener){
            String path = "/message/" + id;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

            // Xử lý khi có thay đổi database
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    onGetValueListener.onSuccess(snapshot);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Message msg = snapshot.getValue(ConversationModel.Message.class);

                    if (list_message == null || list_message.isEmpty())
                        return;

                    for (int i = 0; i < list_message.size(); i++)
                        if (msg.getId().equals(list_message.get(i).getId())){
                            list_message.remove(i);
                            break;
                        }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public static ConversationModel.Message getMessageWithID(String id){
            for (ConversationModel.Message msg : list_message)
                if (msg.getId().equals(id))
                    return msg;

            return null;
        }

        public Map<String, Object> toMap(){
            Map<String, Object> msgData = new HashMap<>();
            msgData.put("sender", sender);
            msgData.put("id", id);
            msgData.put("text", text);
            msgData.put("timestamp", timestamp);
            return msgData;
        }

        public static void insertNewMsgToDatabase(ConversationModel.Message msg, String id, String idConversation){
            String path = "/message/" + idConversation ;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
            databaseReference.child(id).setValue(msg.toMap());
        }

        public static void listenLastElement(String idConv, OnGetValueListener onGetValueListener){
            String path = "/message/" + idConv;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
            Query lastQuery = databaseReference.orderByKey().limitToLast(1);

            lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    onGetValueListener.onSuccess(snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public String toString() {
            return "Message{" +
                    "id='" + id + '\'' +
                    ", sender='" + sender + '\'' +
                    ", text='" + text + '\'' +
                    ", startTime=" + startTime +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
