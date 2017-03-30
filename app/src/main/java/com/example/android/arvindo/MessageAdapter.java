package com.example.android.arvindo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Lenovo on 30-03-2017.
 */

public class MessageAdapter extends ArrayAdapter<MessageBody> {
    public MessageAdapter(Context context, int resource, List<MessageBody> object) {
        super(context, resource, object);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }

        ImageView messageImageView = (ImageView)convertView.findViewById(R.id.photoImageView);
        TextView  messageTextView  = (TextView)convertView.findViewById(R.id.mesgTextView);
        TextView  nameTextView=(TextView)convertView.findViewById(R.id.NameTextView);

        MessageBody currentMessage = getItem(position);

        boolean isPhoto = currentMessage.getPhotoUrl() != null;

        if(isPhoto){
            messageTextView.setVisibility(View.GONE);
            messageImageView.setVisibility(View.VISIBLE);
            Glide.with(messageImageView.getContext())
                    .load(currentMessage.getPhotoUrl())
                    .into(messageImageView);
        }

        else{
            messageImageView.setVisibility(View.GONE);
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(currentMessage.getText());
        }

        nameTextView.setText(currentMessage.getName());

        return convertView;
    }
}
