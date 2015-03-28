package info.papdt.express.helper.ui.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ClickableViewHolder> {

	public interface OnItemClickListener {
		public void onItemClicked(int position);
	}

	public interface OnItemLongClickListener {
		public boolean onItemLongClicked(int position);
	}

	private OnItemClickListener itemClickListener;
	private OnItemLongClickListener itemLongClickListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.itemClickListener = listener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		this.itemLongClickListener = listener;
	}

	@Override
	public void onBindViewHolder(ClickableViewHolder holder, final int position) {
		holder.getParentView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				itemClickListener.onItemClicked(position);
			}
		});
		holder.getParentView().setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return itemLongClickListener.onItemLongClicked(position);
			}
		});
	}

	public class ClickableViewHolder extends RecyclerView.ViewHolder {

		private View parentView;

		public ClickableViewHolder(View itemView) {
			super(itemView);
			this.parentView = itemView;
		}

		public View getParentView() {
			return parentView;
		}

	}

}
