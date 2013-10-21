package com.jacx.ssoa.android;

import com.jacx.ssoa.android.data.xml.XmlNode;

import android.annotation.SuppressLint;
import android.app.Application;
import fr.xgouchet.androidlib.data.FileUtils;

/**
 * 
 */
public class SweetSOAApplication extends Application {

	public void documentContentChanged() {
	}

	public void setCurrentDocument(final XmlNode doc, final String name, final String path) {
		mCurrentDocument = doc;
		mCurrentDocumentName = name;
		mCurrentDocumentPath = path;
	}

	public void setCurrentSelection(final XmlNode currentSelection) {
		mCurrentSelection = currentSelection;
	}

	public XmlNode getCurrentDocument() {
		return mCurrentDocument;
	}

	public String getCurrentDocumentName() {
		return mCurrentDocumentName;
	}

	public String getCurrentDocumentPath() {
		return mCurrentDocumentPath;
	}

	public XmlNode getCurrentSelection() {
		return mCurrentSelection;
	}

	/**
	 * @return if the current document can be displayed in a webview
	 */
	@SuppressLint("DefaultLocale")
	public boolean canBePreviewed() {
		boolean res = false;

		// test on file name extension
		String ext = FileUtils.getFileExtension(mCurrentDocumentName)
				.toLowerCase();
		if (ext.equals("html") || ext.equals("htm") || ext.equals("php")
				|| (ext.equals("svg"))) {
			res = true;
		}

		// test on root node
		XmlNode root = mCurrentDocument.getRootChild();
		if (root != null) {
			String tag = root.getContent().getName();
			if ("html".equalsIgnoreCase(tag) || "svg".equalsIgnoreCase(tag)) {
				res = true;
			}
		}

		// TODO test on root namespace / doctype

		return res;
	}

	/**
	 * @return the mime type of the current document
	 */
	@SuppressLint("DefaultLocale")
	public String getMimeType() {
		String type = "text/xml";

		// test on file name extension
		String ext = FileUtils.getFileExtension(mCurrentDocumentName)
				.toLowerCase();
		if (ext.equals("html") || ext.equals("htm") || ext.equals("php")) {
			type = "text/html";
		} else if (ext.equals("svg")) {
			type = "text/html";
		} else {
			// test on root node
			XmlNode root = mCurrentDocument.getRootChild();
			if (root != null) {
				String tag = root.getContent().getName().toLowerCase();
				if (tag.equals("html")) {
					type = "text/html";
				} else if (tag.equals("svg")) {
					type = "text/html";
				}
			}
		}

		return type;
	}

	/** */
	private String mCurrentDocumentName;
	/** */
	private String mCurrentDocumentPath;
	/** */
	private XmlNode mCurrentDocument;
	/** */
	private XmlNode mCurrentSelection;

}
