package com.jacx.ssoa.android;

import com.jacx.ssoa.android.data.xml.XmlNode;

import com.jacx.ssoa.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AxelPreviewActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SweetSOAApplication app = (SweetSOAApplication) getApplication();

		setTitle(getString(R.string.title_preview, app.getCurrentDocumentName()));

		XmlNode node = app.getCurrentDocument();
		StringBuilder builder = new StringBuilder();
		node.buildXmlString(builder);
		String data = builder.toString();
		builder = null;

		String mimetype = app.getMimeType();

		if (mimetype.equals("text/html")) {
			data = data.replace("%", "%25");
			data = data.replace("#", "%23");
			data = data.replace("\\", "%27");
			data = data.replace("?", "%3f");
		}

		WebView webview = new WebView(getBaseContext());
		webview.loadData(data, mimetype, "utf-8");
		webview.getSettings().setBuiltInZoomControls(true);
		setContentView(webview);
	}
}
