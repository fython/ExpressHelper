package info.papdt.express.helper.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.papdt.express.helper.R;

public class DrawerListItemAdapter extends BaseAdapter {

	private ArrayList<Item> data;
	private LayoutInflater mInflater;
	private int normalColor, highlightColor;

	private int selectedPosition;

	public DrawerListItemAdapter(Context context, ArrayList<Item> data,
	                             @ColorRes int normalColorId, @ColorRes int highlightColorId) {
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.normalColor = context.getResources().getColor(normalColorId);
		this.highlightColor = context.getResources().getColor(highlightColorId);
		this.selectedPosition = 0;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Item getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.drawer_list_item, parent, false);

			holder = new ViewHolder();
			holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/** 设置文本标题和图标 */
		holder.tv_title.setText(getItem(position).title);
		try {
			holder.iv_icon.setImageResource(getItem(position).iconId);
		} catch (Exception e) {
			holder.iv_icon.setImageResource(R.drawable.ic_assignment_black_24dp);
		}

		/** 设置未选定与高亮项目色彩 */
		if (position != selectedPosition) {
			holder.tv_title.setTextColor(normalColor);
			try {
				holder.iv_icon.setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
			} catch (Exception e) {

			}
		} else {
			holder.tv_title.setTextColor(highlightColor);
			try {
				holder.iv_icon.setColorFilter(highlightColor, PorterDuff.Mode.MULTIPLY);
			} catch (Exception e) {

			}
		}

		return convertView;
	}

	private class ViewHolder {

		public ImageView iv_icon;
		public TextView tv_title;

	}

	public static class Item {

		public int iconId;
		public String title;

		public Item(String title, @DrawableRes int iconId) {
			this.title = title;
			this.iconId = iconId;
		}

	}

}
