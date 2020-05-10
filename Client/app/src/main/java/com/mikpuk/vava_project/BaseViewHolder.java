package com.mikpuk.vava_project;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class that is part of recycle view
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    private int mCurrentPosition;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract void clear();

    public void onBind(int position) {
        mCurrentPosition = position;
        clear();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}