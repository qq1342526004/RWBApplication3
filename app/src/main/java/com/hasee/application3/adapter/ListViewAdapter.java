package com.hasee.application3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hasee.application3.R;
import com.hasee.application3.bean.GoodInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    private List<GoodInfo> lists;//总数据
    private final Object mLock = new Object();
    private Context mContext;
    private List<GoodInfo> backupList;//备份的数据
    private MyFilter myFilter;

    public ListViewAdapter(Context mContext, List<GoodInfo> lists){
        this.mContext = mContext;
        this.lists = lists;
    }

    public MyFilter getMyFilter() {
        if(myFilter == null){
            myFilter = new MyFilter();
        }
        return myFilter;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.listview_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.itemLayout = (LinearLayout)view.findViewById(R.id.item_layout);
            viewHolder.vsnCodeTv = (TextView) view.findViewById(R.id.list_item_tv1);
            viewHolder.spsRouteTv = (TextView) view.findViewById(R.id.list_item_tv2);
            viewHolder.spsPSJIDTv = (TextView) view.findViewById(R.id.list_item_tv3);
            viewHolder.checkBox = (CheckBox)view.findViewById(R.id.list_item_checkbox);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(position%2 !=0){
            viewHolder.itemLayout.setBackgroundResource(R.color.oddNumber_bg);
        }else{
            viewHolder.itemLayout.setBackgroundResource(R.color.evenNumber_bg);
        }
        viewHolder.vsnCodeTv.setText(lists.get(position).getVsnCode());
        viewHolder.spsRouteTv.setText(lists.get(position).getSpsRoute());
        viewHolder.spsPSJIDTv.setText(lists.get(position).getSpsPSJID());
        if(lists.get(position).isFlag()){
            viewHolder.checkBox.setChecked(true);
        }else {
            viewHolder.checkBox.setChecked(false);
        }
        return view;
    }

    public class ViewHolder{
        LinearLayout itemLayout;
        TextView vsnCodeTv;
        TextView spsRouteTv;
        TextView spsPSJIDTv;
        public CheckBox checkBox;
    }


    /**
     * 过滤器
     */
    public class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(backupList == null){
                synchronized (mLock){
                    backupList = new ArrayList<>(lists);
                }
            }
            if(constraint == null || constraint.length()==0){//如果为空
                final List<GoodInfo> list;
                synchronized (mLock){
                    list = new ArrayList<>(backupList);
                }
                results.values = list;
                results.count = list.size();
            }else{//不为空
                final String filterString = constraint.toString().toLowerCase();
                final List<GoodInfo> values;
                synchronized (mLock){
                    values = new ArrayList<>(backupList);
                }
                final List<GoodInfo> newValues = new ArrayList<>();
                Iterator<GoodInfo> iterator = values.iterator();
                while (iterator.hasNext()){
                    GoodInfo goodInfo = iterator.next();
                    if(goodInfo.getVsnCode().contains(filterString)){
                        newValues.add(goodInfo);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            lists = (List<GoodInfo>) results.values;
            if(results.count > 0){//通知数据发生了改变
                notifyDataSetChanged();
            }else{//通知数据失效
                notifyDataSetInvalidated();
            }
        }
    }

}
