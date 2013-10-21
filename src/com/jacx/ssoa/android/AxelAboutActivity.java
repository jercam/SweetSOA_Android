package com.jacx.ssoa.android;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import fr.xgouchet.androidlib.ui.activity.AboutActivity;
import com.jacx.ssoa.android.R;

/**
 * 
 */
public class AxelAboutActivity extends AboutActivity implements OnClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
