package com.jacx.ssoa.android.parser.xml;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jacx.ssoa.android.data.xml.XmlData;
import com.jacx.ssoa.android.data.xml.XmlNode;

import android.text.TextUtils;

/**
 * 
 */
public class XmlTreePullParser extends XmlTreeParser {

	/**
	 * 
	 */
	public static class XmlPullParserInstantiationException extends
			XmlPullParserException {

		public XmlPullParserInstantiationException(final String message) {
			super(message);
		}

		public XmlPullParserInstantiationException(final String message,
				final XmlPullParser parser, final Throwable chain) {
			super(message, parser, chain);
		}
	}

	/**
	 * 
	 */
	public static class XmlPullParserUnavailableFeatureException extends
			XmlPullParserException {

		public XmlPullParserUnavailableFeatureException(final String message) {
			super(message);
		}

		public XmlPullParserUnavailableFeatureException(final String message,
				final XmlPullParser parser, final Throwable chain) {
			super(message, parser, chain);
		}
	}

	/** The xml document property : Version */
	public static final String PROPERTY_XML_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
	/** The xml document property : Standalone */
	public static final String PROPERTY_XML_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";

	/**
	 * @param input
	 *            the input character stream
	 * @param createDocDecl
	 *            create the document declaration ?
	 * @param encoding
	 *            the encoding of the input stream
	 * @return an XML Node from the parser
	 * @throws XmlPullParserUnavailableFeatureException
	 *             when a feature is not supported by the current device
	 * @throws XmlPullParserInstantiationException
	 *             when the factory can't create a new parser
	 * @throws XmlPullParserException
	 *             when a parsing error occurs
	 * @throws IOException
	 * @throws StringIndexOutOfBoundsException
	 */
	public static XmlNode parseXmlTree(final InputStream input,
			final boolean createDocDecl, final String encoding)
			throws XmlPullParserUnavailableFeatureException,
			XmlPullParserInstantiationException, XmlPullParserException,
			StringIndexOutOfBoundsException, IOException {

		XmlPullParserFactory factory;
		XmlPullParser xpp;
		XmlTreePullParser parser;

		parser = new XmlTreePullParser();

		try {
			factory = XmlPullParserFactory.newInstance();
		} catch (XmlPullParserException e) {
			throw new XmlPullParserInstantiationException(
					"Factory couldn't create new parser instance", null, e);
		}

		try {
			xpp = factory.newPullParser();
		} catch (XmlPullParserException e) {
			throw new XmlPullParserUnavailableFeatureException(
					"Some required features are unavailable", null, e);
		}

		xpp.setInput(input, encoding);
		parser.parse(xpp);
		if (createDocDecl) {
			parser.pullDocumentDeclaration(xpp);
		}

		return parser.getRoot();
	}

	/**
	 * @param xpp
	 *            the {@link XmlPullParser} to use
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws StringIndexOutOfBoundsException
	 */
	protected void parse(final XmlPullParser xpp)
			throws XmlPullParserException, IOException,
			StringIndexOutOfBoundsException {

		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				createRootDocument();
				break;
			case XmlPullParser.END_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				pullElementNode(xpp);
				break;
			case XmlPullParser.END_TAG:
				onCloseElement();
				break;
			case XmlPullParser.TEXT:
				pullTextNode(xpp);
				break;
			case XmlPullParser.CDSECT:
				pullCDataNode(xpp);
				break;
			case XmlPullParser.ENTITY_REF:
				break;
			case XmlPullParser.IGNORABLE_WHITESPACE:
				break;
			case XmlPullParser.PROCESSING_INSTRUCTION:
				pullProcessingInstructionNode(xpp);
				break;
			case XmlPullParser.COMMENT:
				pullCommentNode(xpp);
				break;
			case XmlPullParser.DOCDECL:
				pullDoctypeNode(xpp);
				break;
			default:
				break;
			}
			eventType = xpp.nextToken();
		}

	}

	/**
	 * @param xpp
	 *            the parser
	 * @throws XmlPullParserException
	 */
	protected void pullElementNode(final XmlPullParser xpp)
			throws XmlPullParserException {
		String name, prefix, uri;

		name = xpp.getName();
		prefix = xpp.getPrefix();
		uri = xpp.getNamespace(prefix);
		if ((uri != null) && (prefix != null)) {
			declareNamespace(prefix, uri);
		}

		XmlNode tag = XmlNode.createElement(prefix, name);
		if (!xpp.isEmptyElementTag()) {
			tag.getContent().modifyFlags((byte) 0, XmlData.FLAG_EMPTY);
		}

		String attUri, attName, attValue, attPrefix;
		int count = xpp.getAttributeCount();
		for (int i = 0; i < count; ++i) {
			attUri = xpp.getAttributeNamespace(i);
			attPrefix = xpp.getAttributePrefix(i);
			attName = xpp.getAttributeName(i);
			attValue = xpp.getAttributeValue(i);

			if ((attUri != null) && (attPrefix != null)) {
				declareNamespace(attPrefix, attUri);
			}

			if (TextUtils.isEmpty(attPrefix) && (attName.indexOf(':') >= 0)) {
				String[] data = attName.split(":");
				if (data.length == 2) {
					attPrefix = data[0];
					attName = data[1];
				}
			}

			tag.getContent().addAttribute(attPrefix, attName, attValue);
		}

		onCreateElement(tag);
	}

	/**
	 * @param xpp
	 *            the parser
	 * @throws XmlPullParserException
	 */
	protected void pullTextNode(final XmlPullParser xpp)
			throws XmlPullParserException {
		if (!xpp.isWhitespace()) {
			onCreateNode(XmlNode.createText(xpp.getText()));
		}
	}

	/**
	 * @param xpp
	 *            the parser
	 * @throws XmlPullParserException
	 */
	protected void pullCDataNode(final XmlPullParser xpp)
			throws XmlPullParserException {
		onCreateNode(XmlNode.createCDataSection(xpp.getText()));
	}

	/**
	 * @param xpp
	 *            the parser
	 * @throws XmlPullParserException
	 */
	protected void pullCommentNode(final XmlPullParser xpp)
			throws XmlPullParserException {
		onCreateNode(XmlNode.createComment(xpp.getText()));
	}

	/**
	 * @param xpp
	 *            the parser
	 * @throws XmlPullParserException
	 */
	protected void pullDoctypeNode(final XmlPullParser xpp)
			throws XmlPullParserException {
		onCreateNode(XmlNode.createDoctypeDeclaration(xpp.getText()));
	}

	/**
	 * @param xpp
	 *            the parser
	 * @throws XmlPullParserException
	 */
	protected void pullProcessingInstructionNode(final XmlPullParser xpp)
			throws XmlPullParserException {

		String text = xpp.getText().trim();

		XmlNode pi = XmlNode.createProcessingInstruction(text);

		onCreateNode(pi);
	}

	/**
	 * @param xpp
	 *            the parser
	 */
	protected void pullDocumentDeclaration(final XmlPullParser xpp) {
		String version, enc;
		Boolean standalone;
		XmlNode decl;

		version = (String) xpp.getProperty(PROPERTY_XML_VERSION);
		if (version == null) {
			version = "1.0";
		}

		enc = xpp.getInputEncoding();

		standalone = (Boolean) xpp.getProperty(PROPERTY_XML_STANDALONE);

		decl = XmlNode.createDocumentDeclaration(version, enc, standalone);

		onCreateNode(decl);
	}

}
