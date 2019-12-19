package com.notepad.namespace;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class NotepadActivity extends ListActivity implements OnScrollListener {
	/** Called when the activity is first created. */
	//用于表示当前界面是属于哪种状态
	public static final int CHECK_STATE = 0;
	public static final int EDIT_STATE = 1;
	public static final int ALERT_STATE = 2;


	private ListView listView;
	private ListViewAdapter adapter;// 数据源对象
	private View RecordView;///列表布局
	private View longClickView ;///长按弹出的布局
	private Button addRecordButton;//新增

	private Button deleteRecordButton;//删除
	private Button checkRecordButton;//查看
	private Button modifyRecordButton;//修改

	private DatabaseManage dm = null;// 数据库管理对象
	private Cursor cursor = null;

	private int id = -1;//被点击的条目

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		RecordView = getLayoutInflater()
				.inflate(R.layout.footer,null);//获取条目列表的布局
		longClickView = getLayoutInflater()
				.inflate(R.layout.long_click,null);

		//获取按钮对象
		addRecordButton = (Button)
				RecordView.findViewById(R.id.addRecordButton);
		deleteRecordButton = (Button)
				longClickView.findViewById(R.id.deleteRecordButton);
		checkRecordButton = (Button)
				longClickView.findViewById(R.id.checkRecordButton);
		modifyRecordButton = (Button)
				longClickView.findViewById(R.id.modifyRecordButton);

		dm = new DatabaseManage(this);//数据库操作对象


		listView = getListView();//获取id为list的对象

		listView.addFooterView(RecordView);//设置列表底部 视图

		initAdapter();//初始化

		//   this.startManagingCursor(cursor);//将cursor交给Activity管理

		setListAdapter(adapter);//自动为id为list的ListView设置适配器


		//设置滑动监听器
		listView.setOnScrollListener(this);
		listView.setOnCreateContextMenuListener(new myOnCreateContextMenuListener());


		//设置按钮监听器
		addRecordButton.setOnClickListener(new AddRecordListener());//新增
		deleteRecordButton.setOnClickListener(new DeleteRecordListener());//删除
		checkRecordButton.setOnClickListener(new CheckRecordListener());//查看
		modifyRecordButton.setOnClickListener(new ModifyRecordListener());//修改


	}

	//初始化数据源
	public void initAdapter(){

		dm.open();//打开数据库操作对象

		cursor = dm.selectAll();//获取所有数据

		cursor.moveToFirst();//将游标移动到第一条数据，使用前必须调用

		int count = cursor.getCount();//个数

		ArrayList<String> items = new ArrayList<String>();
		ArrayList<String> times = new ArrayList<String>();
		for(int i= 0; i < count; i++){
			items.add(cursor.getString(cursor.getColumnIndex("title")));
			times.add(cursor.getString(cursor.getColumnIndex("time")));
			cursor.moveToNext();//将游标指向下一个
		}
		// 	cursor.close();
		dm.close();//关闭数据操作对象
		adapter = new ListViewAdapter(this,items,times);//创建数据源
	}


	@Override
	protected void onDestroy() {//销毁Activity之前，所做的事
		// TODO Auto-generated method stub
		cursor.close();//关闭游标
		super.onDestroy();
	}

	//滑动事件
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}



	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	//---------------------------------------------------------------

	//长按
	public class myOnCreateContextMenuListener implements OnCreateContextMenuListener{

		public void onCreateContextMenu(ContextMenu menu, View v,
										ContextMenuInfo menuInfo) {
			// TODO Auto-generated method stub
			final AdapterView.AdapterContextMenuInfo info =
					(AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle("");
			//设置选项
			menu.add(0,0,0,"删除");
			menu.add(0,1,0,"修改");
			menu.add(0,2,0,"查看");
		}

	}



	//响应长按弹出菜单的点击事件
	public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo menuInfo =
				(AdapterView.AdapterContextMenuInfo)item
						.getMenuInfo();
		//	HashMap<String, Object> map = List.get(menuInfo.position);
		//	Log.v("show", "shibai");
		dm.open();
		//	cursor = dm.selectAll();
		switch(item.getItemId()){
			case 0://删除
				try{
					cursor.moveToPosition(menuInfo.position);

					int i = dm.delete(Long.parseLong(cursor.getString(cursor.getColumnIndex("_id"))));//删除数据

					adapter.removeListItem(menuInfo.position);//删除数据
					adapter.notifyDataSetChanged();//通知数据源，数据已经改变，刷新界面

					//	Log.v("show", "chenggong1" + i);
				}catch(Exception ex){
					ex.printStackTrace();
				}

				break;
			case 1://修改
				//	Log.v("show", "chenggong2");
				try{
					cursor.moveToPosition(menuInfo.position);

					//用于Activity之间的通讯
					Intent intent = new Intent();
					//通讯时的数据传送
					intent.putExtra("id", cursor.getString(cursor.getColumnIndex("_id")));
					intent.putExtra("state", ALERT_STATE);
					intent.putExtra("title", cursor.getString(cursor.getColumnIndex("title")));
					intent.putExtra("time", cursor.getString(cursor.getColumnIndex("time")));
					intent.putExtra("content", cursor.getString(cursor.getColumnIndex("content")));
					//设置并启动另一个指定的Activity
					intent.setClass(NotepadActivity.this, NotepadEditActivity.class);
					NotepadActivity.this.startActivity(intent);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			case 2://查看
				//	Log.v("show", "chenggong3");
				try{
					cursor.moveToPosition(menuInfo.position);

					Intent intent = new Intent();

					intent.putExtra("id", cursor.getString(cursor.getColumnIndex("_id")));
					intent.putExtra("title", cursor.getString(cursor.getColumnIndex("title")));
					intent.putExtra("time", cursor.getString(cursor.getColumnIndex("time")));
					intent.putExtra("content", cursor.getString(cursor.getColumnIndex("content")));

					intent.setClass(NotepadActivity.this, NotepadCheckActivity.class);
					NotepadActivity.this.startActivity(intent);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				break;
			default:;
		}
		//	cursor.close();
		dm.close();
		return super.onContextItemSelected(item);

	}


	//短按，即点击
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

//		Log.v("position", position+"");
//		Log.v("id", id+"");

		cursor.moveToPosition(position);

		Intent intent = new Intent();

		intent.putExtra("state", CHECK_STATE);
		intent.putExtra("id", cursor.getString(cursor.getColumnIndex("_id")));
		intent.putExtra("title", cursor.getString(cursor.getColumnIndex("title")));
		intent.putExtra("time", cursor.getString(cursor.getColumnIndex("time")));
		intent.putExtra("content", cursor.getString(cursor.getColumnIndex("content")));

		//	cursor.close();
		dm.close();

		intent.setClass(NotepadActivity.this, NotepadCheckActivity.class);
		NotepadActivity.this.startActivity(intent);

	}

	//新建
	public class AddRecordListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("state", EDIT_STATE);
			intent.setClass(NotepadActivity.this,NotepadEditActivity.class);
			NotepadActivity.this.startActivity(intent);
		}

	}


	//------------------------------------------------------------------------

	public class DeleteRecordListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}
	public class CheckRecordListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}
	public class ModifyRecordListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}
}