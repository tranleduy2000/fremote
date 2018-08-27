package com.duy.fremote.server;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duy.fremote.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private final ArrayList<MessageItem> mMessageItems = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public MessageAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MessageItem.TYPE_IN:
                return new MessageHolder(
                        mLayoutInflater.inflate(R.layout.list_item_message_in, parent, false));
            case MessageItem.TYPE_ERROR:
                return new MessageHolder(
                        mLayoutInflater.inflate(R.layout.list_item_message_error, parent, false));
            default:
                return new MessageHolder(
                        mLayoutInflater.inflate(R.layout.list_item_message_out, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageItems.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.txtContent.setText(mMessageItems.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mMessageItems.size();
    }

    public void add(MessageItem message) {
        mMessageItems.add(message);
        notifyItemInserted(mMessageItems.size() - 1);
    }

    class MessageHolder extends RecyclerView.ViewHolder {

        private TextView txtContent;

        public MessageHolder(View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.txt_content);
        }
    }

}
