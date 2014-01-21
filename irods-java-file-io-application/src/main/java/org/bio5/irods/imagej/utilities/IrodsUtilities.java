package org.bio5.irods.imagej.utilities;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

public final class IrodsUtilities {

	/*Calculate MD5 CheckSum of a File*/
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
	
	/*Pull pathSeperator of the Operating System*/
	public static String pathSeperator() {
		String pathSeperator = Constants.DEFAULT_PATH_SEPERATOR;
		pathSeperator = System.getProperty("file.separator");
		return pathSeperator;
	}
	
	/*Get JTree node path depending on the Mouse selection*/
	public static String getJtreeSelection(MouseEvent me, JTree userDirectoryTree)
	{
		String fullTreePath="";
		TreePath tp =userDirectoryTree.getPathForLocation(me.getX(), me.getY());
		if(tp!=null)
		{
			Object treepath[] =tp.getPath();
			for(int i=0;i<treepath.length;i++)
			{
				fullTreePath  += IrodsUtilities.pathSeperator() +treepath[i].toString();
			}
		}
		return fullTreePath;
	}
}
