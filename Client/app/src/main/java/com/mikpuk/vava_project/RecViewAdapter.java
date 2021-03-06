package com.mikpuk.vava_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hypertrack.hyperlog.HyperLog;
import com.mikpuk.vava_project.data.Item;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic recycle view that stores every request for user to see
 */
public class RecViewAdapter extends RecyclerView.Adapter<BaseViewHolder>  {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private OnItemListener onItemListener;

    private List<Item> mPostItems;
    private Context context;
    private static final String TAG = "Recycle view adapter";


    public RecViewAdapter(ArrayList<Item> postItems, Context context, OnItemListener onItemListener) {
        this.mPostItems = postItems;
        this.context = context;
        this.onItemListener = onItemListener;
    }
    @NonNull

    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HyperLog.i(TAG,"Creating hyper log");
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
        if(position < 0) {
            return;
        }
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

    public Item getItem(int position) {
        if(mPostItems.size() < position-1 || position < 0)
            return null;
        else
            return mPostItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder implements View.OnClickListener {
        @BindView(R.id.textTitle)
        TextView textViewTitle;
        @BindView(R.id.textDescriptionCard)
        TextView textViewAddress;
        @BindView(R.id.myReqDistance)
        TextView textDistance;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.acceptImage)
        ImageView acceptedImage;
        BackGrounPicker backGrounPicker = new BackGrounPicker();

        OnItemListener onItemListener;

        ViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
            acceptedImage.setVisibility(View.INVISIBLE);
        }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    public void onBind(int position) {
        super.onBind(position);
        Item item = mPostItems.get(position);

        textViewTitle.setText(item.getName());
        imageView.setImageResource((int)item.getType_id());
        AppLocationManager appLocationManager = new AppLocationManager(context);
        textViewAddress.setText(appLocationManager.generateAddress(item.getLatitude(), item.getLongtitude()));
        if (item.isAccepted())
            acceptedImage.setVisibility(View.VISIBLE);

        if(item.getDistance() < 0) {
            textDistance.setText("???");
        }else {
            textDistance.setText(new DecimalFormat("0.00").format(item.getDistance()) + "km");
        }
        backGrounPicker.randomBackground(textDistance);
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

