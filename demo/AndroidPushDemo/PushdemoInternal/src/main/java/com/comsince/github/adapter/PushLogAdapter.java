package com.comsince.github.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comsince.github.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaojinlong on 19-5-23.
 */

public class PushLogAdapter extends BaseAdapter {

    private Context context;
    private List<String> pushLogList;
    private LayoutInflater inflater;


    public PushLogAdapter(Context context){
        this(context,new ArrayList<String>());
    }

    public PushLogAdapter(Context context, List<String> pushLogList) {
        this.context = context;
        this.pushLogList = pushLogList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addlog(String log){
        if(pushLogList.size()>50){
            pushLogList.clear();
        }
        pushLogList.add(0,log);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pushLogList.size();
    }

    @Override
    public String getItem(int position) {
        return pushLogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.push_log_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TextView pushLogText = viewHolder.convertView.findViewById(R.id.push_log_text);
        pushLogText.setText(getItem(position));

        return convertView;
    }


    public class ViewHolder{
        private View convertView;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
        }
    }
}
