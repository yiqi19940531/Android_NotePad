package com.notepad.namespace;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {


	private List<String> listItems;
	private List<String> listItemTimes;
	//private HashMap<String,String> listItems;

	private LayoutInflater inflater;

	public ListViewAdapter(Context context, List<String> listItems, List<String> times){
		this.listItems = listItems;
		this.listItemTimes = times;
		inflater = (LayoutInflater)
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	/**
	 * 往列表添加条目
	 * @param item
	 */
	public void addListItem(String item, String time){
		listItems.add(item);
		listItemTimes.add(time);

	}

	/**
	 * 删除指定位置的数据
	 * @param position
	 */
	public void removeListItem(int position){
		listItems.remove(position);
		listItemTimes.remove(position);
	}


	/**
	 * 获取列表的数量
	 */
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	/**
	 * 根据索引获取列表对应索引的内容
	 */
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * 通过该函数显示数据
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if(convertView == null){
			convertView = inflater.inflate(R.layout.notepad_list_item,null);
		}

		TextView text = (TextView)convertView.findViewById(R.id.listItem);
		text.setText(listItems.get(position));

		TextView time = (TextView)convertView.findViewById(R.id.listItemTime);
		String datetime = DateFormat.format("yyyy-MM-dd kk:mm:ss",
				Long.parseLong(listItemTimes.get(position))).toString();
		time.setText(datetime);

		return convertView;
	}

}
