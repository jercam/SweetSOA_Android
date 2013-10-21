package com.jacx.ssoa.android.common;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import fr.xgouchet.androidlib.data.FileUtils;

/**
 * 
 */
public final class TemplateFiles {

	/** */
	public static final String TEMPLATE_FOLDER = "templates";
	/** */
	public static File sTemplateFolder = null;

	/**
	 * Copies all templates in assets to the app template folder
	 * 
	 * @param context
	 */
	public static void copyTemplatesFromAssets(final Context context) {
		final File templateFolder = getTemplateFolder(context);

		FileUtils.copyAssetsToAppData(context, templateFolder, TEMPLATE_FOLDER);
	}

	/**
	 * @param context
	 *            the current application context
	 * @param name
	 *            the name to validate
	 * @return if the name valid for a new template file
	 */
	public static boolean validateTemplateName(final Context context,
			final String name) {
		final List<File> files = getTemplateFiles(context);

		boolean valid = true;

		for (File f : files) {

			if (f.getName().equals(name)) {
				valid = false;
				break;
			}
		}
		return valid;
	}

	/**
	 * @param contextFile
	 *            the context file to remove
	 * @return if the file was removed
	 */
	public static boolean removeFile(final File contextFile) {
		return contextFile.delete();
	}

	/**
	 * @param context
	 *            the curent application context
	 * @param name
	 *            the name of the file
	 * @return a path to write output to
	 * 
	 */
	public static String getOuputPath(final Context context, final String name) {
		return getTemplateFolder(context).getPath() + File.separator + name;
	}

	/**
	 * @param context
	 *            the current application context
	 * @return the list of template files
	 */
	public static List<File> getTemplateFiles(final Context context) {
		final File[] files = getTemplateFolder(context).listFiles();

		final LinkedList<File> res = new LinkedList<File>();

		for (File f : files) {
			if (!f.canRead()) {
				continue;
			}

			if (!f.isFile()) {
				continue;
			}

			res.add(f);
		}

		return res;
	}

	/**
	 * @param context
	 *            the current application context
	 * @return the template folder
	 */
	private static File getTemplateFolder(final Context context) {
		if (sTemplateFolder == null) {
			sTemplateFolder = context.getDir(TEMPLATE_FOLDER,
					Context.MODE_PRIVATE);
		}

		return sTemplateFolder;
	}

	private TemplateFiles() {
	}
}
