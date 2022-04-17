package com.example.meza.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.meza.R;
import com.example.meza.interfaces.OnGetValueListener;
import com.example.meza.utils.Utils;
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
    HashMap<String, Bitmap> user_image;

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

    public void formatParticipantList(Context context, OnGetValueListener onGetValueListener) {
        participantListArray = new ArrayList<>(participant_list.keySet());

        user_image = new HashMap<>();
        for (String user : getParticipantListArray()) {
            User.listenForUserList(user, new OnGetValueListener() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String imageDecode = ds.child("image").getValue(String.class);
                        String idUser = ds.getKey();
                        if (participant_list.get(idUser) != null){
                            if (imageDecode != null)
                                user_image.put(idUser, Utils.decodeImage(imageDecode));
                            else {
                                Bitmap bitmapTmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_user_image);
                                user_image.put(idUser, bitmapTmp);
                            }
                        }
                    }

                    if (onGetValueListener != null)
                        onGetValueListener.onSuccess(null);
                }

                @Override
                public void onChange(DataSnapshot snapshot) {

                }
            });
        }
    }

    public HashMap<String, Bitmap> getUser_image() {
        return user_image;
    }

    public void setUser_image(HashMap<String, Bitmap> user_image) {
        this.user_image = user_image;
    }

    public static void updateConversation(String idConv, Map<String, Object> convMap){
        String path = "conversation";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);

        databaseReference.child(idConv).updateChildren(convMap);
    }

    public Map<String, Object> toMap(){
        Map<String, Object> convData = new HashMap<>();
        convData.put("creator", creator);
        convData.put("id", ID);
        convData.put("last_message", last_message);
        convData.put("last_time", last_time);
        return convData;
    }

    public static class Message {

        //---New Data--//
        private String id;
        private String sender;
        private String text;
        LocalDateTime startTime;
        private long timestamp;
        private String typeMessage;
        private Map<String, Boolean> listSeen = null;

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

        public Map<String, Boolean> getListSeen() {
            return listSeen;
        }

        public void setListSeen(Map<String, Boolean> listSeen) {
            this.listSeen = listSeen;
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

        public String getTypeMessage() {
            return typeMessage;
        }

        public void setTypeMessage(String typeMessage) {
            this.typeMessage = typeMessage;
        }

        public void formatStartTime(){
            startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                    TimeZone.getDefault().toZoneId());
        }

        private static ArrayList<ConversationModel.Message> list_message = new ArrayList<>();

        public static DatabaseReference chatReference;
        public static ChildEventListener childEventListener;

        public static void listenFirstMessage(String id, OnGetValueListener onGetValueListener) {
            String path = "/message/" + id;
            chatReference = FirebaseDatabase.getInstance().getReference(path);
            Query query=chatReference.orderByKey().limitToLast(15);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    onGetValueListener.onSuccess(snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public static void listenMessageAtOffset(String firstMessageId, String convId, OnGetValueListener onGetValueListener) {
            String path = "/message/" + convId;
            chatReference = FirebaseDatabase.getInstance().getReference(path);
            Query query = chatReference.orderByKey().endBefore(firstMessageId).limitToLast(15);
            Log.d("MessageOffset", "listenMessageAtOffset: " + firstMessageId );

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        onGetValueListener.onSuccess(snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public static void listenChangTest(String id, String idlastMsg, OnGetValueListener onGetValueListener){
            String path = "/message/" + id;
            chatReference = FirebaseDatabase.getInstance().getReference(path);

            // Xử lý khi có thay đổi database
            chatReference.orderByKey().startAfter(idlastMsg).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("ConvDB", "onChildAdded: ");
                    onGetValueListener.onSuccess(snapshot);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("ConvDB", "onChildChanged: ");
                    onGetValueListener.onChange(snapshot);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.d("ConvDB", "onChildChanged: ");
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("ConvDB", "onChildMoved: ");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ConvDB", "onCancelled: " + error);

                }
            });
        }


        public static void listenChange(String id, String idlastMsg, OnGetValueListener onGetValueListener){
            String path = "/message/" + id;
            chatReference = FirebaseDatabase.getInstance().getReference(path);

            // Xử lý khi có thay đổi database
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("ConvDB", "onChildAdded1: " );
                    onGetValueListener.onSuccess(snapshot);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    onGetValueListener.onChange(snapshot);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            chatReference.orderByKey().startAfter(idlastMsg).addChildEventListener(childEventListener);
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
            msgData.put("typeMessage", typeMessage);
            msgData.put("listSeen", listSeen);
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

        public static void updateMessage(String idConv, String idMsg, Map<String, Object> msgMap){
            String path = "/message/" + idConv;
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference(path);

            databaseReference.child(idMsg).updateChildren(msgMap);
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
