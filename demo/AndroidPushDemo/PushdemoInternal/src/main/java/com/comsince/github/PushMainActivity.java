package com.comsince.github;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.comsince.github.adapter.PushLogAdapter;
import com.comsince.github.service.GroupService;
import com.meizu.cloud.pushinternal.DebugLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PushMainActivity extends Activity implements View.OnClickListener{

    private String TAG = "PushMainActivity";

    private EditText groupEt;
    private EditText sendMessageEt;
    private Button joinGroupBt;
    private Button sendAllBt;
    private Button sendPrivateBt;
    private ListView pushLogListView;

    private PushLogAdapter pushLogAdapter;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        pushLogListView = findViewById(R.id.push_log_list);
        PushDemoApplication.setPushLogActivity(this);

        initView();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PushMainActivity.this,PushService.class);
                intent.setAction(PushService.START_PUSH_SERVICE);
                startService(intent);
            }
        });

    }

    private void initView(){
        groupEt = findViewById(R.id.push_group);
        sendMessageEt = findViewById(R.id.send_message);
        joinGroupBt = findViewById(R.id.join_group);
        sendAllBt = findViewById(R.id.send_all);
        sendPrivateBt = findViewById(R.id.send_private);

        joinGroupBt.setOnClickListener(this);
        sendAllBt.setOnClickListener(this);
        sendPrivateBt.setOnClickListener(this);
        DebugLogger.i("pushMain","init view ");
        pushLogAdapter = new PushLogAdapter(this,PushDemoApplication.pushLoglist);
        pushLogListView.setAdapter(pushLogAdapter);
    }


    @Override
    protected void onResume() {
        if(groupEt != null){
            groupEt.setText("test");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void refreshLogInfo(String item) {
        pushLogAdapter.addlog(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PushDemoApplication.setPushLogActivity(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_all:
                if(TextUtils.isEmpty(groupEt.getText().toString())){
                    Toast.makeText(this,"羣組不能爲空",Toast.LENGTH_SHORT).show();
                    break;
                }
                sendAll(groupEt.getText().toString(),sendMessageEt.getText().toString());
                sendMessageEt.setText("");
                break;
            case R.id.join_group:
                joinGroup(groupEt.getText().toString());
                break;
            case R.id.send_private:

            default:
                break;
        }
    }

    private void joinGroup(String group){
        Intent intent = new Intent(this,PushService.class);
        intent.setAction(GroupService.ACTION_GROUP_JOIN_GROUP);
        intent.putExtra(GroupService.GROUP_NAME,group);
        startService(intent);
    }

    private void sendPrivate(String group){
        Intent intent = new Intent(this,PushService.class);
        intent.setAction(GroupService.ACTION_GROUP_JOIN_GROUP);
        intent.putExtra(GroupService.GROUP_NAME,group);
        startService(intent);
    }

    private void sendAll(String group,String message){
        Intent intent = new Intent(this,PushService.class);
        intent.setAction(GroupService.ACTION_SEND_PUBLIC_MESSAGE);
        intent.putExtra(GroupService.GROUP_NAME,group);
        intent.putExtra(GroupService.GROUP_MESSAGE,message);
        startService(intent);
    }


}
