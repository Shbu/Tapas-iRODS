package org.bio5.irods.imagej.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public  final class IrodsUtilities {
	
	public static final String DATA_IPLANTCOLLABORATIVE_ORG = "data.iplantcollaborative.org";
	public static String HOME_DIR ="/iplant/home/";
	public static String ZONE="iplant";

	
	public static String calculateMD5CheckSum(File file) {
		  try {
		    InputStream fin = new FileInputStream(file);
		    java.security.MessageDigest md5er =
		        MessageDigest.getInstance("MD5");
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
		      strDigest += Integer.toString((digest[i] & 0xff) 
		                + 0x100, 16).substring(1).toLowerCase();
		    }
		    return strDigest;
		  } catch (Exception e) {
		    return null;
		  }
		}
}
