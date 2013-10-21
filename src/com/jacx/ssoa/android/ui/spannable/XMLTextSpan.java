package com.jacx.ssoa.android.ui.spannable;

import android.content.Context;
import android.text.style.TextAppearanceSpan;
import com.jacx.ssoa.android.R;

/**
 * 
 */
public class XMLTextSpan extends TextAppearanceSpan {

	/**
	 * @param context
	 *            the current application context
	 */
	public XMLTextSpan(final Context context) {
		super(context, R.style.Axel_Xml_Text);
	}

}
