package com.jacx.ssoa.android.ui.adapter;

import com.jacx.ssoa.android.data.tree.TreeNode;

public interface Highlighter<T> {

	boolean shouldHighlight(final TreeNode<T> node);
}
