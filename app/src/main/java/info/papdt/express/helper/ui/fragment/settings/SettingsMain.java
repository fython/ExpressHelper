package info.papdt.express.helper.ui.fragment.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;

import info.papdt.express.helper.R;
import info.papdt.express.helper.support.Settings;
import info.papdt.express.helper.ui.SettingsActivity;

public class SettingsMain extends PreferenceFragment implements Preference.OnPreferenceClickListener {

	public static SettingsMain newInstance() {
		return new SettingsMain();
	}

	public SettingsMain() {

	}

	private Settings mSets;

	private Preference pref_version, pref_donate, pref_os_license, pref_api_provider;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		addPreferencesFromResource(R.xml.pref_main);

		mSets = Settings.getInstance(getActivity().getApplicationContext());

		getActivity().setTitle(R.string.title_settings);

		pref_version = findPreference("application_version");
		pref_os_license = findPreference("open_source_license");
		pref_api_provider = findPreference("api_provider");
		pref_donate = findPreference("donate");

		String version = "Unknown";
		try {
			version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			version += " (" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode + ")";
		} catch (Exception e) {
			// Keep the default value
		}
		pref_version.setSummary(version);

		pref_os_license.setOnPreferenceClickListener(this);
		pref_api_provider.setOnPreferenceClickListener(this);
		pref_donate.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference p) {
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


}
