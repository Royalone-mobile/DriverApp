package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Message;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.view.design.CircularImage;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 9/20/2017.
 */

public class MessageAdapter extends BaseAdapter {
    private ArrayList<Message> messages = new ArrayList<>();
    private Context context;

    @Bind(R.id.img_leftUser) CircularImage imgLeftUser;
    @Bind(R.id.img_rightUser) CircularImage imgRightUser;
    @Bind(R.id.txt_left_timeago) CustomTextView txtLeftTimeAgo;
    @Bind(R.id.txt_left_message) CustomTextView txtLeftMessage;
    @Bind(R.id.txt_right_timeago) CustomTextView txtRightTimeAgo;
    @Bind(R.id.txt_right_message) CustomTextView txtRightMessage;

    @Bind(R.id.img_leftImg) ImageView imgLeftImage;
    @Bind(R.id.img_rightImg) ImageView imgRightImage;
    @Bind(R.id.txt_left_user_name) CustomTextView txtLeftUserName;
    @Bind(R.id.txt_right_user_name) CustomTextView txtRightUserName;


    public MessageAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_item, parent, false);
        } else {
            view = convertView;
        }
        ButterKnife.bind(this,view);
        Message message = messages.get(position);
        if(message.isMeSender()){
            txtRightUserName.setText(message.getSenderFullName());
            txtRightMessage.setText(Html.fromHtml(message.getText()));
            txtRightTimeAgo.setText(Misc.getSpanFromDate(context, Misc.getDateFromString(message.getDate())));
            try {
                BaseApplication.getPicasso().load(message.getSenderImageURL()).placeholder(R.drawable.ico_user).fit().into(imgRightUser);
            }catch (Exception e){
                e.printStackTrace();
            }
            txtRightUserName.setVisibility(View.VISIBLE);
            txtRightMessage.setVisibility(View.VISIBLE);
            txtRightTimeAgo.setVisibility(View.VISIBLE);
            imgRightUser.setVisibility(View.VISIBLE);

            txtLeftMessage.setVisibility(View.GONE);
            txtLeftTimeAgo.setVisibility(View.GONE);
            txtLeftUserName.setVisibility(View.GONE);
            imgLeftUser.setVisibility(View.GONE);
        }

        else{
            txtLeftMessage.setText(Html.fromHtml(message.getText()));
            txtLeftTimeAgo.setText(Misc.getSpanFromDate(context, Misc.getDateFromString(message.getDate())));
            txtLeftUserName.setText(message.getSenderFullName());
            try {
                BaseApplication.getPicasso().load(message.getSenderImageURL()).placeholder(R.drawable.ico_user).fit().into(imgLeftUser);
            }catch (Exception e){
                e.printStackTrace();
            }

            txtLeftTimeAgo.setVisibility(View.VISIBLE);
            txtLeftUserName.setVisibility(View.VISIBLE);
            imgLeftUser.setVisibility(View.VISIBLE);
            txtLeftMessage.setVisibility(View.VISIBLE);

            txtRightUserName.setVisibility(View.GONE);
            txtRightMessage.setVisibility(View.GONE);
            txtRightTimeAgo.setVisibility(View.GONE);
            imgRightUser.setVisibility(View.GONE);
        }
        return view;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void initializeAdapter() {
        this.messages = new ArrayList<>();
        notifyDataSetChanged();
    }

}
