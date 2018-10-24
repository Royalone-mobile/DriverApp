package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Inbox;
import com.sawatruck.driver.utils.ActivityUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.view.activity.ActivityMessage;
import com.sawatruck.driver.view.design.AutoResizeTextView;
import com.sawatruck.driver.view.design.CircularImage;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by royal on 8/20/2017.
 */

public class InboxAdapter extends BaseAdapter {
    private ArrayList<Inbox> inboxList = new ArrayList<>();
    private Context context;

    @Bind(R.id.txt_username) CustomTextView txtUserName;
    @Bind(R.id.txt_last_mesage) AutoResizeTextView txtLastMessage;
    @Bind(R.id.txt_last_message_time) CustomTextView txtLastMessageTime;
    @Bind(R.id.iv_user_photo) CircularImage imgAvatar;
    @Bind(R.id.img_online) CircularImage imgOnline;

    public InboxAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return inboxList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            view = inflater.inflate(R.layout.inbox_list_item, parent, false);
        } else {
            view = convertView;
        }
        ButterKnife.bind(this,view);


        Inbox inbox = inboxList.get(position);

        try {
            BaseApplication.getPicasso().load(inbox.getUserImageUrl()).placeholder(R.drawable.ico_truck).into(imgAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }
        txtUserName.setText(inbox.getUserFullName());
        txtLastMessage.setText(inbox.getLastMessage());
        txtLastMessageTime.setText(Misc.getTimeStringFromDate(inbox.getLastMessageDate()));
        if(inbox.isOnline())
            imgOnline.setImageResource(R.drawable.drawable_online);
        else
            imgOnline.setImageResource(R.drawable.drawable_offline);

        view.setTag(position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int)v.getTag();
                Inbox inbox = inboxList.get(pos);
                Intent intent = new Intent(context, ActivityMessage.class);
                intent.putExtra(Constant.INTENT_USER_ID, inbox.getUserID());
                intent.putExtra(Constant.INTENT_USERNAME, inbox.getUserFullName());
                ActivityUtil.goOtherActivityFlipTransition(context, intent);
            }
        });
        return view;
    }

    public void initializeAdapter() {
        this.inboxList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<Inbox> getInboxList() {
        return inboxList;
    }

    public void setInboxList(ArrayList<Inbox> inboxList) {
        this.inboxList = inboxList;
    }
}
