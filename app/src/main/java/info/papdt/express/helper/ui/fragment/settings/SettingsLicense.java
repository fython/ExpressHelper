package info.papdt.express.helper.ui.fragment.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import info.papdt.express.helper.R;

public class SettingsLicense extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_license, container, false);

		WebView webView = (WebView) v.findViewById(R.id.webView);
		webView.loadUrl("file:///android_asset/licenses.html");

		getActivity().setTitle(R.string.item_open_source_license);

		return v;
	}

}
