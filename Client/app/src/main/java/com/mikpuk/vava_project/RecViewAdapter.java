package com.mikpuk.vava_project;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;

public class RecViewAdapter extends RecyclerView.Adapter<BaseViewHolder>  {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private OnItemListener onItemListener;

    private List<Item> mPostItems;
    private Context context;

    public RecViewAdapter(ArrayList<Item> postItems, Context context, OnItemListener onItemListener) {
        this.mPostItems = postItems;
        this.context = context;
        this.onItemListener = onItemListener;
    }
    @NonNull

    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false), onItemListener);
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_items, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mPostItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mPostItems == null ? 0 : mPostItems.size();
    }

    public void addItems(List<Item> items) {
        mPostItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        mPostItems.add(new Item());
        notifyItemInserted(mPostItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mPostItems.size() - 1;
        Item item = getItem(position);
        if (item != null) {
            mPostItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mPostItems.clear();
        notifyDataSetChanged();
    }

    Item getItem(int position) {
        return mPostItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder implements View.OnClickListener {
        @BindView(R.id.textTitle)
        TextView textViewTitle;
        @BindView(R.id.textDescriptionCard)
        TextView textViewAddress;
        OnItemListener onItemListener;

        ViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {

        }

    public void onBind(int position) {
        super.onBind(position);
        Item item = mPostItems.get(position);

        textViewTitle.setText(item.getName());
        AppLocationManager appLocationManager = new AppLocationManager(context);
        textViewAddress.setText(appLocationManager.generateAddress(item.getLatitude(), item.getLongtitude()));
    }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }

    public interface OnItemListener{
        void onItemClick(int posistion);
    }
}

