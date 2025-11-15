package com.ujjay.wanderly.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ujjay.wanderly.R;
import com.ujjay.wanderly.models.Message;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<Message> messageList;
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position, Message message);
    }

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);

        // Set long click listener only for AI messages
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null && !message.isUser()) {
                    return longClickListener.onItemLongClick(position, message);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    public void setMessages(List<Message> messages) {
        this.messageList = messages;
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private LinearLayout messageBubble;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageBubble = itemView.findViewById(R.id.message_bubble);
        }

        public void bind(Message message) {
            messageText.setText(message.getText());

            if (message.isUser()) {
                // User message - align right with different background
                messageBubble.setBackgroundResource(R.drawable.bubble_user);
                ((LinearLayout.LayoutParams) messageBubble.getLayoutParams()).gravity = android.view.Gravity.END;
            } else {
                // AI message - align left with different background
                messageBubble.setBackgroundResource(R.drawable.bubble_ai);
                ((LinearLayout.LayoutParams) messageBubble.getLayoutParams()).gravity = android.view.Gravity.START;
            }
        }
    }
}