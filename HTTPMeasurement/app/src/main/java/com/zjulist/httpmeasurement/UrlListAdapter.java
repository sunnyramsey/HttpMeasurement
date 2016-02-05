package com.zjulist.httpmeasurement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dell on 2016/1/30.
 */
public class UrlListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<UrlState> dataList;

    public UrlListAdapter(Context context,List<UrlState> srcList)
    {
        mInflater = LayoutInflater.from(context);
        this.dataList = srcList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.urlText = (TextView) convertView.findViewById(R.id.urlText);
            holder.urlFinishCheck = (CheckBox) convertView.findViewById(R.id.finishCheck);

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        holder.urlFinishCheck.setEnabled(false);
        holder.urlText.setText(dataList.get(position).getUrl());
        holder.urlFinishCheck.setChecked(dataList.get(position).isFinished());

        return convertView;
    }

    public List<UrlState> getDataList() {
        return dataList;
    }

    public void setDataList(List<UrlState> dataList) {
        this.dataList = dataList;
    }

    static class ViewHolder{
        TextView urlText;
        CheckBox urlFinishCheck;
    }
}
