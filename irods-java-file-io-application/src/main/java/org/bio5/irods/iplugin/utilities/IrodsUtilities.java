package org.bio5.irods.iplugin.utilities;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Properties;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.swingworkers.ObjectDetailsLite;
import org.irods.jargon.core.pub.domain.ObjStat;

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

	/* Returns pathSeperator of the Operating System */
	public static String getPathSeperator() {
		String pathSeperator = null;
		pathSeperator = Constants.DEFAULT_PATH_SEPERATOR;
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
	
	public static String getJtreeSelection(TreePath treePaths) {
		String fullTreePath = "";
			Object treepath[] = treePaths.getPath();
			for (int j = 0; j < treepath.length; j++) {
				fullTreePath += IrodsUtilities.getPathSeperator()
						+ treepath[j].toString();
			}
		return fullTreePath;
	}

	/**/
	public static File createFileFromTreePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		for (int i = 0; i < nodes.length; i++) {
			sb.append(File.separatorChar).append(nodes[i].toString());
		}
		return new File(sb.toString());
	}

	/* Generate FilePath from given treePath */
	public static String createFilePathFromTreePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		for (int i = 0; i < nodes.length; i++) {
			sb.append(File.separatorChar).append(nodes[i].toString());
		}
		return sb.toString();
	}

	/* Returns JTree selection depending on the mouseEvent */
	public static String getJtreeSelectionForSingleClick(IPlugin iplugin,
			MouseEvent me, JTree userDirectoryTree) {
		String fullTreePath = "";
		TreePath tp = userDirectoryTree
				.getPathForLocation(me.getX(), me.getY());
		if (tp != null) {
			DefaultMutableTreeNode lastPathComponentNode = (DefaultMutableTreeNode) tp
					.getLastPathComponent();

			/*
			 * Get objstat details for each file and compare if it is a
			 * collection or object
			 */
			ObjStat objStatValueOfObject = null;
			if (null != iplugin) {
				ObjectDetailsLite obj = new ObjectDetailsLite(iplugin);
				try {
					objStatValueOfObject = obj.getObjStatValueOfObj();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error("Error while getting ObjStat values of object");
				}
			}
			if (null != objStatValueOfObject) {
				if (/*
					 * lastPathComponentNode.isLeaf() &&
					 */objStatValueOfObject.getObjectType().toString() == Constants.OBJECT_TYPE_DATA_OBJECT) {
					tp = tp.getParentPath();
				}
			}
			Object treepath[] = tp.getPath();
			for (int i = 0; i < treepath.length; i++) {
				fullTreePath += IrodsUtilities.getPathSeperator()
						+ treepath[i].toString();
			}
		}
		return fullTreePath;
	}

	/* Creates a directory at specific path if doesn't exist */
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

	/*
	 * Get file name from given directory path. This returns the subString after
	 * last slash in absolute path.
	 */
	public static String getFileNameFromDirectoryPath(String directoryPath) {
		String fileName = null;
		try {
			if (null != directoryPath && directoryPath != "") {
				String fullPath = directoryPath;
				int index = fullPath.lastIndexOf(getPathSeperator());
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

	/* Returns user home directory folder path */
	public static String getUserHomeFolderFromSystemProperty() {
		String userHomeFolderFromSystemProperty = null;
		userHomeFolderFromSystemProperty = System.getProperty("user.home");
		return userHomeFolderFromSystemProperty;
	}

	public static long getFolderSize(File dir) {
		long size = 0;
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				// System.out.println(file.getName() + " " + file.length());
				size += file.length();
			} else
				size += getFolderSize(file);
		}
		return size;
	}

	/*
	 * This method will load Tapas properties from either of below two file - 1.
	 * Local Property file in cache directory or 2. Property file in Jar. If
	 * local file is not available, then property file available in jar is
	 * loaded and then a copy is left in local path for future availability.
	 */

	public static Properties getTapasLoginConfiguration(
			String propertyFileName, String localPathForPropertyFile) {

		Properties tapasConfigurationProperties = null;
		if (null == propertyFileName) {
			/* pending - change the final path */
			propertyFileName = Constants.PROPERTY_FILE_NAME;
			/*
			 * propertyFileName =
			 * "/irods-java-file-io-application/src/main/resources/" +
			 * Constants.PROPERTY_FILE_NAME;
			 */
		}
		if (null == localPathForPropertyFile) {
			localPathForPropertyFile = Constants.IMAGEJ_CACHE_FOLDER;
		}
		tapasConfigurationProperties = new Properties();

		tapasConfigurationProperties = loadLocalTapasPropertyFiles(localPathForPropertyFile);

		if (null != tapasConfigurationProperties) {
			log.info("tapas configuration properties is not null - Returning values after first null check");
			return tapasConfigurationProperties;
		} else {
			/* Pulling property file from jar */
			Configuration propertyConfiguration = null;
			URL propFileURL = null;
			try {
				propFileURL = IrodsUtilities.class.getClassLoader()
						.getResource(propertyFileName);
				if (null != propFileURL) {
					propertyConfiguration = new PropertiesConfiguration(
							propFileURL);
				} else {
					log.error("propFileURL is null");
				}

			} catch (ConfigurationException configurationException) {
				log.error("Exception while loading configuration of property files"
						+ configurationException.getMessage());
			}

			/* Getting properties file from ConfigurationProperties */
			if (null != propertyConfiguration) {
				log.info("propertyConfiguration is not null");
				tapasConfigurationProperties = ConfigurationConverter
						.getProperties(propertyConfiguration);
			} else {
				log.error("propertyConfiguration is null");
			}

			/* Creating outputStreamForPropertiesFile */
			OutputStream outputStreamForPropertiesFile = null;
			try {
				String filePath = Constants.IMAGEJ_CACHE_FOLDER
						+ IrodsUtilities.getPathSeperator()
						+ Constants.PROPERTY_FILE_NAME;
				if (null != filePath && "" != filePath) {
					outputStreamForPropertiesFile = new FileOutputStream(
							filePath);
				}
			} catch (FileNotFoundException fileNotFoundException) {
				log.error("fileNotFound Exception while creating output stream of properties file"
						+ fileNotFoundException.getMessage());
			}

			/* Storing properties file to local disk */
			try {
				if (null != outputStreamForPropertiesFile
						&& null != tapasConfigurationProperties) {
					log.info("Copying property file to local cache folder");
					tapasConfigurationProperties.store(
							outputStreamForPropertiesFile,
							"Tapas properties file - V1.1");
				} else {
					log.error("outputStreamForPropertiesFile OR tapasConfigurationProperties is null");
				}
			} catch (IOException ioException) {
				log.error("IOException while storing properties file to local disk"
						+ ioException.getMessage());
			}
		}
		return tapasConfigurationProperties;
	}

	/* Copies a given file from one location to other */
	public static void copyFileToNewPhysicalLocation(File sourceFile,
			String destination) {
		File destinationFile = new File(destination);
		try {
			log.info("Inside copyFileToDirectory");
			FileUtils.copyFileToDirectory(sourceFile, destinationFile);
		} catch (IOException e) {
			log.error("Error while copying file from source to destination"
					+ e.getMessage());
		}
	}

	public static Properties loadLocalTapasPropertyFiles(String filePath) {
		Properties tapasLocalConfigurationProperties = null;

		if (null != filePath) {
			String propertyFileName = filePath
					+ IrodsUtilities.getPathSeperator()
					+ Constants.PROPERTY_FILE_NAME;
			try {
				Reader reader = new FileReader(propertyFileName);
				tapasLocalConfigurationProperties = new Properties();
				tapasLocalConfigurationProperties.load(reader);
			} catch (FileNotFoundException fileNotFoundException) {
				log.error("fileNotFoundException Error while reading localPropertyFile "
						+ fileNotFoundException.getMessage());
			} catch (IOException ioException) {
				log.error("ioException Error while loading local property file"
						+ ioException.getMessage());
			}
		}
		return tapasLocalConfigurationProperties;
	}

	/*
	 * Replaces multiple slashes with single slash in a given string- Applicable
	 * for both FORWARD SLASH and BACKWARD SLASH
	 */

	public static String refactorSlashInFilePaths(String filepath) {
		String finalPath = null;

		if (null != filepath && "" != filepath) {
			log.info("Path before refactoring: " + filepath);
			if (filepath.contains("/")) {
				finalPath = filepath.replaceAll("/+", "/");
				log.info("Path after refactoring: " + finalPath);
			}
			if (filepath.contains("\\")) {
				finalPath = filepath.replaceAll("\\+", "\\");
				log.info("Path after refactoring: " + finalPath);
			}

		} else {
			log.error("Given filePath is null");
		}
		return finalPath;
	}

}
