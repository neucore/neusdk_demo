package com.neucore.neusdk_demo.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.neucore.neusdk_demo.R;
import com.neucore.neusdk_demo.db.bean.Record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecordAdapter extends BaseAdapter {
	protected static final String TAG = "ProjectAdapter";
	private Context mContext;
	private String type="";
	private List<Record> lists;
	private String keyword;
	private int type_search;//搜索类型 0-项目 1-建设单位 2-地址
	public RecordAdapter(Context context ) {
		this.mContext = context;
	}
	public void setLists(List<Record> lists) {
		this.lists = lists;
		notifyDataSetChanged();
	}
	public void setType(String type){
		this.type=type;
	}
	public void setKeyword(String keyword, int type_search){
		this.keyword=keyword;
		this.type_search=type_search;
	}
	@Override
	public int getCount() {
		if (lists != null) {
			return lists.size();
		}
		return 0;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		Record hm = lists.get(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.record_project, null);
			holder = new Holder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_userid = (TextView) convertView.findViewById(R.id.tv_userid);
			holder.tv_yanzheng = (TextView) convertView.findViewById(R.id.tv_yanzheng);
			holder.tv_up = (TextView) convertView.findViewById(R.id.tv_up);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_name.setText(hm.getName());//"processinstName"
		holder.tv_userid.setText(hm.getUserId());//"yysxqf"
		holder.tv_yanzheng.setText(chargeSecondsToNowTime(hm.getTime()));//地址
//		holder.tv_yanzheng.setText(hm.getTime()+"");//地址
		if(hm.getIsUp()==1) {
			holder.tv_up.setText("已上传");//地址
		}else{
			holder.tv_up.setText("未上传");//地址
		}
		return convertView;
	}
	public static String chargeSecondsToNowTime(long seconds) {
//        long time =seconds*1000-8*3600*1000;
//        long time = Long.parseLong(seconds)*1000-8*3600*1000;
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return format2.format(new Date(seconds));
    }  
	public List<Record> getList() {
		return lists;
	}

	class Holder {
		TextView tv_name;
		TextView tv_userid;
		TextView tv_yanzheng;
		TextView tv_up;
	}
	private int	clickTemp = -1;
		public void setSelection(int position){
			clickTemp = position;
		}
		private void disPlayKeyword(TextView tv, String str){
			if (str.contains(keyword)) {
				int index = str.indexOf(keyword);
				int len = keyword.length();
				Spanned temp;
				temp = Html.fromHtml(str.substring(0, index)
						+ "<font color=#FF0000>"
						+ str.substring(index, index + len) + "</font>"
						+ str.substring(index + len, str.length()));
				tv.setText(temp);
			} else {
				tv.setText(str);
			}
		}
}
