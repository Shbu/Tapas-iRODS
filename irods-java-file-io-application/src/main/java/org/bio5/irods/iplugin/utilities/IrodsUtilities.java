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
	static Logger log = Logger.getLogger(IrodsUtilities.class.getName());

	public static String calculateMD5CheckSum(File file) {
		try {
			InputStream fin = new FileInputStream(file);
			MessageDigest md5er = MessageDigest.getInstance("MD5");

			byte[] buffer = new byte[1024];
			int read;
			do {
				read = fin.read(buffer);
				if (read > 0) {
					md5er.update(buffer, 0, read);
				}
			} while (read != -1);
			fin.close();
			byte[] digest = md5er.digest();
			if (digest == null) {
				return null;
			}
			String strDigest = "";
			for (int i = 0; i < digest.length; i++) {
				strDigest = strDigest
						+ Integer.toString((digest[i] & 0xFF) + 256, 16)
								.substring(1).toLowerCase();
			}
			return strDigest;
		} catch (Exception e) {
		}
		return null;
	}

	public static String getJtreeSelection(TreePath treePaths) {
		String fullTreePath = "";
		Object[] treepath = treePaths.getPath();
		for (int j = 0; j < treepath.length; j++) {
			fullTreePath = fullTreePath + getPathSeperator()
					+ treepath[j].toString();
		}
		return fullTreePath;
	}

	public static String getPathSeperator() {
		String pathSeperator = null;
		pathSeperator = Constants.DEFAULT_PATH_SEPERATOR;
		pathSeperator = System.getProperty("file.separator");
		return pathSeperator;
	}

	public static String getJtreeSelection(MouseEvent mouseEvent,
			JTree userDirectoryTree) {
		String fullTreePath = "";
		TreePath tp = userDirectoryTree.getPathForLocation(mouseEvent.getX(),
				mouseEvent.getY());
		if (tp != null) {
			Object[] treepath = tp.getPath();
			for (int i = 0; i < treepath.length; i++) {
				fullTreePath = fullTreePath + getPathSeperator()
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

	public static String getJtreeSelectionForSingleClick(IPlugin iplugin,
			MouseEvent mouseEvent, JTree jTree) {
		String fullTreePath = "";
		TreePath tp = jTree.getPathForLocation(mouseEvent.getX(),
				mouseEvent.getY());
		if (tp != null) {
			ObjStat objStatValueOfObject = null;
			if (null != iplugin) {
				ObjectDetailsLite obj = new ObjectDetailsLite(iplugin);
				try {
					objStatValueOfObject = obj.getObjStatValueOfObj();
				} catch (Exception e) {
					log.error("Error while getting ObjStat values of object");
				}
			}
			if (null != objStatValueOfObject) {
				if (objStatValueOfObject.getObjectType().toString() == "DATA_OBJECT") {
					tp = tp.getParentPath();
				}
				log.info("object size: " + objStatValueOfObject.getObjSize());
				if (objStatValueOfObject.getObjSize() == 0L) {
					log.info("folder size is 0");
					iplugin.setEmptyFolder(true);
				} else {
					log.info("folder size is not 0");
					iplugin.setEmptyFolder(false);
				}
			}
			Object[] treepath = tp.getPath();
			for (int i = 0; i < treepath.length; i++) {
				fullTreePath = fullTreePath + getPathSeperator()
						+ treepath[i].toString();
			}
		}
		return fullTreePath;
	}

	public static boolean createDirectoryIfDoesntExist(String directoryPath) {
		boolean isDirectoryCreated = false;
		try {
			if ((null != directoryPath) && (!"".equals(directoryPath))) {
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
			if ((null != directoryPath) && (directoryPath != "")) {
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

	public static String getUserHomeFolderFromSystemProperty() {
		String userHomeFolderFromSystemProperty = null;
		userHomeFolderFromSystemProperty = System.getProperty("user.home");
		return userHomeFolderFromSystemProperty;
	}

	public static long getFolderSize(File dir) {
		long size = 0L;
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				size += file.length();
			} else {
				size += getFolderSize(file);
			}
		}
		return size;
	}

	public static Properties getTapasLoginConfiguration(
			String propertyFileName, String localPathForPropertyFile) {
		Properties tapasConfigurationProperties = null;
		if (null == propertyFileName) {
			propertyFileName = "tapas.properties";
		}
		if (null == localPathForPropertyFile) {
			localPathForPropertyFile = Constants.IMAGEJ_CACHE_FOLDER;
		}
		tapasConfigurationProperties = new Properties();

		tapasConfigurationProperties = loadLocalTapasPropertyFiles(localPathForPropertyFile);
		if (null != tapasConfigurationProperties) {
			log.info("tapas configuration properties is not null - Returning values after first null check");
			return tapasConfigurationProperties;
		}
		Configuration propertyConfiguration = null;
		URL propFileURL = null;
		try {
			propFileURL = IrodsUtilities.class.getClassLoader().getResource(
					propertyFileName);
			if (null != propFileURL) {
				propertyConfiguration = new PropertiesConfiguration(propFileURL);
			} else {
				log.error("propFileURL is null");
			}
		} catch (ConfigurationException configurationException) {
			log.error("Exception while loading configuration of property files"
					+ configurationException.getMessage());
		}
		if (null != propertyConfiguration) {
			log.info("propertyConfiguration is not null");
			tapasConfigurationProperties = ConfigurationConverter
					.getProperties(propertyConfiguration);
		} else {
			log.error("propertyConfiguration is null");
		}
		OutputStream outputStreamForPropertiesFile = null;
		try {
			String filePath = Constants.IMAGEJ_CACHE_FOLDER
					+ getPathSeperator() + "tapas.properties";
			if ((null != filePath) && ("" != filePath)) {
				outputStreamForPropertiesFile = new FileOutputStream(filePath);
			}
		} catch (FileNotFoundException fileNotFoundException) {
			log.error("fileNotFound Exception while creating output stream of properties file"
					+ fileNotFoundException.getMessage());
		}
		try {
			if ((null != outputStreamForPropertiesFile)
					&& (null != tapasConfigurationProperties)) {
				log.info("Copying property file to local cache folder");
				tapasConfigurationProperties.store(
						outputStreamForPropertiesFile,
						"Tapas V1.0 - properties file");
			} else {
				log.error("outputStreamForPropertiesFile OR tapasConfigurationProperties is null");
			}
		} catch (IOException ioException) {
			log.error("IOException while storing properties file to local disk"
					+ ioException.getMessage());
		}
		return tapasConfigurationProperties;
	}

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
			String propertyFileName = filePath + getPathSeperator()
					+ "tapas.properties";
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

	public static String refactorSlashInFilePaths(String filepath) {
		String finalPath = null;
		if ((null != filepath) && ("" != filepath)) {
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

	public static String[] getStringTokensForGivenURI(String path) {
		String[] stringTokens = null;
		if ((null != path) && (path.contains("\\"))) {
			stringTokens = path.split("\\\\");
		}
		if ((null != path) && (path.contains("/"))) {
			stringTokens = path.split("/");
		}
		return stringTokens;
	}

	public static String replaceBackSlashWithForwardSlash_ViceVersa(String path) {
		String replacedPath = path;
		log.info("Path before replacing slash: " + replacedPath);
		if ((null != replacedPath) && ("" != replacedPath)) {
			if (replacedPath.contains("\\")) {
				replacedPath = replacedPath.replace("\\", "/");
			} else {
				replacedPath = replacedPath.replace("/", "\\");
			}
		}
		log.info("New path after replacing slash: " + replacedPath);
		return replacedPath;
	}
}
