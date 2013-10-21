package com.jacx.ssoa.android.ui.spannable;

import android.content.Context;
import android.text.style.TextAppearanceSpan;
import com.jacx.ssoa.android.R;

/**
 *  
 */
public class XMLTagDecoratorSpan extends TextAppearanceSpan {

	/**
	 * @param context
	 *            the current application context
	 */
	public XMLTagDecoratorSpan(final Context context) {
		super(context, R.style.Axel_Xml_Decorators);
	}

}
