package info.papdt.express.helper.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.support.Express;
import info.papdt.express.helper.support.ExpressResult;
import info.papdt.express.helper.support.Settings;

public class HomeCardAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ExpressDatabase db;
	private int type;
	private boolean useCardStyle;

	private int[] defaultColors;

	public static final int TYPE_ALL = 0, TYPE_UNRECEIVED = 1, TYPE_RECEIVED = 2;

	public HomeCardAdapter(Context context, ExpressDatabase db) {
		this(context, db, TYPE_ALL);
	}

	public HomeCardAdapter(Context context, ExpressDatabase db, int type) {
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mInflater = mInflater.cloneInContext(new ContextThemeWrapper(context, R.style.AppTheme_NoActionBar));
		this.db = db;
		this.defaultColors = context.getResources().getIntArray(R.array.statusColor);
		this.type = type;
		this.useCardStyle = Settings.getInstance(context).getBoolean(Settings.KEY_USE_CARD_LIST, true);
	}

	@Override
	public int getCount() {
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
	public Express getItem(int i) {
		if (type == TYPE_ALL) {
			return db.getExpress(i);
		} else if (type == TYPE_UNRECEIVED) {
			return db.getUnreceivedArray().get(i);
		} else if (type == TYPE_RECEIVED) {
			return db.getReceivedArray().get(i);
		}
		return null;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(
					useCardStyle ? R.layout.card_express_item : R.layout.card_express_item_no_card,
					viewGroup,
					false
			);

			holder = new ViewHolder();

			holder.iv_round = (CircleImageView) view.findViewById(R.id.iv_round);
			holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
			holder.tv_desp = (TextView) view.findViewById(R.id.tv_desp);
			holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
			holder.tv_center_round = (TextView) view.findViewById(R.id.center_text);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Express nowItem = getItem(i);

		ExpressResult cache = ExpressResult.buildFromJSON(nowItem.getData());

		ColorDrawable drawable = new ColorDrawable(defaultColors[cache.status]);
		holder.iv_round.setImageDrawable(drawable);

		holder.tv_title.setText(nowItem.getName());

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

		return view;
	}

	private class ViewHolder {

		CircleImageView iv_round;
		TextView tv_title, tv_desp, tv_time, tv_center_round;

	}

}
