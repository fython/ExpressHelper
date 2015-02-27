package info.papdt.express.helper.ui.adapter;

import android.content.Context;
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

public class HomeCardAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ExpressDatabase db;

	public HomeCardAdapter(Context context, ExpressDatabase db) {
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mInflater = mInflater.cloneInContext(new ContextThemeWrapper(context, R.style.AppTheme_NoActionBar));
		this.db = db;
	}

	@Override
	public int getCount() {
		return db.size();
	}

	@Override
	public Express getItem(int i) {
		return db.getExpress(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.card_express_item, viewGroup, false);

			holder = new ViewHolder();

			holder.iv_round = (CircleImageView) view.findViewById(R.id.iv_round);
			holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
			holder.tv_desp = (TextView) view.findViewById(R.id.tv_desp);
			holder.tv_time = (TextView) view.findViewById(R.id.tv_time);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.tv_title.setText(db.getExpress(i).getMailNumber());

		String desp, time;
		try {
			ExpressResult cache = ExpressResult.buildFromJSON(db.getExpress(i).getData());
			Map<String, String> firstData = cache.data.get(0);
			desp = firstData.get("context");
			time = firstData.get("time");
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
		TextView tv_title, tv_desp, tv_time;

	}

}
