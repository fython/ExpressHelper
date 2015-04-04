package info.papdt.express.helper.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.support.Express;
import info.papdt.express.helper.support.ExpressResult;
import info.papdt.express.helper.ui.common.MyRecyclerViewAdapter;

public class HomeCardRecyclerAdapter extends MyRecyclerViewAdapter {

	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_ITEM = 1;

	private ExpressDatabase db;
	private int type;
	private View headerView;

	private int[] defaultColors;

	public static final int TYPE_ALL = 0, TYPE_UNRECEIVED = 1, TYPE_RECEIVED = 2;

	public HomeCardRecyclerAdapter(Context context, ExpressDatabase db, View headerView) {
		this(context, db, TYPE_ALL, headerView);
	}

	public HomeCardRecyclerAdapter(Context context, ExpressDatabase db, int type, View headerView) {
		this.db = db;
		this.defaultColors = context.getResources().getIntArray(R.array.statusColor);
		this.type = type;
		this.headerView = headerView;
	}

	@Override
	public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case VIEW_TYPE_HEADER:
				return new HeaderViewHolder(headerView);
			case VIEW_TYPE_ITEM:
				View v = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.card_express_item, parent, false);
				return new ViewHolder(v);
			default:
				return null;
		}
	}

	@Override
	public void onBindViewHolder(ClickableViewHolder viewHolder, final int position) {
		if (!(viewHolder instanceof ViewHolder)) return;

		super.onBindViewHolder(viewHolder, position);

		Express item = getItem(position + (headerView != null ? -1 : 0));

		ExpressResult cache = item.getData();

		ViewHolder holder = (ViewHolder) viewHolder;

		ColorDrawable drawable = new ColorDrawable(defaultColors[cache.status]);
		holder.iv_round.setImageDrawable(drawable);

		holder.tv_title.setText(item.getName());

		String desp, time;
		try {
			Map<String, String> lastData = cache.data.get(cache.data.size() - 1);
			holder.tv_center_round.setText(cache.expTextName.substring(0, 1));
			desp = lastData.get("context");
			time = lastData.get("time");
		} catch (Exception e) {
			desp = "failed";
			time = "1970/01/01";
		}
		holder.tv_desp.setText(desp);
		holder.tv_time.setText(time);
	}

	public int getExpressCount() {
		if (type == TYPE_ALL) {
			return db.size();
		} else if (type == TYPE_UNRECEIVED) {
			return db.urSize();
		} else if (type == TYPE_RECEIVED) {
			return db.okSize();
		}
		return -1;
	}

	@Override
	public int getItemCount() {
		int result = getExpressCount();
		if (headerView != null) result++;
		return result;
	}

	@Override
	public int getItemViewType(int position) {
		return (position == 0 && headerView != null) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
	}

	public Express getItem(int i) {
		if (type == TYPE_ALL) {
			return db.getExpress(getExpressCount() - i - 1);
		} else if (type == TYPE_UNRECEIVED) {
			return db.getUnreceivedArray().get(getExpressCount() - i - 1);
		} else if (type == TYPE_RECEIVED) {
			return db.getReceivedArray().get(getExpressCount() - i - 1);
		}
		return null;
	}

	public class ViewHolder extends ClickableViewHolder {

		public CircleImageView iv_round;
		public TextView tv_title, tv_desp, tv_time, tv_center_round;

		public ViewHolder(View itemView) {
			super(itemView);
			this.iv_round = (CircleImageView) itemView.findViewById(R.id.iv_round);
			this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
			this.tv_desp = (TextView) itemView.findViewById(R.id.tv_desp);
			this.tv_time = (TextView) itemView.findViewById(R.id.tv_time);
			this.tv_center_round = (TextView) itemView.findViewById(R.id.center_text);
		}

	}

	public class HeaderViewHolder extends ClickableViewHolder {

		public HeaderViewHolder(View view) {
			super(view);
		}

	}

}