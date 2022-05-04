package com.example.meza.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.meza.interfaces.OnGetObjectListener;
import com.example.meza.interfaces.OnGetValueListener;
import com.example.meza.model.ConversationModel;
import com.example.meza.utilities.Constants;
import com.example.meza.utilities.PreferenceManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageDatabase {
    private static MessageDatabase messageDatabase = null;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    Context context;

    private MessageDatabase(Context context){
        this.context = context;
    }

    public static MessageDatabase getInstance(Context context){
        if (messageDatabase == null)
            messageDatabase = new MessageDatabase(context);

        return messageDatabase;
    }

    public void updateMessage(ConversationModel conversation, String userID, String text, String typeMessage){
        PreferenceManager preferenceManager = new PreferenceManager(context);
        //********************************************************************************//
        //Khởi tạo Message mà người dùng vừa gửi//
        ConversationModel.Message msg = new ConversationModel.Message();
        msg.setTimestamp(System.currentTimeMillis());
        msg.setSender(userID);
        msg.setText(text);
        msg.setTypeMessage(typeMessage);
        //*********************************End********************************************//

        ArrayList<ConversationModel.Message> list_msg = conversation.getListMessage();
        String id = (list_msg == null || list_msg.isEmpty()) ? "0" : list_msg.get(list_msg.size() - 1).getId() ;
        id = String.valueOf(Integer.valueOf(id) + 1);
        //*********************************End************************************//

        msg.setId(id);
        ConversationModel.Message.insertNewMsgToDatabase(msg, id, conversation.getID());

        // Cập nhật conversation
        conversation.setLast_message("Hình ảnh vừa được gửi");
        conversation.setLast_time(msg.getTimestamp());
        ConversationModel.updateConversation(conversation.getID(), conversation.toMap());
    }

    public void deleteMessage(ConversationModel conversationModel, int positionSelectedItem, OnGetObjectListener onGetObjectListener){
        String path = "/message/" + conversationModel.getID();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        String idMessage = conversationModel.getListMessage().get(positionSelectedItem).getId();

        databaseReference.child(idMessage).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                onGetObjectListener.onSuccess(null);
            }
        });
    }

    public void listenMessageRemove(String idConv, OnGetValueListener onGetValueListener){
        String path = "/message/" + idConv;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        // Xử lý khi có thay đổi database
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ConversationModel.Message msg = snapshot.getValue(ConversationModel.Message.class);
                msg.formatStartTime();

                onGetValueListener.onRemove(msg);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
