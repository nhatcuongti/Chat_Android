package com.example.meza.interfaces;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager linearLayoutManager;

    public PaginationScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int firstVisibileItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

        if (isLoading() || isLastPage())
            return;

        if (firstVisibileItemPosition == 0){
            loadMoreItems();
        }
    }

    public abstract void loadMoreItems();
    public abstract boolean isLoading();
    public abstract boolean isLastPage();
}
