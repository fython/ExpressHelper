package info.papdt.express.helper.support;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import info.papdt.express.helper.R;
import info.papdt.express.helper.dao.ExpressDatabase;
import info.papdt.express.helper.ui.DetailsActivity;

public class ReminderService extends IntentService {
	private static final String TAG = ReminderService.class.getSimpleName();

	private static final int ID = 100000;

	private Notification produceNotifications(int position, Express exp) {
		if (exp != null) {
			int defaults = parseDefaults(getApplicationContext());

			PendingIntent pi;

			Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			i.putExtra("id", position);
			i.putExtra("data", exp.toJSONObject().toString());

			pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

			String title;
			if (exp.getData().getTrueStatus() == ExpressResult.STATUS_DELIVERED) {
				title = exp.getName() + getString(R.string.notification_delivered);
			} else {
				title = exp.getName() + getString(R.string.notification_new_message);
			}

			Notification n = buildNotification(getApplicationContext(),
						title,
						exp.getData().data.get(0).get("context"),
						R.drawable.ic_local_shipping_white_24dp,
						defaults,
						pi);

			return n;
		}
		return null;
	}

	public ReminderService() {
		super(TAG);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onHandleIntent(Intent intent) {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		ExpressDatabase db = ExpressDatabase.getInstance(getApplicationContext());

		db.pullNewDataFromNetwork(false);

		for (int i = 0; i < db.size(); i++) {
			Express exp = db.getExpress(i);
			if (exp.getData().getTrueStatus() != ExpressResult.STATUS_FAILED && exp.getData().getTrueStatus() != exp.getLastStatus()) {
				Notification n = produceNotifications(i, exp);
				if (exp != null) {
					nm.notify(i + 20000, n);
				}
			}
		}
	}

	private static int parseDefaults(Context context) {
		Settings settings = Settings.getInstance(context);

		return (settings.getBoolean(Settings.KEY_NOTIFICATION_SOUND, true) ? Notification.DEFAULT_SOUND : 0) |
				(settings.getBoolean(Settings.KEY_NOTIFICATION_VIBRATE, true) ? Notification.DEFAULT_VIBRATE : 0) |
				Notification.DEFAULT_LIGHTS;
	}

	private static Notification buildNotification(Context context, String title, String text, int icon, int defaults, PendingIntent intent) {
		return new Notification.Builder(context)
				.setContentTitle(title)
				.setContentText(text)
				.setSmallIcon(icon)
				.setDefaults(defaults)
				.setAutoCancel(true)
				.setContentIntent(intent)
				.build();
	}

	private static String format(Context context, int resId, int data) {
		return String.format(context.getString(resId), data);
	}
}