package com.jacx.ssoa.android.data.xml;

import com.jacx.ssoa.android.data.tree.TreeNode;
import com.jacx.ssoa.android.ui.adapter.Highlighter;

public class XmlHighlighter implements Highlighter<XmlData> {

	public XmlHighlighter(final String query) {
		mQuery = query;
	}

	@Override
	public boolean shouldHighlight(final TreeNode<XmlData> node) {
		boolean shouldHighlitght = false;
		XmlData content = node.getContent();

		if (content.isElement()) {
			shouldHighlitght = content.getName().contains(mQuery);
			if (!shouldHighlitght) {
				for (XmlAttribute attr : content.getAttributes()) {
					if (attr.getFullName() != null) {
						shouldHighlitght |= (attr.getFullName()
								.contains(mQuery));
					}

					if (attr.getValue() != null) {
						shouldHighlitght |= attr.getValue().contains(mQuery);
					}

					if (shouldHighlitght) {
						break;
					}
				}
			}
		} else if (content.isText()) {
			shouldHighlitght = content.getText().contains(mQuery);
		} else if (content.isCData()) {
			shouldHighlitght = content.getText().contains(mQuery);
		}

		return shouldHighlitght;
	}

	private String mQuery;
}
