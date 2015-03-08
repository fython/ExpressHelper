package info.papdt.express.helper.ui.fragment.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;

import info.papdt.express.helper.R;
import info.papdt.express.helper.support.Settings;
import info.papdt.express.helper.ui.SettingsActivity;

public class SettingsMain extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

	public static SettingsMain newInstance() {
		return new SettingsMain();
	}

	public SettingsMain() {

	}

	private Settings mSets;

	private Preference pref_version, pref_donate, pref_os_license, pref_api_provider,
		pref_weibo, pref_github;
	private SwitchPreference pref_card_list;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		addPreferencesFromResource(R.xml.pref_main);

		mSets = Settings.getInstance(getActivity().getApplicationContext());

		getActivity().setTitle(R.string.title_settings);

		pref_version = findPreference("application_version");
		pref_weibo = findPreference("sina_weibo");
		pref_os_license = findPreference("open_source_license");
		pref_api_provider = findPreference("api_provider");
		pref_donate = findPreference("donate");
		pref_card_list = (SwitchPreference) findPreference("use_card_list");
		pref_github = findPreference("github_repo");

		String version = "Unknown";
		try {
			version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			version += " (" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode + ")";
		} catch (Exception e) {
			// Keep the default value
		}
		pref_version.setSummary(version);
		pref_card_list.setChecked(mSets.getBoolean(Settings.KEY_USE_CARD_LIST, true));

		pref_weibo.setOnPreferenceClickListener(this);
		pref_os_license.setOnPreferenceClickListener(this);
		pref_api_provider.setOnPreferenceClickListener(this);
		pref_donate.setOnPreferenceClickListener(this);
		pref_github.setOnPreferenceClickListener(this);
		pref_card_list.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference p) {
		if (p == pref_weibo) {
			openWebsite(getString(R.string.item_weibo_author_url));
			return true;
		}
		if (p == pref_os_license) {
			SettingsActivity.launchActivity(getActivity(), SettingsActivity.FLAG_LICENSE);
			return true;
		}
		if (p == pref_api_provider) {
			openWebsite(getString(R.string.api_provider_home));
			return true;
		}
		if (p == pref_donate) {
			showDonateDialog();
			return true;
		}
		if (p == pref_github) {
			openWebsite(getString(R.string.item_github_url));
			return true;
		}
		return false;
	}

	private void openWebsite(String url) {
		Uri uri = Uri.parse(url);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	private void showDonateDialog() {
		View v = View.inflate(
				new ContextThemeWrapper(
						getActivity().getApplicationContext(),
						R.style.Theme_AppCompat_Light_Dialog
				),
				R.layout.dialog_donate,
				null
		);
		new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog)
				.setTitle(R.string.item_donate)
				.setView(v)
				.setNegativeButton(android.R.string.ok, null)
				.show();
	}


	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		if (pref == pref_card_list) {
			Boolean b = (Boolean) newValue;
			mSets.putBoolean(Settings.KEY_USE_CARD_LIST, b);
			pref_card_list.setChecked(b);
			showRestartTips();
			return true;
		}
		return false;
	}

	private void showRestartTips() {
		Toast.makeText(
				getActivity().getApplicationContext(),
				R.string.toast_you_need_restart,
				Toast.LENGTH_SHORT
		).show();
	}

}
