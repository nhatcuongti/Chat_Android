package com.example.meza.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meza.databinding.ItemContainerUserBinding;
import com.example.meza.interfaces.UserListener;
import com.example.meza.model.User;
import com.example.meza.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<User> users;
    private final ArrayList<User> backUpUsersList;
    private final UserListener userListener;

    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
        backUpUsersList = new ArrayList<>();
        backUpUsersList.addAll(users);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user) {
            binding.textUsername.setText(user.fullname);
            binding.textPhone.setText(user.phone_number);
            binding.imageProfile.setImageBitmap(Utils.decodeImage(user.image));
            binding.addFriendRequest.setOnClickListener(v -> {
                userListener.onAddUserClicked(user);
                requesting(true);
            });
            binding.removeFriendRequest.setOnClickListener(v -> {
                userListener.onRemoveUserClicked(user);
                requesting(false);
            });
        }

        private void requesting(boolean isRequesting) {
            if (isRequesting) {
                binding.addFriendRequest.setVisibility(View.INVISIBLE);
                binding.removeFriendRequest.setVisibility(View.VISIBLE);
            } else {
                binding.addFriendRequest.setVisibility(View.VISIBLE);
                binding.removeFriendRequest.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        users.clear();
        if (charText.length() == 0) {
            users.addAll(backUpUsersList);
        } else {
            for (User user : backUpUsersList) {
                if (user.phone_number.toLowerCase(Locale.getDefault()).contains(charText)) {
                    users.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }
}
