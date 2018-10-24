package com.sawatruck.driver.view.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Message;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.view.adapter.MessageAdapter;
import com.sawatruck.driver.view.design.xlistview.XListView;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ActivityMessage extends BaseActivity implements XListView.IXListViewListener {
    @Bind(R.id.listview) XListView xListView;
    @Bind(R.id.btn_send_message) View btnSendMessage;
    @Bind(R.id.edit_messages) EditText editMessage;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages = new ArrayList<>();
    private int currentPage = 0;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_message, null);

        ButterKnife.bind(this, view);
        initView();

        xListView.setPullLoadEnable(false);
        xListView.setFootText("");
        xListView.setXListViewListener(this);

        registerReceiver(sendMessageReceiver, new IntentFilter("onSendMessage"));
        registerReceiver(receiveMessageReceiver, new IntentFilter("onReceiveMessage"));


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            getMessages(0);
            loadingProgress.setVisibility(View.VISIBLE);
            loadingProgress.startSpinning();

        } catch (Exception e) {
            e.printStackTrace();
        }

        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                xListView.requestFocus();
                xListView.smoothScrollToPosition(messageAdapter.getCount());
                xListView.setSelection(messageAdapter.getCount());
            }
        };
        IntentFilter intFilt = new IntentFilter(Constant.BROADCAST_ACTION);
        registerReceiver(br, intFilt);

        return view;
    }

    private void initView() {
        messageAdapter = new MessageAdapter(this);
        xListView.setAdapter(messageAdapter);
    }

    @Override
    public void onRefresh() {
        Logger.error("onrefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    Logger.error("onrefresh:" + currentPage);
                    getMessages(currentPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                xListView.stopRefresh(false);
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        Logger.error("onLoadMore");
    }

    private final BroadcastReceiver sendMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strMessage = intent.getStringExtra("message");
            Message message = BaseApplication.getGson().fromJson(strMessage, Message.class);
            messages.add(message);
            messageAdapter.setMessages(messages);
            messageAdapter.notifyDataSetChanged();
            xListView.smoothScrollToPosition(messageAdapter.getCount());
            xListView.setSelection(messageAdapter.getCount());
        }
    };

    private final BroadcastReceiver receiveMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strMessage = intent.getStringExtra("message");
            Message message = BaseApplication.getGson().fromJson(strMessage, Message.class);
            if(!message.isMeSender())
                messages.add(message);
            messageAdapter.setMessages(messages);
            messageAdapter.notifyDataSetChanged();
            xListView.smoothScrollToPosition(messageAdapter.getCount());
            xListView.setSelection(messageAdapter.getCount());
        }
    };

    private class GetMessageAsyncTask extends AsyncTask<Integer, Void, Void> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Integer... params) {
            int page = params[0];
            String userId = getIntent().getStringExtra(Constant.INTENT_USER_ID);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("UserID", userId);
                jsonObject.put("Page", page);
                json = jsonObject.toString();
                Logger.error(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpUtil.postBody(Constant.GET_MESSAGE_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(String responseBody) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        ArrayList<Message> allPageMessages = new ArrayList<>();
                        ArrayList<Message> newPageMessages = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Message message = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Message.class);
                            newPageMessages.add(message);

                        }

                        allPageMessages.addAll(newPageMessages);
                        allPageMessages.addAll(messages);


                        messages = allPageMessages;

                        if (jsonArray.length() != 0)
                            currentPage++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(Message message:messages)
                                    if(!message.isMeSender())
                                        setAppTitle(message.getSenderFullName());

                                messageAdapter.setMessages(messages);
                                messageAdapter.notifyDataSetChanged();
                                if (currentPage == 1) {
                                    xListView.smoothScrollToPosition(messageAdapter.getCount());
                                    xListView.setSelection(messageAdapter.getCount());
                                }
                                loadingProgress.setVisibility(View.GONE);
                                loadingProgress.stopSpinning();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorString) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Notice.show(getString(R.string.error_get_messages));
                            loadingProgress.setVisibility(View.GONE);
                            loadingProgress.stopSpinning();
                        }
                    });
                }
            });
            return null;
        }
    }


    private void getMessages(int page) {
        GetMessageAsyncTask getMessageAsyncTask = new GetMessageAsyncTask();
        getMessageAsyncTask.execute(page);
    }

    @Override
    public void onResume() {
        super.onResume();
        showNavHome(false);
        String userName = getIntent().getStringExtra(Constant.INTENT_USERNAME);
        setAppTitle(userName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {
        SendMessageAsyncTask sendMessageAsyncTask = new SendMessageAsyncTask();
        sendMessageAsyncTask.execute(editMessage.getText().toString());
        editMessage.setText("");
        hideKeyboard();
    }

    private class SendMessageAsyncTask extends AsyncTask<String, Void, Void> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String strMessage = params[0];
            String userId = getIntent().getStringExtra(Constant.INTENT_USER_ID);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ReceiverID", userId);
                jsonObject.put("Message", strMessage);
                json = jsonObject.toString();
                Logger.error(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpUtil.postBody(Constant.SEND_MESSAGE_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(String responseBody) {
                }

                @Override
                public void onFailure(String errorString) {
                }
            });
            return null;
        }
    }

}
