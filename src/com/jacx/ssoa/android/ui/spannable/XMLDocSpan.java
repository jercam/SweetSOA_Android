package com.jacx.ssoa.android.ui.spannable;

import android.content.Context;
import android.text.style.TextAppearanceSpan;
import com.jacx.ssoa.android.R;

/**
 * 
 */
public class XMLDocSpan extends TextAppearanceSpan {

	/**
	 * @param context
	 *            the current application context
	 */
	public XMLDocSpan(final Context context) {
		super(context, R.style.Axel_Xml_Document);
	}

}
