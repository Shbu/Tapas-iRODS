package org.bio5.irods.iplugin.utilities;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

public final class IrodsUtilities {

	/* Logger instantiation */
	static Logger log = Logger.getLogger(IrodsUtilities.class.getName());

	/* Calculate MD5 CheckSum of a File */
	public static String calculateMD5CheckSum(File file) {
		try {
			InputStream fin = new FileInputStream(file);
			java.security.MessageDigest md5er = MessageDigest
					.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int read;
			do {
				read = fin.read(buffer);
				if (read > 0)
					md5er.update(buffer, 0, read);
			} while (read != -1);
			fin.close();
			byte[] digest = md5er.digest();
			if (digest == null)
				return null;
			String strDigest = "";
			for (int i = 0; i < digest.length; i++) {
				strDigest += Integer.toString((digest[i] & 0xff) + 0x100, 16)
						.substring(1).toLowerCase();
			}
			return strDigest;
		} catch (Exception e) {
			return null;
		}
	}

	/* Pull pathSeperator of the Operating System */
	public static String getPathSeperator() {
		String pathSeperator = Constants.DEFAULT_PATH_SEPERATOR;
		pathSeperator = System.getProperty("file.separator");
		return pathSeperator;
	}

	/* Get JTree node path depending on the Mouse selection */
	public static String getJtreeSelection(MouseEvent me,
			JTree userDirectoryTree) {
		String fullTreePath = "";
		TreePath tp = userDirectoryTree
				.getPathForLocation(me.getX(), me.getY());
		if (tp != null) {
			Object treepath[] = tp.getPath();
			for (int i = 0; i < treepath.length; i++) {
				fullTreePath += IrodsUtilities.getPathSeperator()
						+ treepath[i].toString();
			}
		}
		return fullTreePath;
	}

	public static File createFileFromTreePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		for (int i = 0; i < nodes.length; i++) {
			sb.append(File.separatorChar).append(nodes[i].toString());
		}
		return new File(sb.toString());
	}

	public static String createFilePathFromTreePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		for (int i = 0; i < nodes.length; i++) {
			sb.append(File.separatorChar).append(nodes[i].toString());
		}
		return sb.toString();
	}

	public static String getJtreeSelectionForSingleClick(MouseEvent me,
			JTree userDirectoryTree) {
		String fullTreePath = "";
		TreePath tp = userDirectoryTree
				.getPathForLocation(me.getX(), me.getY());
		if (tp != null) {
			DefaultMutableTreeNode lastPathComponentNode = (DefaultMutableTreeNode) tp
					.getLastPathComponent();
			if (lastPathComponentNode.isLeaf()) {
				tp = tp.getParentPath();
			}
			Object treepath[] = tp.getPath();
			for (int i = 0; i < treepath.length; i++) {
				fullTreePath += IrodsUtilities.getPathSeperator()
						+ treepath[i].toString();
			}
		}
		return fullTreePath;
	}

	public static boolean createDirectoryIfDoesntExist(String directoryPath) {
		boolean isDirectoryCreated = false;
		try {
			if (null != directoryPath && !"".equals(directoryPath)) {
				File file = new File(directoryPath);
				if (!file.exists()) {
					log.info("Cache folder doesn't exist- Creating folder");
					isDirectoryCreated = file.mkdirs();
				}
				if (file.exists()) {
					isDirectoryCreated = true;
				}
			}
		} catch (Exception e) {
			log.error("Error while creating ImageJ cache directory"
					+ e.getMessage());
			isDirectoryCreated = false;
		}
		return isDirectoryCreated;
	}

	public static String getFileNameFromDirectoryPath(String directoryPath) {
		String fileName = null;
		try {
			if (null != directoryPath && directoryPath != "") {
				String fullPath = directoryPath;
				int index = fullPath.lastIndexOf("\\");
				fileName = fullPath.substring(index + 1);
				log.info("File name extracted from given directory path: "
						+ fileName);
			} else {
				log.error("Given directoryPath is either empty or null!");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return fileName;
	}
}
